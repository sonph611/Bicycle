package com.bikestore.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.json.GsonBuilderUtils;

import com.bikestore.model.Orders;
import com.bikestore.service.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

public class JSONParse {

//    private final ConcurrentMap<String, ExpiringObject> objectStore = new ConcurrentHashMap()<>();
//    private final Timer timer = new Timer(true);
//
//    public void addObject(String key, Object obj, long ttlMillis) {
//        ExpiringObject expiringObject = new ExpiringObject(obj, ttlMillis);
//        objectStore.put(key, expiringObject);
////        scheduleRemoval(key, ttlMillis);
//    }
//
//    public Object getObject(String key) {
//        ExpiringObject expiringObject = objectStore.get(key);
//        if (expiringObject != null && !expiringObject.isExpired()) {
//            return expiringObject.getObject();
//        }
//        // Remove expired object
//        removeObject(key);
//        return null;
//    }
//
//    public void removeObject(String key) {
//        objectStore.remove(key);
//    }

//    private void scheduleRemoval(String key, long ttlMillis) {
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                removeObject(key);
//            }
//        }, ttlMillis);
//    }

    private static class ExpiringObject {
        private final Object object;
        private final long expirationTime;

        public ExpiringObject(Object object, long ttlMillis) {
            this.object = object;
            this.expirationTime = System.currentTimeMillis() + ttlMillis;
        }

        public Object getObject() {
            return object;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
//    @Autowired
//    private static RedisTemplate<String, Object> redisTemplate;
//    public static void saveOrder(String orderId, Orders order) {
//        redisTemplate.opsForValue().set("order:" + orderId, order);
//    }
//
//    public static Orders getOrder(String orderId) {
//        return (Orders) redisTemplate.opsForValue().get("order:" + orderId);
//    }
//    
//    public static void main(String[] args) throws InterruptedException {
//    		Orders o=new Orders();
//    		o.setId(228272772);
//    		saveOrder("1", o);
//    		o=getOrder("1");
//    		System.out.println(o.getId());

//        ExpiringObjectServic service = new ExpiringObjectService();
//
//        service.addObject("key1", "value1", 5000); // Object sẽ hết hạn sau 5 giây
//
//        System.out.println(service.getObject("key1")); // Hiển thị "value1"
//
//        Thread.sleep(6000); // Chờ hơn thời gian hết hạn
//
//        System.out.println(service.getObject("key1")); // Hiển thị null vì object đã hết hạn
//    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String convertToJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    public static <T> T convertFromJson(String json, Class<T> valueType) throws Exception {
        return objectMapper.readValue(json, valueType);
    }
    public static void main(String[] args) {
    	
//    	Orders order=new Orders();
//    	try {
//			String a=JSONParse.convertToJson(order);
//			System.out.println(a);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
