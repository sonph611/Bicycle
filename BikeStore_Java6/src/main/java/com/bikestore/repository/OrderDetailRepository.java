package com.bikestore.repository;

import java.util.List;

import org.eclipse.tags.shaded.org.apache.bcel.generic.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bikestore.model.OrderDetail;



public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

	@Query(value = "select b.id, b.order_date, d.address, b.total, c.nameStatus  from OrderDetails a inner join Orders b on a.order_id = b.id inner join States c on b.status_id = c.id inner join Addresses d on b.address_id = d.id where c.nameStatus NOT LIKE N'Chờ xác nhận' AND b.account_id = :iduser", nativeQuery = true)
	List<Object[]> findAllSQL(@Param("iduser") Integer iduser);

	
	
}
