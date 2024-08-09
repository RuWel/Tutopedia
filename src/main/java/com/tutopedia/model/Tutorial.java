package com.tutopedia.model;

import org.hibernate.validator.constraints.Length;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Table(name="tutorials", schema="public")
public class Tutorial {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tutorials_seq")
	@SequenceGenerator(name = "tutorials_seq", allocationSize = 1)
	@Getter
	private long id;
	
	@Column(name = "title")
	@Getter @Setter 
	@NotEmpty(message = "error.title.empty")
	@Length(max = 255, message = "error.title.length")
	private String title;
	
	@Column(name = "description")
	@Getter @Setter
	@NotEmpty(message = "error.description.empty")
	@Length(max = 255, message = "error.description.length")
	private String description;
	
	@Column(name = "published")
	@Getter @Setter
	private boolean published = false;

	@Column(name = "filename")
	@Getter @Setter
	private String filename;

	public Tutorial(String title, String description, boolean published, String filename) {
		this.title = title;
		this.description = description;
		this.published = published;
		this.filename = filename;
	}

	@Override
	public String toString() {
		return "Tutorial [id=" + id + ", title=" + title + ", desc=" + description + ", published=" + published + ", filename=" + filename + "]";
	}
}
