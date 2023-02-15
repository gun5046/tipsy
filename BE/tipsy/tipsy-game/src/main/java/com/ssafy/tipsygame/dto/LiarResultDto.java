package com.ssafy.tipsygame.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LiarResultDto {
	private String user1;
	private String user2;
	private int vote1;
	private int vote2;
	
}
