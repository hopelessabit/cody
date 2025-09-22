package cody.ecommerce.cody_app.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

/**
 * Service interface for Cloudinary file upload operations.
 */
public interface CloudinaryService {
    /**
     * Uploads a file to Cloudinary.
     * @param file the file to upload
     * @return upload result map from Cloudinary
     */
    String uploadFile(MultipartFile file);
}

