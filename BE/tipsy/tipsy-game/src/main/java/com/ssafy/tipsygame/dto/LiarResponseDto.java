package com.ssafy.tipsygame.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LiarResponseDto {
	private String category;
	private String word;
	private String liar; // nickname
	private List<GameUserDto> gameUserList=new ArrayList<GameUserDto>();
}
