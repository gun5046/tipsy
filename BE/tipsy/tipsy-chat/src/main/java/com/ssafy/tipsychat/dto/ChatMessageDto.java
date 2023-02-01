package com.ssafy.tipsychat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
	private String from; //nickname
	private String to; // nickname
	private String content;
	private String datetime;
	
	public String toString() {
		return "sender : " + from + "to :" + to +"    - " + content ;
	}
}
