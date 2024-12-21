package com.bikestore.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DataRespone<T> {
	private Map<String,T> dataMap=new HashMap();

	public Map<String, T> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, T> dataMap) {
		this.dataMap = dataMap;
	}
	
	public DataRespone addData(String key,T data) {
		dataMap.put(key, data);
		return this;
	}
}
