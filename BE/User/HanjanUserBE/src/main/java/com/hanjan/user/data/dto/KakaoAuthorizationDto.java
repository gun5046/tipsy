package com.hanjan.user.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class KakaoAuthorizationDto {
	private String code;
	private String state;
	private String error;
	private String error_description;
}
