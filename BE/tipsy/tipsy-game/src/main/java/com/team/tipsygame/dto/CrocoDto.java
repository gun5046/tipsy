package com.team.tipsygame.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrocoDto {
	private String type;// start turn play result
	private String nickname;
	private int idx;
}

//start : 처음 할사람, idx=0
//play : 누른 사람, 누른 인덱스
//turn : 다음 사람, 전 사람 눌렀던 인덱스
//result : 걸린사람, idx=0;
