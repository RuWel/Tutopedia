package com.tutopedia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.tutopedia.message.ResponseMessage;
import com.tutopedia.model.File;
import com.tutopedia.model.TutorialFileInfo;
import com.tutopedia.service.FileStorageService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class FileController {
	@Autowired
	private FileStorageService fileStorageService;
	
	private String getLogCommand(String command) {
		Date currentDate = new Date();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentDateTime = dateFormat.format(currentDate);

		return "[" + currentDateTime + " : " + command + "]";
	}

	@PutMapping("/tutorials/files/{tid}")
	public ResponseEntity<ResponseMessage> uploadFileWithTid(@PathVariable("tid") Long tid, @ModelAttribute TutorialFileInfo tutorialFileInfo) {
		String message = "";
		
		System.out.println(getLogCommand("UploadFile for " + tid));

		try {

			System.out.println(getLogCommand("Upload File"));
			File storedFile = fileStorageService.store(tid, tutorialFileInfo.getTutorial());
			System.out.println(getLogCommand("Upload File:" ) + "RESULT: " + storedFile.getId());

			message = "file upload successfull (" + tutorialFileInfo.getTutorial().getOriginalFilename() + ")";
			System.out.println(getLogCommand("UploadFile: " + HttpStatus.OK.name()));
			return (new ResponseEntity<>(new ResponseMessage(message), HttpStatus.OK));
		} catch (Exception e) {
			message = "file upload failed (" + tutorialFileInfo.getTutorial().getOriginalFilename() + ")";
			System.out.println(getLogCommand("UploadFile: " + HttpStatus.EXPECTATION_FAILED.name()));
			return (new ResponseEntity<>(new ResponseMessage(message), HttpStatus.EXPECTATION_FAILED));
		}
	}

	@DeleteMapping("/tutorials/files/delete")
	public ResponseEntity<ResponseMessage> deleteAllFiles() {
		String message = "";
		
		try {
			System.out.println(getLogCommand("Delete All Files"));
			
			fileStorageService.deleteAll();
			return (new ResponseEntity<>(HttpStatus.NO_CONTENT));
		} catch (Exception e) {
			message = "all file deleting failed";
			System.out.println(getLogCommand("UploadFile: " + HttpStatus.EXPECTATION_FAILED.name()));
			return (new ResponseEntity<>(new ResponseMessage(message), HttpStatus.EXPECTATION_FAILED));
		}
	}
}
