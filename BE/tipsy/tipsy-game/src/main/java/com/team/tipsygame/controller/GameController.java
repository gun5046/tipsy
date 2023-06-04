package com.team.tipsygame.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.tipsygame.dto.CommonGameDto;
import com.team.tipsygame.dto.CrocoDto;
import com.team.tipsygame.dto.ForceExitDto;
import com.team.tipsygame.dto.GameCommDto;
import com.team.tipsygame.dto.GameUserDto;
import com.team.tipsygame.dto.LiarRequestDto;
import com.team.tipsygame.dto.LiarResponseDto;
import com.team.tipsygame.dto.LiarResultDto;
import com.team.tipsygame.service.impl.GameServiceImpl;

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
				simpMessagingTemplate.convertAndSend("/topic/play/liar-game/" + rid, result);
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
				CrocoDto data = new CrocoDto("Result", crocoDto.getNickname(),0);
				gameServiceImpl.setHost(rid, crocoDto.getNickname());
				simpMessagingTemplate.convertAndSend("/sub/play/croco-game/"+rid, data);
				simpMessagingTemplate.convertAndSend("/topic/play/croco-game/"+rid, data);
			}
		}
	}

	@MessageMapping("/game/play/drink-game/{rid}")
	public void playDrinkGame(@DestinationVariable String rid, CommonGameDto commonGameDto) {
		gameServiceImpl.putRecord(rid, commonGameDto);
		if(gameServiceImpl.countUser(rid)) {
			List<CommonGameDto> list = gameServiceImpl.sortRecord(rid);
			simpMessagingTemplate.convertAndSend("/sub/play/drink-game/"+rid, list);
			simpMessagingTemplate.convertAndSend("/topic/play/drink-game/"+rid, list);
		}
	}

	@MessageMapping("/game/play/drag-game/{rid}")
	public void playDragGame(@DestinationVariable String rid, CommonGameDto commonGameDto) {
		gameServiceImpl.putRecord(rid, commonGameDto);
		if(gameServiceImpl.countUser(rid)) {
			List<CommonGameDto> list = gameServiceImpl.sortRecord(rid);
			simpMessagingTemplate.convertAndSend("/sub/play/drag-game/"+rid, list);
			simpMessagingTemplate.convertAndSend("/topic/play/drag-game/"+rid, list);
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
				simpMessagingTemplate.convertAndSend("/topic/play/roulette-game/" + rid, type);
			}
		}
	}

	@MessageMapping("/game/play/ordering-game/{rid}")
	public void playOrderingGame(@DestinationVariable String rid, CommonGameDto commonGameDto) {
		gameServiceImpl.putRecord(rid, commonGameDto);
		if(gameServiceImpl.countUser(rid)) {
			List<CommonGameDto> list = gameServiceImpl.sortRecord(rid);
			simpMessagingTemplate.convertAndSend("/sub/play/ordering-game/"+rid, list);
			simpMessagingTemplate.convertAndSend("/topic/play/ordering-game/"+rid, list);
		}
	}
	
	@MessageMapping("/game/force-exit/{rid}")
	public void ForceExit(@DestinationVariable String rid, ForceExitDto dto) {
		List<GameUserDto> data = gameServiceImpl.communicateInGameRoom(rid, new GameCommDto("Exit", dto.getGameUserDto()));
		if(data != null) {
			gameServiceImpl.onGameStart(rid);
			simpMessagingTemplate.convertAndSend("/sub/room/" + rid, data);
			switch(dto.getGid()) {
			case -1:
				simpMessagingTemplate.convertAndSend("/sub/select/" + rid, "ForceExit," + dto.getGameUserDto().getNickname());
			case 1:
				simpMessagingTemplate.convertAndSend("/sub/play/liar-game/" + rid, "ForceExit," + dto.getGameUserDto().getNickname());
			case 2:
				gameServiceImpl.forceExit(rid);
				simpMessagingTemplate.convertAndSend("/sub/play/croco-game/" + rid, "ForceExit," + dto.getGameUserDto().getNickname());
			case 3:
				gameServiceImpl.forceExit(rid);
				simpMessagingTemplate.convertAndSend("/sub/play/drink-game/" + rid, "ForceExit," + dto.getGameUserDto().getNickname());
			case 4:
				gameServiceImpl.forceExit(rid);
				simpMessagingTemplate.convertAndSend("/sub/play/drag-game/" + rid, "ForceExit," + dto.getGameUserDto().getNickname());
			case 5:
				simpMessagingTemplate.convertAndSend("/sub/play/roulette-game/" + rid, "ForceExit," + dto.getGameUserDto().getNickname());
			case 6:
				gameServiceImpl.forceExit(rid);
				simpMessagingTemplate.convertAndSend("/sub/play/ordering-game/" + rid, "ForceExit," + dto.getGameUserDto().getNickname());
			}
		}
	}
}
