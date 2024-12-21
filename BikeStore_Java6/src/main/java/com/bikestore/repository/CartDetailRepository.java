package com.bikestore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bikestore.model.Account;
import com.bikestore.model.CartDetail;
import com.bikestore.model.ProductDetail;

import jakarta.transaction.Transactional;


// CÓ THAY ĐỔI
public interface CartDetailRepository extends JpaRepository<CartDetail, Integer>{
	
	CartDetail findByAccountAndProductDetail(Account account, ProductDetail productDetail);
	
	@Modifying
    @Transactional
    @Query("UPDATE CartDetail c SET c.quantity =c.quantity+ :quantity WHERE c.account.id = :accountId AND c.productDetail.id = :productDetailId")
    int updateQuantityByAccountIdAndProductDetailId(int quantity, int accountId, int productDetailId);
	
	List<CartDetail> findByAccount(Account accountId);
	
	@Modifying
    @Transactional
    @Query(value = "UPDATE CartDetails SET quantity = :quantity WHERE id = :id", nativeQuery = true)
    int updateQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);
	
    @Query("SELECT cd FROM CartDetail cd WHERE cd.account.id = :accountId AND cd.id IN :ids")
    List<CartDetail> findByAccountIdAndIds(@Param("accountId") int accountId, @Param("ids") List<Integer> ids);
    @Query("SELECT cd FROM CartDetail cd WHERE cd.account.id = :accountId AND cd.id IN :ids")
    
    List<CartDetail> findByAccountIdAndIds(@Param("accountId") int accountId, @Param("ids") String[] ids);
    
    
    @Query("SELECT count(cd.id) FROM CartDetail cd WHERE cd.account.id = :accountId")
    Integer getTotalQuantityByAccountId(@Param("accountId") Long accountId);
}