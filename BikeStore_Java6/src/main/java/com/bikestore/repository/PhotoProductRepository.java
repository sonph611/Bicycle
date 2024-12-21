package com.bikestore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bikestore.model.PhotoProduct;



public interface PhotoProductRepository extends JpaRepository<PhotoProduct, Integer>{
	@Query(value = "SELECT * FROM PhotoProducts WHERE product_id = :productId", nativeQuery = true)
	Page<PhotoProduct> findAllPhotoSQL(Pageable pageable,@Param("productId") Integer productId);
}
