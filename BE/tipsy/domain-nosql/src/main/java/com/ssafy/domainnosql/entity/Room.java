package com.ssafy.domainnosql.entity;

import java.util.List;

import lombok.Data;

@Data
public class Room {
	private String code;
	private String title;
	private String password;
	private int max;
	private String time;
	private int entrance;
	private int silence;
	private List<String> hashtag;
}
