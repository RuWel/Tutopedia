package com.tutopedia.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tutopedia.error.CustomError;
import com.tutopedia.model.File;
import com.tutopedia.model.Tutorial;
import com.tutopedia.model.TutorialFileInfo;
import com.tutopedia.repository.TutorialRepository;
import com.tutopedia.service.FilePublishService;
import com.tutopedia.service.FileStorageService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class TutorialController {

	@Autowired
	TutorialRepository tutorialRepository;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private FilePublishService filePublishService;

	private String getLogCommand(String command) {
		Date currentDate = new Date();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentDateTime = dateFormat.format(currentDate);

		return "[" + currentDateTime + " : " + command + "]";
	}
	
	@GetMapping("/heartbeat")
	public String heartBeat() {
		System.out.println(getLogCommand("HeartBeat"));
		
		return ("Hello ... This is a test for springboot app");	
	}
	
	@PostMapping("/tutorials")
	public ResponseEntity<Tutorial> createTutorial(@ModelAttribute TutorialFileInfo tutorialFileInfo) {
		try {
			@SuppressWarnings("null")
			String fileName = StringUtils.cleanPath(tutorialFileInfo.getTutorial().getOriginalFilename());

			Tutorial result = tutorialRepository.save(new Tutorial(
					tutorialFileInfo.getTitle(), 
					tutorialFileInfo.getDescription(), 
					Boolean.parseBoolean(tutorialFileInfo.getPublished()),
					fileName));
			System.out.println(getLogCommand("CreateTutorial: ") + "RESULT: " + result.getId());

			System.out.println(getLogCommand("Upload File"));
			File storedFile = fileStorageService.store(result.getId(), tutorialFileInfo.getTutorial());
			System.out.println(getLogCommand("Upload File:" ) + "RESULT: " + storedFile.getId());

			return (new ResponseEntity<>(result, HttpStatus.CREATED));
			
		} catch (Exception e) {
			return (new ResponseEntity<>(new CustomError("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}
	
	@GetMapping("/tutorials")
	public ResponseEntity<List<Tutorial>> getAllTutorials() {
		List<Tutorial> result = new ArrayList<>();

		System.out.println(getLogCommand("GetAllTutorials"));
		
		tutorialRepository.findAll().forEach(result::add);
		
		System.out.println(getLogCommand("GetAllTutorials: RESULT: " + result.size()));

		Collections.sort(result, Comparator.comparingLong(Tutorial::getId));
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@PutMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") Long id, @Valid @RequestBody Tutorial tutorial) {
		try {
			Optional<Tutorial> result = tutorialRepository.findById(id);

			System.out.println(getLogCommand("UpdateTutorial: " + id));

			if (result.isPresent()) {
				Tutorial data = result.get();
				
				data.setTitle(tutorial.getTitle());
				data.setDescription(tutorial.getDescription());
				data.setPublished(tutorial.isPublished());
	
				Tutorial newEntity = tutorialRepository.save(data);
				
				return new ResponseEntity<>(newEntity, HttpStatus.OK);
			}
			
			return (new ResponseEntity<>(new CustomError("Tutorial with id " + id + " not found"), HttpStatus.NOT_FOUND));
		} catch (Exception e) {
			return (new ResponseEntity<>(new CustomError("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@SuppressWarnings("null")
	@PutMapping("/tutorials/file/{id}")
	public ResponseEntity<Tutorial> updateTutorialWithFile(@PathVariable("id") Long id, @ModelAttribute TutorialFileInfo tutorialFileInfo) {
		try {
			Optional<Tutorial> result = tutorialRepository.findById(id);

			System.out.println(getLogCommand("UpdateTutorial: " + id));

			if (result.isPresent()) {
				Tutorial data = result.get();
				
				data.setTitle(tutorialFileInfo.getTitle());
				data.setDescription(tutorialFileInfo.getDescription());
				data.setPublished(Boolean.parseBoolean(tutorialFileInfo.getPublished()));
				data.setFilename(StringUtils.cleanPath(tutorialFileInfo.getTutorial().getOriginalFilename()));
				
				Tutorial newEntity = tutorialRepository.save(data);

				System.out.println(getLogCommand("Update File"));
				File storedFile = fileStorageService.update(id, tutorialFileInfo.getTutorial());
				System.out.println(getLogCommand("UpDate File:" ) + "RESULT: " + storedFile.getId());
				
				return new ResponseEntity<>(newEntity, HttpStatus.OK);
			}
			
			return (new ResponseEntity<>(new CustomError("Tutorial with id " + id + " not found"), HttpStatus.NOT_FOUND));
		} catch (Exception e) {
			return (new ResponseEntity<>(new CustomError("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@DeleteMapping("/tutorials/delete/{id}")
	public ResponseEntity<Tutorial> deleteTutorial(@PathVariable("id") Long id) {
		try {
			System.out.println(getLogCommand("DeleteTutorial: " + id));
			
			tutorialRepository.deleteById(id);
			return (new ResponseEntity<>(HttpStatus.NO_CONTENT));
		} catch (Exception e) {
			return (new ResponseEntity<>(new CustomError("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}
	
	// delete all tutorials
	@DeleteMapping("/tutorials/delete")
	@Transactional
	public ResponseEntity<Tutorial> deleteAllTutorials() {
		try {
			System.out.println(getLogCommand("Delete All Non-Published Tutorials"));
	
//			tutorialRepository.deleteAll();
			Long deleted = tutorialRepository.deleteByPublished(false);
			System.out.println("Deleted " + deleted + " entitites");
			return (new ResponseEntity<>(HttpStatus.OK));
		} catch (Exception e) {
			System.out.println("Deleted Exception " + e.getMessage());
			return (new ResponseEntity<>(new CustomError("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@GetMapping("/tutorials/find/{id}")
	public ResponseEntity<Tutorial> findTutorialByID (@PathVariable("id") Long id) {
		try {
			System.out.println(getLogCommand("Find Tutorial: " + id));
			
			Optional<Tutorial> result = tutorialRepository.findById(id);
			
			if (result.isPresent()) {
				System.out.println(getLogCommand("Found Tutorial: " + id + ": " + result.get().getTitle()));
				return (new ResponseEntity<>(result.get(), HttpStatus.OK));
			}
			
			System.out.println(getLogCommand("Not Found Tutorial: " + id));
			return (new ResponseEntity<>(null, HttpStatus.OK));
		} catch (Exception e) {
			System.out.println(getLogCommand("Exception in Find Tutorial: " + id));
			return (new ResponseEntity<>(new CustomError("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@GetMapping("/tutorials/find")
	public ResponseEntity<List<Tutorial>> findAllTutorials (@RequestParam(required = false) Boolean published) {
		try {
			List<Tutorial> result;
			
			System.out.println(getLogCommand("Find All Tutorials: " + (published?"(PUBLISHED)":"(UNPUBLISHED)")));
			
			if (published == null) {
				result = tutorialRepository.findAll();
			} else {
				result = tutorialRepository.findByPublished(published);
			}
			
			return (new ResponseEntity<>(result, HttpStatus.OK));
		} catch (Exception e) {
			List<Tutorial> errors = new ArrayList<Tutorial>();
			
			errors.add(new CustomError("Internal server error"));
			
			return (new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@GetMapping("/tutorials/find/keywords")
	public ResponseEntity<List<Tutorial>> findTutorialsByKeywords (@RequestParam(required = true) Map<String,String> keywords) {
		try {
			System.out.println(getLogCommand("Find Tutorials With Keywords"));
			
			List<Tutorial> result = new ArrayList<Tutorial>();
			
			Set<String> keySet = keywords.keySet();
			for (String key : keySet) {
				List<Tutorial> lst = new ArrayList<Tutorial>();
				
				lst = tutorialRepository.findByTitleContaining(keywords.get(key));
				for (Tutorial tutorial : lst) {
					if (! result.contains(tutorial)) {
						result.add(tutorial);
					}
				}

				lst = tutorialRepository.findByDescriptionContaining(keywords.get(key));
				for (Tutorial tutorial : lst) {
					if (! result.contains(tutorial)) {
						result.add(tutorial);
					}
				}
			}
			
			Collections.sort(result, Comparator.comparingLong(Tutorial::getId));
			
			return (new ResponseEntity<>(result, HttpStatus.OK));
		} catch (Exception e) {
			List<Tutorial> errors = new ArrayList<Tutorial>();
			
			errors.add(new CustomError("Internal server error"));
			
			return (new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}

	@PutMapping("/tutorials/publish/{id}")
	public ResponseEntity<Tutorial> publishTutorial (@PathVariable("id") Long id) {
		try {
			System.out.println(getLogCommand("Publish Tutorial: " + id));
			
			tutorialRepository.publishTutorial(true, id);
			
			filePublishService.publishFile(id);
			
			return (new ResponseEntity<>(null, HttpStatus.OK));
		} catch (Exception e) {
			System.out.println(getLogCommand("Publish Tutorial Exception: " + e));
			return (new ResponseEntity<>(new CustomError("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}
	
	@PutMapping("/tutorials/publish")
	public ResponseEntity<Tutorial> publishAllTutorials () {
		try {
			System.out.println(getLogCommand("Publish All Tutorials"));
			
			List<Tutorial> unpublished = tutorialRepository.findByPublished(false);
			
			tutorialRepository.publishAllTutorials();
			
			for (Tutorial tutorial : unpublished) {
				filePublishService.publishFile(tutorial.getId());
			}
			
			return (new ResponseEntity<>(null, HttpStatus.OK));
		} catch (Exception e) {
			System.out.println(getLogCommand("Publish All Tutorials Exception: " + e));
			return (new ResponseEntity<>(new CustomError("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR));
		}
	}
}
