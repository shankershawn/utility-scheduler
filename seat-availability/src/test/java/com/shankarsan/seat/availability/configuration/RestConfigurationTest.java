package com.shankarsan.seat.availability.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestConfigurationTest {

  @Mock
  private ApplicationConfiguration applicationConfiguration;

  @InjectMocks
  private RestConfiguration restConfiguration;

  @Test
  void shouldCreateIrctcRestTemplate() {
    when(applicationConfiguration.getUrl(anyString())).thenReturn("http://www.irctc.co.in");
    RestTemplate irctcRestTemplate = restConfiguration.irctcRestTemplate();
    assertEquals(URI.create("http://www.irctc.co.in/"),
        irctcRestTemplate.getUriTemplateHandler().expand("/"));
  }

  @Test
  void shouldCreateConfirmTktRestTemplate() {
    when(applicationConfiguration.getUrl(anyString())).thenReturn("http://www.confirmtkt.co.in");
    RestTemplate confirmTktRestTemplate = restConfiguration.confirmTktRestTemplate();
    assertEquals(URI.create("http://www.confirmtkt.co.in/"),
        confirmTktRestTemplate.getUriTemplateHandler().expand("/"));
  }
}
