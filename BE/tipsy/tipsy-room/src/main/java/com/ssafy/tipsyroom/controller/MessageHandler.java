package com.ssafy.tipsyroom.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.ssafy.domainnosql.entity.Member;
import com.ssafy.domainnosql.entity.User;
import com.ssafy.domainrdb.vo.UserVo;
import com.ssafy.tipsyroom.service.impl.RoomServiceImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageHandler {
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final RoomServiceImpl roomService;

	@MessageMapping("/room/enterMessage")
	public void enterRoom(Integer bno) {

		List<Map<Object, Object>> TableInfo = roomService.getTable(bno);
		System.out.println(TableInfo);
		simpMessagingTemplate.convertAndSend("/room/enter",TableInfo);
	}
	
}
