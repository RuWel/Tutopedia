package com.tutopedia.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tutopedia.model.File;
import com.tutopedia.model.Tutorial;
import com.tutopedia.model.TutorialFileData;

@Service
public class FilePublishService {
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private EmailService emailService;
	
	public void publishFile(Tutorial tutorial) throws Exception {
		System.out.println("Publish File Service called");
		File file = fileStorageService.getFileByTutorialId(tutorial.getId());
		
		TutorialFileData fileData = new TutorialFileData(tutorial.getFilename(), file.getTfile());
		
/*		emailService.setConnectionParams("smtp.gmail.com", 587, "rudi.welter@gmail.com", "oqkb gkrl jifw gzmf");
		emailService.sendEmail(
				"rudi.welter@gmail.com", 
				"rudi.welter@gmail.com", 
				tutorial.getTitle(),
				fileData); */
	}

	public void publishFiles(List<Tutorial> tutorials) throws Exception {
		Map<String, TutorialFileData> filesToSend = new HashMap<String, TutorialFileData>();
		
		for (Tutorial tutorial : tutorials) {
			File file = fileStorageService.getFileByTutorialId(tutorial.getId());
			
			filesToSend.put(
					tutorial.getTitle(), 
					new TutorialFileData(tutorial.getFilename(), file.getTfile()));
		}
		
/*		emailService.setConnectionParams("smtp.gmail.com", 587, "rudi.welter@gmail.com", "oqkb gkrl jifw gzmf");
		emailService.sendEmail(
				"rudi.welter@gmail.com", 
				"rudi.welter@gmail.com", 
				filesToSend); */
	}
}
