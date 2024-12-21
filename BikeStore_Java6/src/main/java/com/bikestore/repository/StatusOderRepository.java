package com.bikestore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bikestore.model.Status;



public interface StatusOderRepository extends JpaRepository<Status, Integer> {
	@Query(value = "SELECT * FROM States Where id = : id", nativeQuery = true)
	public Status findStatusById(@Param("id") Integer id);
}
