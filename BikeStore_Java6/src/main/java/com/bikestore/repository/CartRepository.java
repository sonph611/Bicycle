package com.bikestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bikestore.model.CartDetail;



public interface CartRepository extends JpaRepository<CartDetail, Integer> {
	
	@Query(value = "SELECT * FROM CartDetails WHERE account_id = :id", nativeQuery = true)
	public List<CartDetail> findProduct(@Param("id") Integer id);
	@Query(value = "SELECT  * FROM CartDetails WHERE account_id = :id and product_detail_id = :id2", nativeQuery = true)
	public CartDetail  findByAccountAndProductDetail(@Param("id") Integer idAccount, @Param("id2")Integer idProduct);
	
	//
	
}
