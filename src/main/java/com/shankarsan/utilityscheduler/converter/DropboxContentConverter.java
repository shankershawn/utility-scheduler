package com.shankarsan.utilityscheduler.converter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class DropboxContentConverter implements HttpMessageConverter<InputStream> {
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
    public InputStream read(Class<? extends InputStream> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        return inputMessage.getBody();
    }

    @Override
    public void write(InputStream file, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        //TODO No action needed
    }
}
