package com.tutopedia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tutopedia.model.Tutorial;

@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, Long>{
	List<Tutorial> findByPublished(boolean isPublished);
	List<Tutorial> findByTitleContaining(String keyword);
	List<Tutorial> findByDescriptionContaining(String keyword);
	
	@Modifying
	@Transactional
	@Query("update Tutorial t set t.published = :published where t.id = :id")
	void publishTutorial(@Param("published") boolean published, @Param("id") long id);

	@Modifying
	@Transactional
	@Query("update Tutorial t set t.published = true")
	void publishAllTutorials();
	
	Long deleteByPublished(boolean isPublished);
}