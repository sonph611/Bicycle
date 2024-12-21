package com.bikestore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data @Getter @Setter  @NoArgsConstructor
public class OrderStatusCount {
    private String status;
    private Long count;

    public OrderStatusCount(String status, Long count) {
        this.status = status;
        this.count = count;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

    

}
