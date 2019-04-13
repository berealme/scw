package com.atguigu.scw.vo.req;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class OrderFormInfoSubmitVo {
	private String address;// 收货地址id
	private Byte invoice;// 0代表不要 1-代表要
	private String invoictitle;// 发票抬头
	private String remark;// 订单的备注
}
