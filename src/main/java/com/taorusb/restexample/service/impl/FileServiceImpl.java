package com.taorusb.restexample.service.impl;

import com.taorusb.restexample.model.File;
import com.taorusb.restexample.repository.FileRepository;
import com.taorusb.restexample.service.FileService;

import java.util.List;

public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public File getById(Long id) {
        return fileRepository.getById(id);
    }

    @Override
    public File update(File file) {
        return fileRepository.update(file);
    }

    @Override
    public File save(File file) {
        return fileRepository.save(file);
    }

    @Override
    public void delete(Long id) {
        fileRepository.deleteById(id);
    }

    @Override
    public List<File> getAll() {
        return fileRepository.findAll();
    }

    @Override
    public List<File> getByUserId(Long id) {
        return fileRepository.findByUserId(id);
    }

    @Override
    public File getByUserId(Long userId, Long id) {
        return fileRepository.findByDoubleId(userId, id);
    }
}