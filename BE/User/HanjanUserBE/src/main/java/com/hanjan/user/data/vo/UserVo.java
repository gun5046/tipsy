package com.hanjan.user.data.vo;

import com.hanjan.user.data.dto.KakaoAccountDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserVo {
	private long uid;
	private String kakao_id;
	private String name;
	private String nickname;
	private String image;
	private String birth;
	private String gender; 
	private String interest;
	private int reportcnt;
	
	
	public UserVo(KakaoAccountDto accountDto) {
		this.kakao_id = accountDto.getKakao_id();
		this.gender = accountDto.getGender();
		this.birth = accountDto.getBirth();
		this.image = accountDto.getImage();
	}
}
