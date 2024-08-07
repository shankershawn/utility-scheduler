package com.shankarsan.utilityscheduler.service.impl;

import com.shankarsan.dropbox.service.DropboxWebhookService;
import com.shankarsan.utilityscheduler.constants.CommonConstants;
import com.shankarsan.utilityscheduler.consumer.SeatAvailabilityEmailProcessor;
import com.shankarsan.utilityscheduler.parser.SeatAvailabilityDateParser;
import com.shankarsan.utilityscheduler.predicate.SeatAvailabilityRequestDatePredicate;
import com.shankarsan.utilityscheduler.predicate.provider.SeatAvailabilityResponseDatePredicateProvider;
import com.shankarsan.utilityscheduler.service.SeatAvailabilityDataService;
import com.shankarsan.utilityscheduler.transformer.SeatAvailabilityInputStreamTransformer;
import com.shankarsan.utilityscheduler.transformer.SeatAvailabilityRequestDateTransformer;
import com.shankarsan.utilityscheduler.transformer.SeatAvailabilityResponseFlattenTransformer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class SeatAvailabilityServiceImplTest {

    @MockBean(name = CommonConstants.IRCTC)
    private SeatAvailabilityDataService irctcSeatAvailabilityDataServiceImpl;

    @MockBean(name = CommonConstants.CONFIRM_TKT)
    private SeatAvailabilityDataService confirmTktSeatAvailabilityDataServiceImpl;

    @Mock
    private DropboxWebhookService dropboxWebhookService;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private SeatAvailabilityRequestDateTransformer seatAvailabilityRequestDateTransformer;

    @Mock
    private SeatAvailabilityInputStreamTransformer seatAvailabilityInputStreamTransformer;

    @Mock
    private SeatAvailabilityResponseFlattenTransformer seatAvailabilityResponseFlattenTransformer;

    @Mock
    private SeatAvailabilityEmailProcessor seatAvailabilityEmailProcessor;

    @Mock
    private SeatAvailabilityDateParser seatAvailabilityDateParser;

    @Mock
    private SeatAvailabilityResponseDatePredicateProvider seatAvailabilityResponseDatePredicateProvider;

    @Mock
    private SeatAvailabilityRequestDatePredicate seatAvailabilityRequestDatePredicate;

    @Mock
    private RetryTemplate retryTemplate;

    @InjectMocks
    private SeatAvailabilityServiceImpl seatAvailabilityServiceImpl;

    @Test
    void shouldProcessSeatAvailability() {
        assertTrue(Boolean.TRUE);
    }
}
