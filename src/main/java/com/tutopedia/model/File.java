package com.tutopedia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="files")
public class File {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "files_seq")
	@SequenceGenerator(name = "files_seq", allocationSize = 1)
	@Getter
	@Column(name = "id", updatable = false, nullable = false)	
	private long id;

	@Column(name = "tid")	
	@Getter
	@Setter	
	private long tutorialId;
	
	@Column(name = "type")	
	@Getter
	@Setter	
	private String type;
	
	@Lob
	@Getter
	@Setter	
	private byte[] tfile;
	
	public File() {
	}
	
	public File(Long tid, String type, byte[] data) {
		this.tutorialId = tid;
	    this.type = type;
	    this.tfile = data;
	  }
}
