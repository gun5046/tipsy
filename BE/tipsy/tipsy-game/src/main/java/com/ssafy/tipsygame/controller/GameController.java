package com.ssafy.tipsygame.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.tipsygame.dto.CommonGameDto;
import com.ssafy.tipsygame.dto.CrocoDto;
import com.ssafy.tipsygame.dto.GameCommDto;
import com.ssafy.tipsygame.dto.LiarRequestDto;
import com.ssafy.tipsygame.dto.LiarResponseDto;
import com.ssafy.tipsygame.dto.LiarResultDto;
import com.ssafy.tipsygame.service.impl.GameServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GameController {

	private final GameServiceImpl gameServiceImpl;
	private final SimpMessagingTemplate simpMessagingTemplate;

	@GetMapping("/game/room")
	public String checkGameRoom(@RequestParam Long uid, @RequestParam String rid) {
		return gameServiceImpl.checkGameRoom(uid, rid);
	}

	@MessageMapping("/game/room/{rid}")
	public void communicationInGameRoom(@DestinationVariable String rid, GameCommDto gameCommDto) {
		simpMessagingTemplate.convertAndSend("/sub/room/" + rid,
				gameServiceImpl.communicateInGameRoom(rid, gameCommDto));
	}

	@MessageMapping("/game/select/{rid}")
	public void selectGame(@PathVariable String rid, int gid) {
		simpMessagingTemplate.convertAndSend("/game/select/" + rid, gid);
	}

	@MessageMapping("/game/play/liar-game/{rid}")
	public void playLiarGame(@PathVariable String rid, LiarRequestDto liarRequestDto) {
		String type = liarRequestDto.getType();
		if (type.equals("Enter")) {
			if (gameServiceImpl.countUser(rid)) {
				LiarResponseDto liarResponseDto = gameServiceImpl.getLiarData(rid);
				simpMessagingTemplate.convertAndSend("/game/play/liar-game/" + rid, liarResponseDto);
			}
		} else {
			String nickname = liarRequestDto.getNickname();
			LiarResultDto result = gameServiceImpl.voteLiar(rid, nickname);
			if (result != null) {
				simpMessagingTemplate.convertAndSend("/game/play/liar-game/" + rid, result);
			}
		}
	}

	@MessageMapping("/game/play/croco-game/{rid}")
	public void playCrocoGame(@PathVariable String rid, CrocoDto crocoDto) {

		if (crocoDto.getType().equals("Start")) {
			if (gameServiceImpl.countUser(rid)) {
				String next = gameServiceImpl.findNextUser(rid, "");
				simpMessagingTemplate.convertAndSend("/game/play/croco-game/" + rid, new CrocoDto("Start", next, 0));
			}
		} else if (crocoDto.getType().equals("Play")) {
			Boolean check = gameServiceImpl.checkCrocoIdx(rid, crocoDto.getIdx());
			if (!check) {
				String next = gameServiceImpl.findNextUser(rid, crocoDto.getNickname());
				simpMessagingTemplate.convertAndSend("/game/play/croco-game/" + rid,
						new CrocoDto("Turn", next, crocoDto.getIdx()));
			}else {
				simpMessagingTemplate.convertAndSend("/game/play/croco-game/"+rid,new CrocoDto("Result", crocoDto.getNickname(),0));
			}
		}
	}

	@MessageMapping("/game/play/drink-game/{rid}")
	public void playDrinkGame(@PathVariable String rid, CommonGameDto commonGameDto) {
		gameServiceImpl.putRecord(rid, commonGameDto);
		if(gameServiceImpl.countUser(rid)) {
			List<CommonGameDto> list = gameServiceImpl.sortRecord(rid);
			simpMessagingTemplate.convertAndSend("/game/play/drink-game/"+rid, list);
		}
	}

	@MessageMapping("/game/play/drag-game/{rid}")
	public void playDragGame(@PathVariable String rid, CommonGameDto commonGameDto) {
		gameServiceImpl.putRecord(rid, commonGameDto);
		if(gameServiceImpl.countUser(rid)) {
			List<CommonGameDto> list = gameServiceImpl.sortRecord(rid);
			simpMessagingTemplate.convertAndSend("/game/play/drag-game/"+rid, list);
		}
	}

	@MessageMapping("/game/play/roulette-game/{rid}")
	public void playRouletteGame() {

	}

	@MessageMapping("/game/play/ordering-game/{rid}")
	public void playOrderingGame(@PathVariable String rid, CommonGameDto commonGameDto) {
		gameServiceImpl.putRecord(rid, commonGameDto);
		if(gameServiceImpl.countUser(rid)) {
			List<CommonGameDto> list = gameServiceImpl.sortRecord(rid);
			simpMessagingTemplate.convertAndSend("/game/play/drag-game/"+rid, list);
		}
	}
}
