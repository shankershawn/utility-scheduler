package com.shankarsan.utilityscheduler.controller;

import com.shankarsan.dropbox.service.DropboxWebhookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DropboxWebhookControllerTest {

    @Mock
    private DropboxWebhookService dropboxWebhookService;

    @InjectMocks
    private DropboxWebhookController dropboxWebhookController;


    @Test
    void shouldRegisterWebhook() {
        ResponseEntity<String> responseEntity = dropboxWebhookController.registerWebhook("someChallenge");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("someChallenge", responseEntity.getBody());
        assertEquals("nosniff", Optional
                .ofNullable(responseEntity
                        .getHeaders()
                        .get("X-Content-Type-Options"))
                .orElseThrow(() -> new IllegalStateException("null header list")).get(0));
        assertEquals(MediaType.TEXT_PLAIN, responseEntity.getHeaders().getContentType());
    }

    @Test
    void shouldRefreshAvailabilityFileData() {
        ResponseEntity<Void> responseEntity = dropboxWebhookController.refreshAvailabilityFileData("");
        verify(dropboxWebhookService, times(1)).refreshAvailabilityFileData();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
