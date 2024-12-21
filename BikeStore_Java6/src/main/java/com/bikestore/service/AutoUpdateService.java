package com.bikestore.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bikestore.model.Orders;
import com.bikestore.model.Status;
import com.bikestore.repository.OrderRepository;
import com.bikestore.repository.StatusOderRepository;



@Service
public class AutoUpdateService {
	  @Autowired
	    private OrderRepository orderRepository;

	    @Autowired
	    private StatusOderRepository statusOrderRepository;

	    // Scheduled to run at midnight every day
	    @Scheduled(cron = "0 0 0 * * ?") // @Scheduled(fixedRate = 60000)
	    public void updateOrderStatusToCompleted() {
	        LocalDateTime now = LocalDateTime.now();
	        LocalDateTime sevenDaysAgo = now.minus(7, ChronoUnit.DAYS);
	     //   LocalDateTime sevenDaysAgo = now.minus(10, ChronoUnit.SECONDS);
	        
	        // Fetch orders that were created 7 days ago and are not yet completed
	        List<Orders> ordersToUpdate = orderRepository.findOrdersToBeCompleted(sevenDaysAgo);
	        
	        Status completedStatus = statusOrderRepository.findById(6).orElseThrow(() -> new RuntimeException("Status not found"));
	        
	        for (Orders order : ordersToUpdate) {
	            order.setStatus(completedStatus);
	            orderRepository.saveAndFlush(order);
	        }
	    }
}
