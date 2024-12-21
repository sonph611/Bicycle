package com.bikestore.service;


import com.bikestore.model.Location;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    private static final String BASE_URL = "https://esgoo.net/api-tinhthanh/";

    private final OkHttpClient client = new OkHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Location> getAllProvinces() throws IOException {
        String url = BASE_URL + "1/0.htm";
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return parseLocationData(jsonNode.get("data"));
        }
    }

    public List<Location> getDistricts(int provinceId) throws IOException {
        String url = BASE_URL + "2/" + provinceId + ".htm";
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            return parseLocationData(jsonNode.get("data"));
        }
    }

    public List<Location> getWards(int districtId) throws IOException {
        String url = BASE_URL + "3/" + districtId + ".htm";
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            return parseLocationData(jsonNode.get("data"));
        }
    }

    private List<Location> parseLocationData(JsonNode dataNode) {
        List<Location> locations = new ArrayList<>();
        for (JsonNode node : dataNode) {
            Location location = new Location();
            location.setId(node.get("id").asInt());
            location.setFullName(node.get("name").asText()); 
            locations.add(location);
        }
        return locations;
    }



   
}
