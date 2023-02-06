package com.ssafy.tipsygame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonGameDto implements Comparable {
	private String nickname;
	private String image;
	private int score;

	public Integer getScore() {
		return this.score;
	}

	@Override
	public boolean equals(Object obj) {
		return ((CommonGameDto) obj).getScore().equals(this.getScore());
	}

	@Override
	public int compareTo(Object o) {
		CommonGameDto e = (CommonGameDto) o;
		return this.getScore().compareTo(e.getScore());
	}
}
