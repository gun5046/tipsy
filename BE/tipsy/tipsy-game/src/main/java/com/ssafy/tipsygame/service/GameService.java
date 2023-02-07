package com.ssafy.tipsygame.service;

import java.util.List;

import com.ssafy.tipsygame.dto.CommonGameDto;
import com.ssafy.tipsygame.dto.GameCommDto;
import com.ssafy.tipsygame.dto.GameUserDto;
import com.ssafy.tipsygame.dto.LiarResponseDto;
import com.ssafy.tipsygame.dto.LiarResultDto;

public interface GameService {
	String checkGameRoom(Long uid, String rid);
	public List<GameUserDto> communicateInGameRoom(String rid, GameCommDto gameCommDto);
	public Boolean countUser(String rid);
	public LiarResponseDto getLiarData(String rid);
	public String voteLiar(String rid,String nickname);
	public void getCrocoTeeth(String rid);
	public String findNextUser(String rid, String nickname);
	public Boolean checkCrocoIdx(String rid, int idx);
	public List<CommonGameDto> sortRecord(String rid);
}
