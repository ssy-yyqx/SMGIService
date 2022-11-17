package cn.netty.server.typeServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class FileServer {

	@Autowired
	@Qualifier("fileDownloadServerBootstrap")
	private ServerBootstrap fileDownloadServerBootstrap;

	@Autowired
	@Qualifier("fileUploadServerBootstrap")
	private ServerBootstrap fileUploadServerBootstrap;

	@Autowired
	@Qualifier("fileDownloadAddress")
	private InetSocketAddress tcpIpAndPort;


	private Channel serverChannel;


	//@PostConstruct（会导致其他东西不运行）
	public void start() throws Exception {
		fileDownloadServerBootstrap.bind(tcpIpAndPort).sync().channel().closeFuture().sync();
	}

	//@PreDestroy
	public void stop() throws Exception {
		serverChannel.close();
		serverChannel.parent().close();
	}

	public ServerBootstrap getFileDownloadServerBootstrap() {
		return fileDownloadServerBootstrap;
	}

	public void setFileDownloadServerBootstrap(ServerBootstrap fileDownloadServerBootstrap) {
		this.fileDownloadServerBootstrap = fileDownloadServerBootstrap;
	}

	public InetSocketAddress getTcpPort() {
		return tcpIpAndPort;
	}

	public void setTcpPort(InetSocketAddress tcpPort) {
		this.tcpIpAndPort = tcpPort;
	}
}
