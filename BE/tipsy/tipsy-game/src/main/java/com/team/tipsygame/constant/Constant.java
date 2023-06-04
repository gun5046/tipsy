package com.team.tipsygame.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class Constant {
	public Map<String,List<String>> liarGameData = new HashMap<String, List<String>>();
	public int crocoTeeth = 16;
	public Constant() {
		List<String>list = new ArrayList<String>();
		
		list.add("바나나");
		list.add("사과");
		list.add("포도");
		list.add("청포도");
		list.add("샤인머스켓");
		list.add("감");
		list.add("수박");
		list.add("망고");
		list.add("배");
		list.add("자두");
		list.add("귤");
		list.add("오렌지");
		
		liarGameData.put("과일", list);
		
		list= new ArrayList<String>();
		
		list.add("경찰");
		list.add("소방관");
		list.add("대통령");
		list.add("국회의원");
		list.add("바리스타");
		list.add("개발자");
		list.add("선생님");
		list.add("학원강사");
		list.add("군인");
		list.add("베이비시터");
		list.add("유튜버");
		list.add("요리사");
		list.add("가수");
		list.add("배우");
		list.add("영화감독");
		list.add("작가");
		list.add("작곡가");
		
		liarGameData.put("직업", list);
	}
}
