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
import com.ssafy.tipsygame.dto.GameUserDto;
import com.ssafy.tipsygame.dto.LiarRequestDto;
import com.ssafy.tipsygame.dto.LiarResponseDto;
import com.ssafy.tipsygame.dto.LiarResultDto;
import com.ssafy.tipsygame.service.impl.GameServiceImpl;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GameController {

	private final GameServiceImpl gameServiceImpl;
	private final SimpMessagingTemplate simpMessagingTemplate;

	@GetMapping("/game/room")
	@ApiOperation(value = "방 정보 확인", notes = "요청받은 rid의 방 정보를 확인하고 결과 반환")
	public String checkGameRoom(@RequestParam Long uid, @RequestParam String rid) {
		gameServiceImpl.room();
		return gameServiceImpl.checkGameRoom(uid, rid);
	}

	@MessageMapping("/game/room/{rid}")
	public void communicationInGameRoom(@DestinationVariable String rid, GameCommDto gameCommDto) {
		List<GameUserDto> data = gameServiceImpl.communicateInGameRoom(rid, gameCommDto);
		if(data != null)
			simpMessagingTemplate.convertAndSend("/sub/room/" + rid, data);
	}

	@MessageMapping("/game/select/{rid}")
	public void selectGame(@DestinationVariable String rid, int gid) {
		simpMessagingTemplate.convertAndSend("/sub/select/" + rid, gid);
		gameServiceImpl.onGameStart(rid);
	}

	@MessageMapping("/game/play/liar-game/{rid}")
	public void playLiarGame(@DestinationVariable String rid, LiarRequestDto liarRequestDto) {
		String type = liarRequestDto.getType();
		if (type.equals("Enter")) {
			if (gameServiceImpl.countUser(rid)) {
				LiarResponseDto liarResponseDto = gameServiceImpl.getLiarData(rid);
				simpMessagingTemplate.convertAndSend("/sub/play/liar-game/" + rid, liarResponseDto);
			}
		} else {
			String nickname = liarRequestDto.getNickname();
			String result = gameServiceImpl.voteLiar(rid, nickname);
			if (result != null) {
				simpMessagingTemplate.convertAndSend("/sub/play/liar-game/" + rid, result);
			}
		}
	}

	@MessageMapping("/game/play/croco-game/{rid}")
	public void playCrocoGame(@DestinationVariable String rid, CrocoDto crocoDto) {
		if (crocoDto.getType().equals("Start")) {
			if (gameServiceImpl.countUser(rid)) {
				gameServiceImpl.getCrocoTeeth(rid);
				String next = gameServiceImpl.findNextUser(rid, "");
				simpMessagingTemplate.convertAndSend("/sub/play/croco-game/" + rid, new CrocoDto("Start", next, 0));
			}
		} else if (crocoDto.getType().equals("Play")) {
			Boolean check = gameServiceImpl.checkCrocoIdx(rid, crocoDto.getIdx());
			if (!check) {
				String next = gameServiceImpl.findNextUser(rid, crocoDto.getNickname());
				simpMessagingTemplate.convertAndSend("/sub/play/croco-game/" + rid,
						new CrocoDto("Turn", next, crocoDto.getIdx()));
			}else {
				simpMessagingTemplate.convertAndSend("/sub/play/croco-game/"+rid,new CrocoDto("Result", crocoDto.getNickname(),0));
			}
		}
	}

	@MessageMapping("/game/play/drink-game/{rid}")
	public void playDrinkGame(@DestinationVariable String rid, CommonGameDto commonGameDto) {
		gameServiceImpl.putRecord(rid, commonGameDto);
		if(gameServiceImpl.countUser(rid)) {
			List<CommonGameDto> list = gameServiceImpl.sortRecord(rid);
			simpMessagingTemplate.convertAndSend("/sub/play/drink-game/"+rid, list);
		}
	}

	@MessageMapping("/game/play/drag-game/{rid}")
	public void playDragGame(@DestinationVariable String rid, CommonGameDto commonGameDto) {
		gameServiceImpl.putRecord(rid, commonGameDto);
		if(gameServiceImpl.countUser(rid)) {
			List<CommonGameDto> list = gameServiceImpl.sortRecord(rid);
			simpMessagingTemplate.convertAndSend("/sub/play/drag-game/"+rid, list);
		}
	}

	@MessageMapping("/game/play/roulette-game/{rid}")
	public void playRouletteGame(@DestinationVariable String rid, String type) {
		if(type.equals("Enter")) {
			if(gameServiceImpl.countUser(rid)) {
				simpMessagingTemplate.convertAndSend("/sub/play/roulette-game/"+rid,
						gameServiceImpl.getRouletteResponseDto(rid));
			}
		} else {
			if(gameServiceImpl.countUser(rid)) {
				//type == nickname send response to frontend
			}
		}
	}

	@MessageMapping("/game/play/ordering-game/{rid}")
	public void playOrderingGame(@DestinationVariable String rid, CommonGameDto commonGameDto) {
		gameServiceImpl.putRecord(rid, commonGameDto);
		if(gameServiceImpl.countUser(rid)) {
			List<CommonGameDto> list = gameServiceImpl.sortRecord(rid);
			simpMessagingTemplate.convertAndSend("/sub/play/ordering-game/"+rid, list);
		}
	}
}
