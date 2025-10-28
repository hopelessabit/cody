package cody.ecommerce.cody_app.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface FirebaseStorageService {

    String upload(MultipartFile file, String folder);

    String uploadStreaming(InputStream in, String contentType, String objectName);
}
