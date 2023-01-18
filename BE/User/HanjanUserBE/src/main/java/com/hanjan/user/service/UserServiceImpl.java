package com.hanjan.user.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanjan.user.dao.UserRepo;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	
	private final UserRepo userRepo;
	
	@Value("${REDIRECT.URI}")
    String redirect;
	
	@Value("${KAKAO-KEY}")
    String apiKey;
	
	WebClient webClient;
	
	@Override
	public String getAccessToken(String code) {
		// TODO Auto-generated method stub  
	    String reqURL = "https://kauth.kakao.com";
	    
	    MultiValueMap<String, String> dataForAccess = new LinkedMultiValueMap<String, String>();
	    
	    dataForAccess.add("code", code);
	    dataForAccess.add("grant_type", "authorization_code");
	    dataForAccess.add("client_id", apiKey);
	    dataForAccess.add("redirect_uri", redirect);
	    
	    try {
	    	 webClient = WebClient.create(reqURL);
	    	
	    	ResponseEntity<String> response = webClient.post().uri(uriBuilder -> uriBuilder.path("/oauth/token").build())
	    			.body(BodyInserters.fromFormData(dataForAccess))
	    			.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
	    			.retrieve().toEntity(String.class).block();
	    	
	    	if(response.getStatusCodeValue()==200) {
	    		ObjectMapper objmapper = new ObjectMapper();
	    		Map<String,Object> obj = objmapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>(){});
	    		
	    		return (String) obj.get("access_token");
	    	}else {
	    	
	    	}
	   
	    } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }

	    return null;

	}

	@Override
	public Map<String, Object> getKakaoUserInfo(String access_token) {
		String reqURL = "https://kapi.kakao.com";
		try {
			webClient = WebClient.create(reqURL);
			ResponseEntity<String> response = webClient.get()
					.uri(uriBuilder -> uriBuilder.path("/v2/user/me").build())
					.header("Authorization", "Bearer "+access_token)
					.retrieve().toEntity(String.class).block();
			System.out.println(response);
			ObjectMapper objMapper = new ObjectMapper();
			Map<String,Object> obj = objMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>(){});
			
			String kakao_id = (String)obj.get("id");
			
			
			return obj;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int checkUser(String id) {
		if(userRepo.checkUser(id)==null) {
			return 0;
		}else {
			return 1;
		}
	}
	
}
