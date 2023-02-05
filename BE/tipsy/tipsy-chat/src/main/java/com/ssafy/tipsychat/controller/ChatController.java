package com.ssafy.tipsychat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.tipsychat.dto.ChatMessageDto;
import com.ssafy.tipsychat.dto.FullChatDto;
import com.ssafy.tipsychat.service.impl.ChatServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
	
	private final ChatServiceImpl chatServiceImpl;
	private final SimpMessagingTemplate simpMessagingTemplate;
	
	public void test() {
		System.out.println("test");
	}
	
	@MessageMapping("/chat/full")//전체 채팅
	public void sendFullMsg(FullChatDto fullChatDto) {
		System.out.println(fullChatDto);
		simpMessagingTemplate.convertAndSend("/topic/chat/full", fullChatDto);
	}
	
	@MessageMapping("/chat/room/{rid}")//룸 채팅
	public void sendRoomMsg(@PathVariable Long rid, FullChatDto fullChatDto) {
		System.out.println(fullChatDto);
		simpMessagingTemplate.convertAndSend("/topic/chat/room/"+rid, fullChatDto);
	}

	@MessageMapping("/chat/friends/{uid}")//친구 채팅
	public void sendFriendMsg(@PathVariable Long uid, ChatMessageDto chatMessageDto) {
		System.out.println(chatMessageDto);
		simpMessagingTemplate.convertAndSend("/topic/chat/friends/"+uid, chatMessageDto);
	}
}
