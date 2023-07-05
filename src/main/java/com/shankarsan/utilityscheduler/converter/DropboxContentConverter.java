package com.shankarsan.utilityscheduler.converter;

import com.shankarsan.utilityscheduler.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Component
@Slf4j
public class DropboxContentConverter implements HttpMessageConverter<File> {
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
        int bytesRead;
        byte[] byteArray = new byte[1024];
        File tempFile = new File(CommonConstants.TEMP_PATH);
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            while ((bytesRead = inputMessage.getBody().read(byteArray)) != -1) {
                outputStream.write(byteArray, 0, bytesRead);
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
