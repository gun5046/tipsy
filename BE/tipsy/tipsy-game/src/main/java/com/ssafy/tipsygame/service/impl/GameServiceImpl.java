package com.ssafy.tipsygame.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.ssafy.domainnosql.entity.Member;
import com.ssafy.domainnosql.repo.RoomRepository;
import com.ssafy.tipsygame.constant.Constant;
import com.ssafy.tipsygame.dto.CommonGameDto;
import com.ssafy.tipsygame.dto.GameCommDto;
import com.ssafy.tipsygame.dto.GameDto;
import com.ssafy.tipsygame.dto.GameUserDto;
import com.ssafy.tipsygame.dto.LiarResponseDto;
import com.ssafy.tipsygame.dto.LiarResultDto;
import com.ssafy.tipsygame.service.GameService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{
	
	private final RoomRepository roomRepository;
	private Map<String, GameDto> roomList;
	private Map<String, Integer> count;
	private Map<String, Map<String,Integer>> vote;
	private Map<String, Integer> crocoIdx;
	private Map<String, List<CommonGameDto>> commonData;
	private Constant constant;
	
	@PostConstruct
	public void init() {
		roomList=new HashMap<>();
		count = new HashMap<String, Integer>();
		vote = new HashMap<String, Map<String,Integer>>();
		crocoIdx = new HashMap<String, Integer>();
		commonData = new HashMap<String, List<CommonGameDto>>();
	}
	public String checkGameRoom(Long uid, String rid) {
		List<Member> memberList = roomRepository.findAllById(rid);
		if(memberList.isEmpty()) {
			return "WrongRoomId";
		}
		Boolean wrongUser = true;
		for(Member m : memberList) {
			if(m.getUid()==(long)uid) {
				wrongUser=false;
			}
		}
		
		if(wrongUser) {
			return "WrongUser";
		}
		
		if(roomList.containsKey(rid)){
			if(roomList.get(rid).getPlaying()) {
				return "Playing";
			}
		}
		return "True";
	}
	public List<GameUserDto> communicateInGameRoom(String rid, GameCommDto gameCommDto) {
		String type= gameCommDto.getType();
		GameUserDto gameUser = gameCommDto.getGameUserDto();
		switch(type) {
		case "Enter" :
			if(!roomList.containsKey(rid)) {
				List <GameUserDto> list = new ArrayList<>();
				gameUser.setHost(true);
				list.add(gameUser);
				roomList.put(rid, new GameDto(list,false));
			}else {
				roomList.get(rid).getGameUserList().add(gameUser);
			}
			return roomList.get(rid).getGameUserList();
		case "Ready" :
			roomList.get(rid).getGameUserList().forEach(e -> {
				if(e.getNickname().equals(gameUser.getNickname())) {
					e.setReady(gameUser.getReady());
				}
			});
			return roomList.get(rid).getGameUserList();
		case "Start" :
			roomList.get(rid).setPlaying(true);
			count.put(rid, 0);
			roomList.get(rid).getGameUserList().forEach(e -> {
				if(e.getNickname().equals(gameUser.getNickname())) {
					e.setReady(gameUser.getReady());
				}
			});
			return roomList.get(rid).getGameUserList();
		case "Exit" :
			List<GameUserDto> list = roomList.get(rid).getGameUserList();
			Boolean host = false;
			for(int i=0; i<list.size(); i++) {
				if(list.get(i).getNickname().equals(gameUser.getNickname())) {
					host = list.get(i).getHost();
					roomList.get(rid).getGameUserList().remove(i);
				}
			}
			if(roomList.get(rid).getGameUserList().size() > 0) {
				if(host) {
					roomList.get(rid).getGameUserList().get(0).setHost(true);
				}
			} else {
				roomList.remove(rid);
			}
			if(roomList.containsKey(rid)) {
				return roomList.get(rid).getGameUserList();	
			} else {
				return null;
			}
		}
		return null;
	}
	
	public Boolean countUser(String rid) {
		int size = roomList.get(rid).getGameUserList().size();
		count.replace(rid, count.get(rid)+1);
		if(count.get(rid)==size) {
			count.remove(rid);
			return true;
		}
		return false;
	}
	public LiarResponseDto getLiarData(String rid) {
		int csize = constant.getLiarGameData().size();
		
		int randomIdx = (int)(Math.random()*csize);
		Iterator<String>keys = constant.getLiarGameData().keySet().iterator();
		int i=0;
		String category = null;
		while(keys.hasNext()) {
			category = keys.next();
			if(i++==randomIdx) {
				break;
			}
		}
		if(category != null) {
			int wsize = constant.getLiarGameData().get(category).size();
			randomIdx = (int)(Math.random() * wsize);
			String word = constant.getLiarGameData().get(category).get(randomIdx);
			int usize = roomList.get(rid).getGameUserList().size();
			
			randomIdx = (int)(Math.random()*usize);
			String liar = roomList.get(rid).getGameUserList().get(randomIdx).getNickname();
			
			List<GameUserDto>list = roomList.get(rid).getGameUserList();
			
			LiarResponseDto liarResponseDto = LiarResponseDto.builder()
					.category(category)
					.word(word)
					.liar(liar)
					.gameUserList(list)
					.build();
			return liarResponseDto;
		}
		return null;
	}
	public LiarResultDto voteLiar(String rid,String nickname) {
		if(!vote.containsKey(rid)) {
			Map<String, Integer> m = new TreeMap<String, Integer>();
			m.put(nickname, -1);
			vote.put(rid, m);
		}else {
			if(!vote.get(rid).containsKey(nickname)) {
				vote.get(rid).put(nickname, -1);
			}else {
				vote.get(rid).replace(nickname, vote.get(rid).get(nickname)-1);
			}
		}
		
		if(countUser(rid)) {
			Iterator<String>keys = vote.get(rid).keySet().iterator();
			int temp=0;
			int v1=0,v2=0;
			String u1="", u2="";
			while(keys.hasNext()) {
				if(temp==0) {
					u1=keys.next();
					v1= vote.get(rid).get(keys.next());
				}else {
					u2=keys.next();
					v2= vote.get(rid).get(keys.next());				
					break;
				}
				temp++;
			}
			
			LiarResultDto result =  LiarResultDto.builder()
					.user1(u1)
					.user2(u2)
					.vote1(v1)
					.vote2(v2)
					.build();
			return result;
		}
		return null;
	}
	
	public void getCrocoTeeth(String rid) {
		crocoIdx.put(rid, (int)(Math.random()*constant.crocoTeeth));
	}
	public String findNextUser(String rid, String nickname) {
		List<GameUserDto> list= roomList.get(rid).getGameUserList();
		if(nickname == "") {
			for(GameUserDto g : list) {
				if(g.getHost()) {
					return g.getNickname();
				}
			}
			return "";
		}else {
			int nowIdx=-1;
			for(int i =0; i<list.size(); i++){
				if(list.get(i).getNickname().equals(nickname)) {
					nowIdx=i;
				}
			}
			if(++nowIdx==list.size()) {
				return list.get(0).getNickname();
			}else {
				return list.get(nowIdx).getNickname();
			}
		}
		
	}
	public Boolean checkCrocoIdx(String rid, int idx) {
		if(idx == crocoIdx.get(rid)) {
			crocoIdx.remove(rid);
			return true;
		}
		
		return false;
	}
	public void putRecord(String rid,CommonGameDto commonGameDto) {

		if(!commonData.containsKey(rid)){
			List<CommonGameDto> list = new ArrayList<CommonGameDto>();
			list.add(commonGameDto);
			commonData.put(rid, list);
		}else {
			commonData.get(rid).add(commonGameDto);
		}
	}
	
	public List<CommonGameDto> sortRecord(String rid) {
		List <CommonGameDto> list = commonData.get(rid);
		commonData.remove(rid);
		Collections.sort(list,Collections.reverseOrder());
		return list;
	}
	
}
