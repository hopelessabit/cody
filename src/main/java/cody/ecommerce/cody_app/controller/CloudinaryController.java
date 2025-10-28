package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.service.CloudinaryService;
import cody.ecommerce.cody_app.service.FirebaseStorageService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import com.google.firebase.internal.FirebaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cloudinary")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class CloudinaryController {
    private final FirebaseStorageService firebaseService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseData<String>> uploadFile(
            @Parameter(description = "File to upload", required = true)
            @RequestParam("file") MultipartFile file) {
        return ResponseUtil.getResponse(() -> firebaseService.upload(file, null), "File uploaded successfully");
    }
}

