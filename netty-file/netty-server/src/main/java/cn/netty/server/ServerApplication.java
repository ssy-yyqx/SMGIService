package cn.netty.server;

import cn.netty.server.typeServer.HeartSever;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import cn.netty.server.typeServer.FileServer;


import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@PropertySource(value= "classpath:/application.properties")
public class ServerApplication {

	public static void main(String[] args) throws Exception {

		//SpringApplication.run(ServerApplication.class, args);
		ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);
		FileServer fileServer = context.getBean(FileServer.class);
		// openBrowse("http://localhost:10003/monitor.do");
		fileServer.start();
		// HeartSever heartSever = context.getBean(HeartSever.class);
		// heartSever.start();
	}


	/**
	 * @title 使用默认浏览器打开
	 * @param url 要打开的网址
	 */
	private static void openBrowse(String url) throws Exception {
		List<String> list = new ArrayList<String>();
		list.add("cmd");
		list.add("/c");
		list.add("start");
		list.add(url);
		list.toArray();
		Runtime.getRuntime().exec(list.toArray(new String[0]));

	}


}