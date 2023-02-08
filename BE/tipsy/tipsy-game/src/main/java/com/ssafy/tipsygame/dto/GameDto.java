package com.ssafy.tipsygame.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor 
public class GameDto {
	private List<GameUserDto> gameUserList=new ArrayList<GameUserDto>();
	private Boolean playing = false;
	
}
