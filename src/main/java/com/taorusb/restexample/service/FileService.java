package com.taorusb.restexample.service;

import com.taorusb.restexample.model.File;

import java.util.List;

public interface FileService extends GenericService<File, Long> {
    List<File> getByUserId(Long id);
    File getByDoubleId(Long userId, Long id);
}
