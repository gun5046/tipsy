package com.team.tipsyroom.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.team.domainnosql.entity.Member;
import com.team.domainnosql.entity.User;
import com.team.domainrdb.vo.UserVo;
import com.team.tipsyroom.service.impl.RoomServiceImpl;

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
