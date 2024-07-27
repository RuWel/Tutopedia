package com.tutopedia.model;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class TutorialFileData {
	@NotEmpty
    @Getter
    private String filename;

    @Getter
    private byte[] tutorial;
//  private String tutorial;
}
