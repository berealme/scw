package com.atguigu.scw.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.atguigu.scw.service.ProjectServiceFeign;
import com.atguigu.scw.service.UserServiceFeign;
import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.scw.vo.resp.ReturnPayConfirmVo;
import com.atguigu.scw.vo.resp.TMemberAddress;
import com.atguigu.scw.vo.resp.UserRespVo;

@Controller
@RequestMapping("/project")
public class ProjectSupportController {

	@Autowired
	ProjectServiceFeign projectServiceFeign;
	
	@Autowired
	UserServiceFeign userServiceFeign;
	
	@RequestMapping("/returnConfirm")
	public String returnConfirm(Integer projectId,Integer returnId,Model model,HttpSession session){
	
		AppResponse<ReturnPayConfirmVo> response = projectServiceFeign.confirmProjectReturnPayInfo(projectId, returnId);
		
		ReturnPayConfirmVo returnPayConfirmVo = response.getData();
		
		model.addAttribute("returnPayConfirmVo", returnPayConfirmVo);
		
		session.setAttribute("returnPayConfirmVoSession", returnPayConfirmVo);
		
		return "project/pay-step-1";
	}
	
	
	
	
	
	@RequestMapping("/payconfirm")
	public String payconfirm(Integer num,Model model,HttpServletRequest request,HttpSession session){
	
		UserRespVo user = (UserRespVo)session.getAttribute("user");
		
		if(user == null) { //没登录去登录页
			session.setAttribute("message","请先登录，再进行访问!");
			session.setAttribute("preUrl", request.getRequestURI()+"?num="+num);
			return "redirect:/login";
		}
		
		//继续逻辑处理
		String accessToken = user.getAccessToken();
		AppResponse<List<TMemberAddress>> resp = userServiceFeign.addresses(accessToken);
		
		List<TMemberAddress> addressList = resp.getData();
		
		model.addAttribute("addressList", addressList);
		
		//session域支持数量需要变更
		ReturnPayConfirmVo returnPayConfirmVo = (ReturnPayConfirmVo)session.getAttribute("returnPayConfirmVoSession");
		returnPayConfirmVo.setNum(num);
		
		//session数据发生了变化，重新存放session数据。这样redis缓存区数据才会同步修改。
		session.setAttribute("returnPayConfirmVoSession", returnPayConfirmVo);
		
		return "project/pay-step-2";
	}
	
	
	
	
	
	
	
	
	
	
	
}
