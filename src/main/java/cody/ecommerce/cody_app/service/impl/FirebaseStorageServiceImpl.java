package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.service.FirebaseStorageService;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {
    private String bucketName;
    private final Storage storage;

    public FirebaseStorageServiceImpl(Storage storage) {
        this.storage = storage;
        this.bucketName = FirebaseApp.getInstance().getOptions().getStorageBucket();
    }

    public String upload(MultipartFile file, String folder) {
        String objectName = "%s/%s-%s".formatted(
                folder == null ? "uploads" : folder,
                UUID.randomUUID(),
                Objects.requireNonNullElse(file.getOriginalFilename(), "file.bin"));

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName))
                .setContentType(file.getContentType())
                .build();
        try {
            // Simple one-shot upload; for large files use the write-channel shown below.
            storage.create(blobInfo, file.getBytes());

            // Return a signed GET URL (temporary) so the client can access it immediately:
            URL signedUrl = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
            return signedUrl.toString();
        } catch (IOException e) {
            Map<String, String> errors = Map.of("file", "Failed to upload file: " + e.getMessage());
            throw new BadRequestException("Failed to upload file to Firebase Storage", Error.build("Bad request", errors));
        }
    }

    // For very large files uploaded via your server, prefer a resumable channel:
    public String uploadStreaming(InputStream in, String contentType, String objectName) {
        BlobInfo info = BlobInfo.newBuilder(bucketName, objectName).setContentType(contentType).build();
        try (WriteChannel writer = storage.writer(info)) {
            in.transferTo(Channels.newOutputStream(writer));
        } catch (IOException e) {
            Map<String, String> errors = Map.of("file", "Failed to upload file: " + e.getMessage());
            throw new BadRequestException("Failed to upload file to Firebase Storage", Error.build("Bad request", errors));
        }
        return "gs://" + bucketName + "/" + objectName;
    }
}
