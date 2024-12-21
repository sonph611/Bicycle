package com.bikestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bikestore.model.Account;

import java.util.List;


public interface ProfileRepository extends JpaRepository<Account, Integer>{
	 Account findByUsername(String username);
}
