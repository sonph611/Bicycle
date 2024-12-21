package com.bikestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bikestore.model.Account;
import com.bikestore.model.Address;




public interface AddressRepository extends JpaRepository<Address, Integer> {
	
public Address findByAccount(Account account);
	
	@Query(value = "select * from Addresses where account_id = :id", nativeQuery = true)
	public List<Address>  findBListyAccount(@Param("id") Integer account);
}
