package com.atguigu.scw.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.druid.util.StringUtils;
import com.atguigu.scw.service.ProjectServiceFeign;
import com.atguigu.scw.service.UserServiceFeign;
import com.atguigu.scw.vo.req.ProjectVo;
import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.scw.vo.resp.UserRegistVo;
import com.atguigu.scw.vo.resp.UserRespVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class DispatcherController {
	
	@Autowired
	UserServiceFeign userServiceFeign;
	
	@Autowired
	ProjectServiceFeign projectServiceFeign;
	
	

	
	
	
	
	@RequestMapping("/index")
	public String index(Model model, HttpSession session) {

//		model.addAttribute("hello", "<h2>all in one!!!</h2>");
//		session.setAttribute("name", "tom");
//		ServletContext application = session.getServletContext();
//		application.setAttribute("count", 22);

		AppResponse<List<ProjectVo>> all = projectServiceFeign.all();
		
		List<ProjectVo> projectVoList = all.getData();
		
		model.addAttribute("projectVoList", projectVoList);
		
		return "index";
	}


	
	


	@PostMapping("/doLogin")
	public String login(String loginacct, String userpswd, Model model, HttpSession session) {
		log.debug("有人登录系统：loginacct：【{}】，准备远程调用", loginacct);
		// 1、登录；远程调用
		AppResponse<UserRespVo> login = userServiceFeign.login(loginacct, userpswd);

		if (login.getCode() == 1) {
			model.addAttribute("msg", "账号密码错误，请重试");
			return "login";
		} else {
			session.setAttribute("user", login.getData());
			String preUrl = (String)session.getAttribute("preUrl");
			System.out.println("preUrl="+preUrl);
			if(StringUtils.isEmpty(preUrl)) {
				return "redirect:/index";
			}else {
				return "redirect:"+preUrl;
			}
			
		}
		
	}
	
	
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		if(session!=null) {
			session.removeAttribute("user");
			session.invalidate();
		}
		return "redirect:/index";
	}
}
