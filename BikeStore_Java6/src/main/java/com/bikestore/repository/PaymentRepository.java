package com.bikestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bikestore.model.Payments;



public interface PaymentRepository extends JpaRepository<Payments, Integer>{
	
}
