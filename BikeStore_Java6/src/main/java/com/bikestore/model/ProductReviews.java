package com.bikestore.model;
import java.util.Date;

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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ProductReviews")
public class ProductReviews {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id;

	    @ManyToOne
	    @JoinColumn(name = "product_id", nullable = false)
	    private Product product;

	    @ManyToOne
	    @JoinColumn(name = "account_id", nullable = false)
	    private Account account;

	    @Column
	    private int rating;

	    @Column
	    private Date review_date;

	    @Column(columnDefinition = "TEXT")
	    private String review_text;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public Product getProduct() {
			return product;
		}

		public void setProduct(Product product) {
			this.product = product;
		}

		public Account getAccount() {
			return account;
		}

		public void setAccount(Account account) {
			this.account = account;
		}

		public int getRating() {
			return rating;
		}

		public void setRating(int rating) {
			this.rating = rating;
		}

		public Date getReview_date() {
			return review_date;
		}

		public void setReview_date(Date review_date) {
			this.review_date = review_date;
		}

		public String getReview_text() {
			return review_text;
		}

		public void setReview_text(String review_text) {
			this.review_text = review_text;
		}
	    
	    
}
