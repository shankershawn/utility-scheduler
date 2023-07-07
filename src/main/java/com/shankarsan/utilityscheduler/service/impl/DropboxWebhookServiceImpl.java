package com.shankarsan.utilityscheduler.service.impl;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.service.DropboxService;
import com.shankarsan.utilityscheduler.service.DropboxWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DropboxWebhookServiceImpl implements DropboxWebhookService {

    private final CacheManager cacheManager;

    private final DropboxService dropboxService;

    @Override
    public File refreshAvailabilityFileData() {
        File availabilityFileData = dropboxService.downloadFile(CommonConstants.SEAT_AVAILABILITY_CSV);
        Optional.ofNullable(cacheManager)
                .map(e -> e.getCache(CommonConstants.DROPBOX_AVAILABILITY_FILE_CACHE))
                .ifPresent(cache -> cache.put(CommonConstants.SEAT_AVAILABILITY_FILE_DATA, availabilityFileData));
        return availabilityFileData;
    }
}
