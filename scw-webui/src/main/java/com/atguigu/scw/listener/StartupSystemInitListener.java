package com.atguigu.scw.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于监听application对象的创建和销毁。
 * @author Administrator
 *
 */
//@WebListener
public class StartupSystemInitListener implements ServletContextListener {

	Logger logger = LoggerFactory.getLogger(StartupSystemInitListener.class);
	
	//application对象被创建，执行初始化方法
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.debug("服务器启动，StartupSystemInitListener监听器开始监听：");
		ServletContext application = sce.getServletContext();
		String contextPath = application.getContextPath();
		logger.debug("将上下文路径存放到applicaiton域中：contextPath={}", contextPath);
		application.setAttribute("PATH", contextPath);
	}

	//application对象被销毁，执行销毁方法
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.debug("服务器停止，StartupSystemInitListener监听器开始执行销毁操作");
	}

}
