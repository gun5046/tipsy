package com.ssafy.domainrdb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentVo {
	private Long aid;
	private double score;
	private Long uid;
}
