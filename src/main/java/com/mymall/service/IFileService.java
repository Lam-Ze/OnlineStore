package com.mymall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by lamZe on 2017/11/22.<br>
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
