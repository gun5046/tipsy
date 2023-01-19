package com.hanjan.user.data.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class LoginDto {
	private Boolean userCheck;
	private KakaoAccountDto accountDto;
	private String accessToken;
	private String refreshToken;
}
