package com.ssafy.tipsyuser.dto;

import com.ssafy.coreweb.dto.TokenDto;
import com.ssafy.domainrdb.vo.UserVo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginDto {
	private Boolean userCheck;
	private UserVo userVo;
	private TokenDto tokenDto;
	
	public LoginDto(Boolean userCheck, UserVo userVo) {
		this.userCheck = userCheck;
		this.userVo = userVo;
	}
}
