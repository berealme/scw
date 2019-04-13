package com.atguigu.scw.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.atguigu.scw.vo.req.ProjectVo;
import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.scw.vo.resp.ProjectDetailVo;
import com.atguigu.scw.vo.resp.ReturnPayConfirmVo;

@FeignClient("SCW-PROJECT")
public interface ProjectServiceFeign {
	@GetMapping("/project/all")
	public AppResponse<List<ProjectVo>> all();
	
	@GetMapping("/project/details/info/{projectId}")
	public AppResponse<ProjectDetailVo> detailsInfo(@PathVariable("projectId") Integer projectId) ;

	@GetMapping("/project/returnConfirm")
	public AppResponse<ReturnPayConfirmVo> confirmProjectReturnPayInfo(@RequestParam("projectId") Integer projectId,
			@RequestParam("retId") Integer retId);

}
