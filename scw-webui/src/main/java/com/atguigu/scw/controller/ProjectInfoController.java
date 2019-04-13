package com.atguigu.scw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.atguigu.scw.service.ProjectServiceFeign;
import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.scw.vo.resp.ProjectDetailVo;

@Controller
public class ProjectInfoController {

	@Autowired
	ProjectServiceFeign projectServiceFeign;
	
	//查出某个项目的详情
	@GetMapping("/project/detail/{id}")
	public String projectInfoPage(@PathVariable("id")Integer id,Model model){
	AppResponse<ProjectDetailVo> response = projectServiceFeign.detailsInfo(id);
	ProjectDetailVo data = response.getData();
	model.addAttribute("project", data);
	return "project/project";
	}

	
}
