package com.atguigu.scw.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.scw.vo.resp.TMemberAddress;
import com.atguigu.scw.vo.resp.UserRegistVo;
import com.atguigu.scw.vo.resp.UserRespVo;

@FeignClient("SCW-USER")
public interface UserServiceFeign {

	@PostMapping("/user/login")
	public AppResponse<UserRespVo> login(@RequestParam("loginacct") String loginacct,@RequestParam("userpswd")  String userpswd) ;
	
	
	@GetMapping("/user/info/address")
	public AppResponse<List<TMemberAddress>> addresses(@RequestParam("accessToken") String accessToken) ;
	
	@GetMapping("/user/register")
	public AppResponse<List<TMemberAddress>> register(@RequestBody UserRegistVo userRegistVo) ;

}
