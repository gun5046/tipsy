package com.ssafy.domainnosql.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomVo {
	private String code;
	private String title;
	private String password;
	private int max;
	private String time;
	private int entrance;
	private int silence;
	private List<String> hashtag;
}
