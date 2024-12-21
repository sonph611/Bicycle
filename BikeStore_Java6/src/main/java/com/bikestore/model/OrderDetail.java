package com.bikestore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
//@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OrderDetails")
public class OrderDetail {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id;
	    
	    public OrderDetail() {
	    	
	    }
	    
	    public OrderDetail(Orders o,ProductDetail p,int quantity) {
	    	this.order=o;
	    	this.productDetail=p;
	    	this.quantity=quantity;
	    	this.price=this.productDetail.getPrice();
	    }

	    public OrderDetail(int o,ProductDetail p,int quantity) {
	    	this.id=o;
	    	this.productDetail=p;
	    	this.quantity=quantity;
	    	this.price=this.productDetail.getPrice();
	    }
	    @ManyToOne
	    @JoinColumn(name = "order_id", nullable = false)
	    private Orders order;

	    @ManyToOne
	    @JoinColumn(name = "product_detail_id", nullable = false)
	    private ProductDetail productDetail;

	    @Column
	    private int quantity;

	    @Column
	    private double price;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public Orders getOrder() {
			return order;
		}

		public void setOrder(Orders order) {
			this.order = order;
		}

		public ProductDetail getProductDetail() {
			return productDetail;
		}

		public void setProductDetail(ProductDetail productDetail) {
			this.productDetail = productDetail;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		public double getPrice() {
			return price;
		}

		public void setPrice(double price) {
			this.price = price;
		}
    
    
}
