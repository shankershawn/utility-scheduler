package com.shankarsan.utilityscheduler.controller;

import com.shankarsan.dropbox.service.DropboxWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/dropbox")
@EnableCaching
public class DropboxWebhookController {

    private final DropboxWebhookService dropboxWebhookService;

    @GetMapping(value = "/availability/refresh")
    public ResponseEntity<String> registerWebhook(@RequestParam String challenge) {
        return getChallengeResponseEntity(challenge);
    }

    @PostMapping(value = "/availability/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> refreshAvailabilityFileData(@RequestBody Object requestBody) {
        dropboxWebhookService.refreshAvailabilityFileData();
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<String> getChallengeResponseEntity(String challenge) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header("X-Content-Type-Options", "nosniff")
                .body(challenge);
    }


}
