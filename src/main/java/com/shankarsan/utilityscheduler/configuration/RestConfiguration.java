package com.shankarsan.utilityscheduler.configuration;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RestConfiguration {

    private final ApplicationConfiguration applicationConfiguration;

    @Bean
    public RestTemplate irctcRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(applicationConfiguration.getUrl(CommonConstants.IRCTC))
                .build();
    }

    @Bean
    public RestTemplate dropboxShortLivedTokenRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(applicationConfiguration.getUrl(CommonConstants.DROPBOX_API))
                .basicAuthentication(applicationConfiguration.getSecret(CommonConstants.DROPBOX_CLIENT_ID),
                        applicationConfiguration.getSecret(CommonConstants.DROPBOX_CLIENT_SECRET))
                .build();
    }

    @Bean
    public RestTemplate dropboxDownloadRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(applicationConfiguration.getUrl(CommonConstants.DROPBOX_CONTENT))
                .messageConverters(new DropboxContentConverter())
                .build();
    }

    static class DropboxContentConverter implements HttpMessageConverter<File> {
        @Override
        public boolean canRead(Class<?> clazz, MediaType mediaType) {
            return true;
        }

        @Override
        public boolean canWrite(Class<?> clazz, MediaType mediaType) {
            return false;
        }

        @Override
        public List<MediaType> getSupportedMediaTypes() {
            return List.of(MediaType.APPLICATION_OCTET_STREAM);
        }

        @Override
        public List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
            return HttpMessageConverter.super.getSupportedMediaTypes(clazz);
        }

        @Override
        public File read(Class<? extends File> clazz, HttpInputMessage inputMessage)
                throws IOException, HttpMessageNotReadableException {
            File tempFile = null;
            if (inputMessage instanceof ClientHttpResponse) {
                int bytesRead;
                byte[] byteArray = new byte[1024];
                tempFile = new File(CommonConstants.TEMP_PATH);
                try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                    while ((bytesRead = inputMessage.getBody().read(byteArray)) != -1) {
                        outputStream.write(byteArray, 0, bytesRead);
                    }
                }
            }
            return tempFile;
        }

        @Override
        public void write(File file, MediaType contentType, HttpOutputMessage outputMessage)
                throws IOException, HttpMessageNotWritableException {
            //TODO
        }
    }
}
