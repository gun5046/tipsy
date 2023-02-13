package com.ssafy.tipsyuser.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.coreweb.dto.TokenDto;
import com.ssafy.coreweb.dto.UserDto;
import com.ssafy.coreweb.provider.JwtTokenProvider;
import com.ssafy.domainauth.entity.Auth;
import com.ssafy.domainauth.repo.AuthRepository;
import com.ssafy.domainrdb.dao.user.UserDao;
import com.ssafy.domainrdb.vo.ReportVo;
import com.ssafy.domainrdb.vo.UserVo;
import com.ssafy.tipsyuser.dto.KakaoAccountDto;
import com.ssafy.tipsyuser.dto.LoginDto;
import com.ssafy.tipsyuser.service.UserService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	private final JwtTokenProvider jwtTokenProvider;
	private final UserDao userDao;
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
			ObjectMapper objMapper = new ObjectMapper();
			Map<String,Object> obj = objMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>(){});
			
			String kakao_id = Long.toString((Long)obj.get("id"));
			Map<String,Object> account = (Map<String, Object>) obj.get("kakao_account");
			Map<String,Object> profile = (Map<String, Object>) account.get("profile");
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

	@Transactional
	@Override
	public LoginDto checkUser(String type, KakaoAccountDto accountDto) {
		LoginDto loginDto;
		UserVo userVo = userDao.findByKakaoID(accountDto.getKakao_id());
		if(type.equals("mobile")) {
			if(userVo == null) return null;

		}else {
			if(userVo==null) {
				UserVo newUserVo = UserVo
						.builder()
						.kakao_id(accountDto.getKakao_id())
						.image(accountDto.getImage())
						.gender(accountDto.getGender())
						.birth(accountDto.getBirth())
						.build();
				loginDto = LoginDto.builder().userCheck(false).userVo(newUserVo).build();
				return loginDto;
			}
		}

		
		String accessToken = jwtTokenProvider.createAccessToken(new UserDto(userVo.getUid(),userVo.getName(),userVo.getNickname()));
		String refreshToken = jwtTokenProvider.createRefreshToken();

		jwtTokenProvider.saveRefreshToken(userVo.getUid(), refreshToken);

		TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
		loginDto = LoginDto.builder()
				.userCheck(true)
				.userVo(userVo)
				.tokenDto(tokenDto)
				.build();
		return loginDto;
	}
	
	
	
	@Override
	@Transactional
	public int registUser(UserVo userVo) {
		return userDao.insertUser(userVo);
	}
	
	@Override
	public UserVo getUserInfo(Long uid) {
		return userDao.findUserByUid(uid);
	}

	@Override
	public int checkNickname(String nickname) {
		return userDao.findUserCountByNickname(nickname)!=null? 1:0;
	}

	@Override
	@Transactional
	public int updateUserInfo(UserVo userVo) {
		return userDao.updateUserInfo(userVo);
	}

	@Override
	@Transactional
	public int deleteUser(Long uid) {
		return userDao.deleteUser(uid);
	}
	
	@Override
	public int reportUser(ReportVo reportvo) {
		return userDao.reportUser(reportvo);
	}
}
