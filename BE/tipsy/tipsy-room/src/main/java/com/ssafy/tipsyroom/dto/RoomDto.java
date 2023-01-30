package com.ssafy.tipsyroom.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RoomDto {
	private String code;
	private String title;
	private String password;
	private int max;
	private String time;
	private int entrance;
	private int silence;
	private List<String> hashtag;
}
