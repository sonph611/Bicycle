package com.bikestore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bikestore.model.Color;



public interface ColorRepository extends JpaRepository<Color, Integer> {
//	@Query(value = "SELECT DISTINCT * \r\n"
//			+ "FROM Colorful a\r\n"
//			+ "JOIN ProductDetails c ON c.color_id = a.id\r\n"
//			+ "WHERE c.product_id = :a ", nativeQuery = true)
//	public List<Color> findColorByProductSQL(@Param("a")Integer id);

	@Query(value = "SELECT DISTINCT c.id , c.nameColor , pd.imagedetail  FROM Colorful c JOIN ProductDetails pd ON c.id = pd.color_id WHERE pd.product_id = :productId", nativeQuery = true)
	List<Object[]> findColorByProductSQL(@Param("productId") Integer productId);

	List<Color> findByNameColor(String nameColor);

	Page<Color> findAll(Pageable pageable);
	
	@Query(value = "select * from Colorful where status = 0", nativeQuery = true)
	List<Color>findAllByStatus();
	
	@Query(value = "select id from Colorful", nativeQuery = true)
	public List<Integer> findIdColor();

	@Query("SELECT c.id FROM Color c ")
	   List<Integer> findIdsByIds();
}
