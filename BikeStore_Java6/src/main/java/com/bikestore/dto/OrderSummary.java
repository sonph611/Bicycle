package com.bikestore.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data @Getter @Setter @NoArgsConstructor
public class OrderSummary {
	
    private int orderCount;
    private float totalRevenue;

    public OrderSummary(int orderCount, float totalRevenue) {
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
        
        
        
    }

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public float getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(float totalRevenue) {
		this.totalRevenue = totalRevenue;
	}
    
}



