package com.atguigu.scw.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.scw.config.AlipayConfig;
import com.atguigu.scw.service.OrderServiceFeign;
import com.atguigu.scw.service.ProjectServiceFeign;
import com.atguigu.scw.vo.req.OrderFormInfoSubmitVo;
import com.atguigu.scw.vo.req.OrderInfoSubmitVo;
import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.scw.vo.resp.ProjectDetailVo;
import com.atguigu.scw.vo.resp.ReturnPayConfirmVo;
import com.atguigu.scw.vo.resp.TOrder;
import com.atguigu.scw.vo.resp.UserRespVo;

import lombok.extern.slf4j.Slf4j;
 
@Slf4j
@Controller
@RequestMapping("/order")
public class PayController {

	@Autowired
	OrderServiceFeign orderServiceFeign;

	@Autowired
	ProjectServiceFeign projectServiceFeign;

	// HttpMessageConverter
	// StringHttpMessageConverter: 方法直接返回字符串采用该转换器
	// MappingJackson2HttpMessageConverter：方法返回对象(实体类 ,VO,List,Map等)
	@ResponseBody
	@RequestMapping("/pay")
	public String pay(OrderFormInfoSubmitVo vo, HttpSession session, HttpServletRequest request) {
		log.debug("OrderFormInfoSubmitVo-{}", vo);

		UserRespVo user = (UserRespVo) session.getAttribute("user");

		if (user == null) { // 没登录去登录页
			session.setAttribute("message", "请先登录，再进行访问!");
			session.setAttribute("preUrl", request.getRequestURI());
			return "redirect:/login";
		}

		// 准备远程调用数据

		OrderInfoSubmitVo orderInfoSubmitVo = new OrderInfoSubmitVo(); // 封装远程调用数据

		// 1.来自表单数据
		BeanUtils.copyProperties(vo, orderInfoSubmitVo);

		// 2.来自session域，用户数据
		orderInfoSubmitVo.setAccessToken(user.getAccessToken());

		// 3.来自session域，支持回报数据
		ReturnPayConfirmVo returnPayConfirmVo = (ReturnPayConfirmVo) session.getAttribute("returnPayConfirmVoSession");
		orderInfoSubmitVo.setCountNum(returnPayConfirmVo.getNum());

		orderInfoSubmitVo.setProjectId(returnPayConfirmVo.getProjectId());
		orderInfoSubmitVo.setReturnId(returnPayConfirmVo.getReturnId());

		// 创建订单
		AppResponse<TOrder> response = orderServiceFeign.createOrder(orderInfoSubmitVo);

		TOrder order = response.getData();

		log.debug("订单数据order-{}", order); // 订单状态 - 未支付状态

		// 业务逻辑，支付操作。。。。
		Integer projectId = returnPayConfirmVo.getProjectId();
		AppResponse<ProjectDetailVo> resp = projectServiceFeign.detailsInfo(projectId);
		ProjectDetailVo projectDetailVo = resp.getData();

		String result = payOrder(order.getOrdernum(), order.getMoney() + "", projectDetailVo.getName(),
				projectDetailVo.getRemark());

		log.debug("{}", result);

		return result;// 返回form表单。不能进行视图解析。应该是异步处理。将字符串原封不动返回。只需要增加@ResponseBody
	}

	// 支付
	// 商户订单号，商户网站订单系统中唯一订单号，必填
	// 付款金额，必填
	// 订单名称，必填
	// 商品描述，可空
	private String payOrder(String out_trade_no, String total_amount, String subject, String body) {
		// 1、创建支付宝客户端
		AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id,
				AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key,
				AlipayConfig.sign_type);

		// 2、创建一次支付请求
		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		alipayRequest.setReturnUrl(AlipayConfig.return_url);
		alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

		// 3、构造支付请求数据
		alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"," + "\"total_amount\":\"" + total_amount
				+ "\"," + "\"subject\":\"" + subject + "\"," + "\"body\":\"" + body + "\","
				+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

		String result = "";
		try {
			// 4、请求
			result = alipayClient.pageExecute(alipayRequest).getBody();
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}

		return result;// 支付跳转页的代码，一个form表单，来到扫码页
	}

	/** 异步通知的处理 */
	@ResponseBody
	@RequestMapping("/pay/Async")
	public String payAsync(HttpServletRequest request) throws UnsupportedEncodingException {
		log.debug("支付宝支付异步通知完成....");

		// 更新订单状态 ： 未支付 -> 已支付

		// 修改订单的状态
		// 支付宝收到了success说明处理完成，不会再通知

		Map<String, String> params = new HashMap<String, String>();
		
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}

		boolean signVerified = true;
		try {
			signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset,
					AlipayConfig.sign_type);
			System.out.println("验签：" + signVerified);
		} catch (AlipayApiException e) {
		}
		// 商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
		// 支付宝流水号
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
		// 交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

		if (trade_status.equals("TRADE_FINISHED")) {
			// 改订单状态
			log.debug("订单【{}】,已经完成...不能再退款。数据库都改了", out_trade_no);
		} else if (trade_status.equals("TRADE_SUCCESS")) {
			log.debug("订单【{}】,已经支付成功.", out_trade_no);
			// 改订单状态
			
			
			
			
			
			
			
			
		}

		// 如果不给支付宝返回"success"结果，支付宝服务器会在24小时内发送6-7次调用，告知支付完成，需要完成商家业务。
		return "success"; // 只需要完成业务逻辑后告知支付宝服务，操作成功。返回固定字符串"success"
	}

	/** 同步通知处理 */
	@RequestMapping("/pay/success")
	public String paySucccess() {
		log.debug("支付宝支付同步通知完成....");

		// 跳转到会员中心页面，展示我下订单项目。

		return "member/minecrowdfunding";
	}

}
