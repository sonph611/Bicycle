package com.bikestore.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bikestore.model.Product;



public interface FilterRepository extends JpaRepository<Product, Integer>{
	public static String valueQuery = "SELECT a.id, a.name, a.category_id, c.type, a.photo, a.status, MIN(b.price) as price " +
		    "FROM Products a " +
		    "INNER JOIN ProductDetails b ON a.id = b.product_id " +
		    "INNER JOIN Categories c ON a.category_id = c.id " +
		    "WHERE a.category_id = :category " +
		    "AND a.name LIKE CONCAT('%', :keyWord, '%') " +
		    "AND b.price BETWEEN :minPrice AND :maxPrice " +
		    "AND b.color_id IN :colorIds " +
		    "AND b.size_id IN :sizeIds " +
		    "GROUP BY a.id, a.name, a.category_id, c.type, a.photo, a.status";

		@Query(value = valueQuery, nativeQuery = true)
		public Page<Object> findByCategory(
		    @Param("category") Integer category,
		    @Param("keyWord") String keyWord,
		    @Param("minPrice") double minPrice,
		    @Param("maxPrice") double maxPrice,
		    @Param("colorIds") List<Integer> colorIds,
		    @Param("sizeIds") List<Integer> sizeIds,
		    Pageable pageable
		);

	
//	 public static String valueQuery = "select a.id, a.name, a.category_id, c.type, a.photo, a.status, MIN(b.price) as price from Products a \r\n"
//				+ "inner join ProductDetails b on a.id = b.product_id\r\n"
//				+ "inner join Categories c on a.category_id = c.id\r\n"
//				+ "where a.category_id = :category and a.name like CONCAT('%', :keyWord, '%') "
//				+ "and ( price between :minPrice and :maxPrice )\r\n"
//				+ "and (b.color_id in :color \r\n"
//				+ "and b.size_id in :size )\r\n"
//				+ "GROUP BY a.id, a.name, a.category_id, c.type, a.photo, a.status"; 
//		@Query(value = valueQuery, nativeQuery =  true)
//		public Page<Object> findByCategoryInteger(@Param("category") Integer category, @Param("keyWord") String keyWord,  @Param("minPrice") double minPrice,  @Param("maxPrice") double maxPrice, @Param("color") Integer[] color, @Param("size") Integer[] size, Pageable pageable);
		
		
	
	 public static String valueQuery1 = "select a.id, a.name, a.category_id, c.type, a.photo, a.status, (b.price) as price from Products a inner join ProductDetails b  on a.id=b.product_id \r\n"
	 		+ "				inner join Categories c on c.id=a.category_id  where c.id=:category"; 
		@Query(value = valueQuery1, nativeQuery =  true)
		public Page<Object> findByCategory1(@Param("category") Integer category,Pageable pageable);
}
