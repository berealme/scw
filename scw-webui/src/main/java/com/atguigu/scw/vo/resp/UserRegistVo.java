package com.atguigu.scw.vo.resp;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 用户注册提交的数据VO
 */
@ToString
@ApiModel
@Data
public class UserRegistVo implements Serializable{
	@ApiModelProperty("手机号")
	private String loginacct;
	@ApiModelProperty("密码")
	private String userpswd;
	@ApiModelProperty("邮箱")
	private String email;
	@ApiModelProperty("验证码")
	private String code;
	@ApiModelProperty("用户类型:1企业,0个人")
	private String usertype ;
	@ApiModelProperty("用户名称")
	private String username ;
}
