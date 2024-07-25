package com.tutopedia.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tutopedia.model.File;
import com.tutopedia.repository.FileRepository;

import jakarta.transaction.Transactional;

@Service
public class FileStorageService {
	@Autowired
	private FileRepository fileDBRepository;

	public File store(Long tid, MultipartFile file) throws IOException {
	    File fileDB = new File(tid, file.getContentType(), file.getBytes());

	    return fileDBRepository.save(fileDB);
	}
	
	@Transactional	// needed for LOB
	public File getFileByTutorialId(Long tid) {
	    return fileDBRepository.findByTutorialId(tid);
	}

	public void deleteAll() throws IOException {
		fileDBRepository.deleteAll();
		
	}

	@Transactional	// needed for LOB
	public File update(Long tid, MultipartFile file) throws IOException {
	    File fileDB = getFileByTutorialId(tid);
	    
	    fileDB.setTfile(file.getBytes());

	    return fileDBRepository.save(fileDB);
	}
}
