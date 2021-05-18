package com.taorusb.restexample.repository;

import com.taorusb.restexample.model.File;

import java.util.List;

public interface FileRepository extends GenericRepository<File, Long> {
    List<File> findByUserId(Long id);
    File findByDoubleId(Long userId, Long id);
}
