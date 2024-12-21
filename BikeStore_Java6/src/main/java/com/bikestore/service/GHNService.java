package com.bikestore.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GHNService {
	 @Value("${ghn.api.token}")
	    private String ghnApiToken;

	    @Value("${ghn.api.shopId}")
	    private String shopId;
	    
	    private final RestTemplate restTemplate = new RestTemplate();

	    private HttpHeaders createHeaders() {
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Token", ghnApiToken);
	        headers.set("Content-Type", "application/json");
	        headers.set("ShopId", shopId);
	        return headers;
	    }

	    public ResponseEntity<String> calculateShippingFee(String requestData) {
	        String url = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
	        HttpHeaders headers = createHeaders();
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new org.springframework.http.HttpEntity<>(requestData, headers), String.class);
	        return response;
	    }
}
