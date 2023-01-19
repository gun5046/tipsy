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
import com.hanjan.user.data.dto.KakaoAccountDto;
import com.hanjan.user.data.dto.LoginDto;
import com.hanjan.user.data.vo.UserVo;

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
	public KakaoAccountDto getKakaoUserInfo(String access_token) {
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
			
			String kakao_id = Long.toString((Long)obj.get("id"));
			Map<String,Object> account = objMapper.readValue((String)obj.get("kakao_account"), new TypeReference<Map<String, Object>>(){});
			Map<String,Object> profile = objMapper.readValue((String)account.get("profile"), new TypeReference<Map<String, Object>>(){});
			String image = (String)profile.get("profile_image_url");
			
			KakaoAccountDto accountDto = KakaoAccountDto.builder()
					.kakao_id(kakao_id)
					.image(image)
					.gender((String)account.get("gender"))
					.birth((String)account.get("birth"))
					.build();
			
			return accountDto;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public LoginDto checkUser(String type, KakaoAccountDto accountDto) {
		LoginDto loginDto;
		UserVo userVo = userRepo.checkUser(accountDto.getKakao_id());
		if(type.equals("mobile")) {
			
			if(userVo == null) return null;
			//토큰 받아와서 빌드
		}else {
			if(userRepo.checkUser(accountDto.getKakao_id())==null)
				loginDto = LoginDto.builder().userCheck(false).accountDto(accountDto).build();
			
			//토큰 받아와서 빌드
		}
		return null;
	}
	
	
	
	@Override
	public int registUser(UserVo userVo) {
		return userRepo.registUser(userVo);
	}

	
}
