package com.atguigu.scw.vo.req;

import lombok.Data;
import lombok.ToString;

//调用远程服务时，用于传递数据
@Data
@ToString
public class OrderInfoSubmitVo {

	private String accessToken;
	
	private String address;// 收货地址id
	private Byte invoice;// 0代表不要 1-代表要
	private String invoictitle;// 发票抬头
	private String remark;// 订单的备注
	
	private Integer returnId ;
	private Integer projectId ;
	
	private Integer countNum ;
	
}
