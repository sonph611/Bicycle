package com.bikestore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter
@NoArgsConstructor

public class ProductRevenueDTO {
    private String productName;
    private Double revenue;
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Double getRevenue() {
		return revenue;
	}
	public void setRevenue(Double revenue) {
		this.revenue = revenue;
	}
	public ProductRevenueDTO(String productName, Double revenue) {
		super();
		this.productName = productName;
		this.revenue = revenue;
	}
    
    
    
}

