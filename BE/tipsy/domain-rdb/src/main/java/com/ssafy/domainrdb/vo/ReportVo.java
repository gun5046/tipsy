package com.ssafy.domainrdb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportVo {
//	private Long rid;
	private String content;
	private Long from;
	private Long to;
}
