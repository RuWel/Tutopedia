package com.tutopedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutopedia.model.File;

@Repository
public interface FileRepository extends JpaRepository<File, Long>{
	File findByTutorialId(long tid);
}
