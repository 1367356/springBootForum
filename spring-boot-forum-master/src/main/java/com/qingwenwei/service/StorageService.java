package com.qingwenwei.service;

import com.qingwenwei.persistence.model.User;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	
	void init();
	
	User store(MultipartFile file, String path);
	
	void deleteAll();
	
}