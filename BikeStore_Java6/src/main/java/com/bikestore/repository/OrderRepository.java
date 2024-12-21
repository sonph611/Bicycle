package com.bikestore.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bikestore.dto.OrderStatusCount;
import com.bikestore.dto.ProductRevenueDTO;
import com.bikestore.model.Orders;

import jakarta.persistence.Tuple;

public interface OrderRepository extends JpaRepository<Orders, Integer> {
	@Query("SELECT new com.bikestore.dto.OrderStatusCount(o.status.nameStatus, COUNT(o.id)) " + "FROM Orders o "
			+ "GROUP BY o.status.nameStatus")
	List<OrderStatusCount> countOrdersByStatus();

	@Query("SELECT COUNT(o) as orderCount, COALESCE(SUM(o.total), 0) as totalRevenue " +
		       "FROM Orders o " +
		       "WHERE o.order_date = :date ") 
		List<Tuple> getSummaryByDate(@Param("date") Date date);
	@Query(value = "SELECT COUNT(*) AS orderCount, COALESCE(SUM(total), 0) AS totalRevenue " +
            "FROM Orders " +
            "WHERE CONVERT(DATE, order_date) = DATEADD(DAY, -1, CONVERT(DATE, GETDATE())) " +
            "", nativeQuery = true)
List<Tuple> getSummaryByYesterday();
		@Query("SELECT COUNT(o) as orderCount, COALESCE(SUM(o.total), 0) as totalRevenue " +
		       "FROM Orders o " +
		       "WHERE MONTH(o.order_date) = :month") 
		List<Tuple> getSummaryByMonth(@Param("month") Integer month);

		@Query("SELECT COUNT(o) as orderCount, COALESCE(SUM(o.total), 0) as totalRevenue " +
		       "FROM Orders o " +	
		       "WHERE YEAR(o.order_date) = :year") 
		List<Tuple> getSummaryByYear(@Param("year") Integer year);

	@Query("SELECT NEW com.bikestore.dto.ProductRevenueDTO(od.productDetail.product.name, SUM(od.quantity * od.price)) "
			+ "FROM OrderDetail od " + "GROUP BY od.productDetail.product.name "
			+ "ORDER BY SUM(od.quantity * od.price) DESC")
	List<ProductRevenueDTO> getTopProductRevenues(Pageable pageable);

	

	@Query(value = "select b.id, b.order_date, d.address, b.total, c.nameStatus  from OrderDetails a inner join Orders b on a.order_id = b.id inner join States c on b.status_id = c.id inner join Addresses d on b.address_id = d.id where c.nameStatus  LIKE N'Chờ xác nhận' AND b.account_id = :iduser", nativeQuery = true)
	List<Object[]> findAllSQL2(@Param("iduser")Integer iduser);

	@Query(value = "select a.id,a.order_date,c.full_name,a.total,d.namePayment,c.phone,e.nameStatus from Orders a join  Accounts b on a.account_id = b.id join Addresses c on a.address_id = c.id join Payments d on a.payment_id = d.id join States e on a.status_id = e.id where e.nameStatus like N'%'+:status+'%'", nativeQuery = true)
	Page<Object[]> findAllOrderByStatusSQL(Pageable pageable, @Param("status") String status);

	@Query(value = "select a.id,a.order_date,c.full_name,a.total,d.namePayment,c.phone,e.nameStatus from Orders a join  Accounts b on a.account_id = b.id join Addresses c on a.address_id = c.id join Payments d on a.payment_id = d.id join States e on a.status_id = e.id where a.id like N'%'+:key+'%'", nativeQuery = true)
	Page<Object[]> findAllOrderByStatusSQL2(Pageable pageable, @Param("key") String key);

	@Query(value = "SELECT p.id, p.name,cl.nameColor,s.nameSize , pd.price,od.quantity,pd.id FROM OrderDetails od JOIN ProductDetails pd ON od.product_detail_id = pd.id JOIN Products p ON pd.product_id = p.id JOIN Colorful cl ON pd.color_id = cl.id JOIN Sizes s ON pd.size_id = s.id WHERE od.order_id = :orderId", nativeQuery = true)
	List<Object[]> findAllProductByOrderIdSQL(@Param("orderId") Integer orderId);

	@Query(value = "SELECT o.order_date ,s.nameStatus, ad.full_name , p.namePayment , ad.full_address , ad.phone, o.note , o.total,o.id,o.reason FROM Orders o JOIN Accounts a ON o.account_id = a.id JOIN Addresses ad ON o.address_id = ad.id JOIN Payments p ON o.payment_id = p.id JOIN States s ON o.status_id = s.id WHERE o.id = :orderId", nativeQuery = true)
	List<Object[]> findOrderDetailSQL(@Param("orderId") Integer orderId);
	
	@Query("SELECT o FROM Orders o WHERE o.order_date <= :sevenDaysAgo AND o.status.id != 6 AND o.status.id = 5") 
	List<Orders> findOrdersToBeCompleted(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
}
