package com.bikestore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bikestore.model.Category;



public interface CategoryRepository extends JpaRepository<Category, Integer>{
	List<Category> findByType(String type);
	
	 Page<Category> findAll(Pageable pageable);
	 
	 @Query(value = "select * from Categories where status = 0", nativeQuery = true)
		List<Category> findAllbyStatus();
}
