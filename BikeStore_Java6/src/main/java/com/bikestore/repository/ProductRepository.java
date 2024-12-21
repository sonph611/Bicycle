package com.bikestore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bikestore.model.Category;
import com.bikestore.model.PhotoProduct;
import com.bikestore.model.Product;
import com.bikestore.model.ProductAndPrice;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	@Query(value = "SELECT TOP 1 a.id, a.product_id,a.color_id,a.size_id,a.quantity,a.price FROM ProductDetails a WHERE product_id = :productId ORDER BY price ASC", nativeQuery = true)
	List<Product> findDetailByProductSQL(@Param("productId") Integer productId);

	@Query(value = "SELECT SUM(quantity) AS total_quantity FROM ProductDetails where product_id = :productId GROUP BY product_id", nativeQuery = true)
	List<Object> findCountByProductSQL(@Param("productId") Integer productId);

	@Query(value = "SELECT p.*, pd.price, pd.product_detail_id, c.type FROM Products p LEFT JOIN (SELECT product_id, MIN(price) AS price, MIN(id) AS product_detail_id FROM ProductDetails GROUP BY product_id) pd ON p.id = pd.product_id LEFT JOIN Categories c ON p.category_id = c.id WHERE pd.price IS NOT NULL and p.status NOT LIKE N'Ngưng bán' AND p.name LIKE N'%'+:key+'%' order by p.id DESC", nativeQuery = true)
	Page<Object[]> findAllWithPriceSQL(@Param("key") String key, Pageable pageable);

	@Query(value = "SELECT * FROM ( \n" + "    SELECT p.*, pd.price, pd.product_detail_id, c.type, \n"
			+ "           ROW_NUMBER() OVER (ORDER BY p.id DESC) AS row_num \n" + "    FROM Products p \n"
			+ "    LEFT JOIN ( \n" + "        SELECT product_id, MIN(price) AS price, MIN(id) AS product_detail_id \n"
			+ "        FROM ProductDetails \n" + "        GROUP BY product_id\n" + "    ) pd ON p.id = pd.product_id \n"
			+ "    LEFT JOIN Categories c ON p.category_id = c.id \n" + "    WHERE pd.price IS NOT NULL \n"
			+ "        AND p.status NOT LIKE N'Ngưng bán' \n" + "        AND p.name LIKE '%' + :key + '%' \n"
			+ ") AS numbered_results \n" + "WHERE row_num <= 8", nativeQuery = true)
	Page<Object[]> findTop8NewSQL(@Param("key") String key, Pageable pageable);

	@Query(value = "SELECT * FROM ( \n" + "    SELECT p.*, pd.price, pd.product_detail_id, c.type, \n"
			+ "           ROW_NUMBER() OVER (ORDER BY p.id DESC) AS row_num \n" + "    FROM Products p \n"
			+ "    LEFT JOIN ( \n" + "        SELECT product_id, MIN(price) AS price, MIN(id) AS product_detail_id \n"
			+ "        FROM ProductDetails \n" + "        GROUP BY product_id\n" + "    ) pd ON p.id = pd.product_id \n"
			+ "    LEFT JOIN Categories c ON p.category_id = c.id \n" + "    WHERE pd.price IS NOT NULL \n"
			+ "        AND p.status NOT LIKE N'Ngưng bán' \n" + "        AND p.name LIKE '%' + :key + '%' \n"
			+ ") AS numbered_results \n" + "WHERE row_num <= 8", nativeQuery = true)
	List<Product> findTop8NewSQL1(@Param("key") String key, Pageable pageable);

	@Query(value = "SELECT * FROM (" + "SELECT " + "    p.*, " + "    pd.price, " + "    pd.product_detail_id, "
			+ "    c.type, " + "    (SELECT SUM(od.quantity) " + "     FROM OrderDetails od "
			+ "     JOIN ProductDetails pd2 ON od.product_detail_id = pd2.id "
			+ "     WHERE pd2.product_id = p.id) AS total_quantity_sold, "
			+ "    ROW_NUMBER() OVER (ORDER BY (SELECT SUM(od.quantity) "
			+ "                                FROM OrderDetails od "
			+ "                                JOIN ProductDetails pd2 ON od.product_detail_id = pd2.id "
			+ "                                WHERE pd2.product_id = p.id) DESC) AS row_num " + "FROM "
			+ "    Products p " + "    LEFT JOIN ( " + "        SELECT " + "            product_id, "
			+ "            MIN(price) AS price, " + "            MIN(id) AS product_detail_id " + "        FROM "
			+ "            ProductDetails " + "        GROUP BY " + "            product_id "
			+ "    ) pd ON p.id = pd.product_id " + "    LEFT JOIN Categories c ON p.category_id = c.id " + "WHERE "
			+ "    pd.price IS NOT NULL " + "    AND p.status NOT LIKE N'Ngưng bán' "
			+ "    AND p.name LIKE N'%'+:key+'%'" + ") sub " + "WHERE row_num <= 8", nativeQuery = true)
	Page<Object[]> findTop8SaleSQL(@Param("key") String key, Pageable pageable);

	@Query(value = "SELECT image FROM PhotoProducts WHERE product_id = :productId", nativeQuery = true)
	List<String> findAllPhotoSQL(@Param("productId") Integer productId);

	@Query(value = "SELECT \r\n" + "    Subquery.id as Detail_id,\r\n" + "    product_id,\r\n" + "    color_id,\r\n"
			+ "    size_id,\r\n" + "    quantity,\r\n" + "    price\r\n" + "FROM (\r\n" + "    SELECT \r\n"
			+ "        id,\r\n" + "        product_id,\r\n" + "        color_id,\r\n" + "        size_id,\r\n"
			+ "        quantity,\r\n" + "        price,\r\n"
			+ "        ROW_NUMBER() OVER (PARTITION BY product_id ORDER BY price ASC) AS row_num\r\n" + "    FROM \r\n"
			+ "        ProductDetails\r\n" + "    WHERE \r\n" + "        product_id = :productId \r\n"
			+ ") AS Subquery\r\n" + "WHERE \r\n" + "    row_num = 1 ", nativeQuery = true)
	List<Object[]> findDetailSQL(@Param("productId") Integer productId);

	Page<Product> findAll(Pageable pageable);

	Page<Product> findByCategory(Category category, Pageable pageable);
}