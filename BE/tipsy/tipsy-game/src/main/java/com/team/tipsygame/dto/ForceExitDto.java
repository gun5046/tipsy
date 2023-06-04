package com.team.tipsygame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForceExitDto {
	private String type;
	private GameUserDto gameUserDto;
	private int gid;
}
