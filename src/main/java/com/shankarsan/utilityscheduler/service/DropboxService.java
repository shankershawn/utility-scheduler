package com.shankarsan.utilityscheduler.service;

import java.io.InputStream;

public interface DropboxService {

    InputStream downloadFile(String path);
}
