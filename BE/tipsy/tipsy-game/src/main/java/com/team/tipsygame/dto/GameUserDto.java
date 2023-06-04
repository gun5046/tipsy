package com.team.tipsygame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameUserDto {
	private String nickname;
	private String img;
	private Boolean host;
	private Boolean ready;
}
