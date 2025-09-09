package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.TrackByDTO;
import cody.ecommerce.cody_app.dto.request.trackby.CreateTrackByRequest;
import cody.ecommerce.cody_app.dto.request.trackby.UpdateTrackByRequest;
import cody.ecommerce.cody_app.service.TrackByService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/track-by")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class TrackByController {
    private final TrackByService trackByService;

    @GetMapping
    public ResponseEntity<ResponseData<List<TrackByDTO>>> getAll() {
        return ResponseUtil.getResponse(trackByService::getAll, "TrackBy list retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<TrackByDTO>> getById(@PathVariable String id) {
        return ResponseUtil.getResponse(() -> trackByService.getById(id), "TrackBy retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ResponseData<TrackByDTO>> create(@RequestBody CreateTrackByRequest request) {
        return ResponseUtil.getResponse(() -> trackByService.create(request), "TrackBy created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData<TrackByDTO>> update(@PathVariable String id, @RequestBody UpdateTrackByRequest request) {
        return ResponseUtil.getResponse(() -> trackByService.update(id, request), "TrackBy updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<Void>> delete(@PathVariable String id) {
        return ResponseUtil.getResponse(() -> { trackByService.delete(id); return null; }, "TrackBy deleted successfully");
    }

    @PostMapping("/admin/scan-insert")
    public ResponseEntity<ResponseData<List<TrackByDTO>>> scanAndInsertTrackBy() {
        return ResponseUtil.getResponse(trackByService::scanAndInsertTrackBy, "TrackBy scan and insert completed");
    }
}
