package com.atguigu.scw.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.atguigu.scw.vo.req.OrderInfoSubmitVo;
import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.scw.vo.resp.TOrder;

@FeignClient("SCW-ORDER")
public interface OrderServiceFeign {

	//1.简单参数：可以用占位符,@PathVariable   ,还可以用@RequestParam 
	//2.复杂对象：@RequestBody,将数据存放到请求体中，然后，服务端通过请求体获取数据
	@RequestMapping("/order/createOrder")
	AppResponse<TOrder> createOrder(@RequestBody OrderInfoSubmitVo orderInfoSubmitVo);

	
}
