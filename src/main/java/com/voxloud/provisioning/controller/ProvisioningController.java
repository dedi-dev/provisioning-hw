package com.voxloud.provisioning.controller;

import com.voxloud.provisioning.service.ProvisioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProvisioningController {

    private final ProvisioningService provisioningService;

    @GetMapping("/provisioning/{mac}")
    public ResponseEntity<String> getProvisioning(@PathVariable("mac") String mac) {
        try {
            String config = provisioningService.getProvisioningFile(mac);
            return ResponseEntity.ok(config);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Device not found or unsupported");
        }
    }
}
