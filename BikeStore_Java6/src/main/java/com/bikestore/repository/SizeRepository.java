package com.bikestore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bikestore.model.Size;



public interface SizeRepository extends JpaRepository<Size, Integer> {
	@Query(value = "SELECT DISTINCT c.id , c.nameSize  FROM Sizes c JOIN ProductDetails pd ON c.id = pd.size_id WHERE pd.product_id = :productId", nativeQuery = true)
	List<Size> findSizeByProductSQL(@Param("productId") Integer productId);

	List<Size> findByNameSize(String nameSize);

	Page<Size> findAll(Pageable pageable);
	
	@Query(value = "select * from Sizes where status = 0", nativeQuery = true)
	List<Size> findAllByStatus();
	
	@Query(value  = "select id from Sizes", nativeQuery = true)
	public List<Integer> findIdSize();
	
	@Query("SELECT s.id FROM Size s")
    List<Integer> findAllIds();
}
