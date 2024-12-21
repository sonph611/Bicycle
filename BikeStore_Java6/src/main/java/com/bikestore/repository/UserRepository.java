package com.bikestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bikestore.model.Account;



public interface UserRepository extends JpaRepository<Account, Integer>{
	public Account findByUsername(String username);
}
