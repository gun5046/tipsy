package com.ssafy.tipsychat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullChatDto {
	private String sender;// nickname
	private String content;
	private String datetime;
	
	public String toString() {
		return sender + " :  " + content;
	}
}
