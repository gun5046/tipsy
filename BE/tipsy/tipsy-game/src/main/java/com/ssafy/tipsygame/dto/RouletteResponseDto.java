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
public class RouletteResponseDto {
	int index;
	List<GameUserDto> list;
}
