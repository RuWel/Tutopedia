package com.tutopedia.model;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;

@Data
public class TutorialFileInfo {
	@NotEmpty
    @Getter
    private String title;

	@NotEmpty
    @Getter
    private String description;

	@NotEmpty
    @Getter
    private String published;

    @Getter
    private MultipartFile tutorial;
//  private String tutorial;
}
