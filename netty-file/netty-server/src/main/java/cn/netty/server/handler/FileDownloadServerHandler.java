package cn.netty.server.handler;

import cn.netty.server.utils.MinioClientUtils;
import cn.netty.common.utils.file.FileMD5Utils;
import cn.netty.common.utils.file.FileTypeUtils;
import cn.netty.common.utils.file.FileUtils;
import cn.netty.common.utils.uuid.IdUtils;
import cn.netty.server.web.entity.FileData;
import cn.netty.server.web.service.FileDataService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import cn.netty.server.components.ChannelRepository;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_DISPOSITION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 其他业务拓展模板参考
 */
@Component(value = "fileDownloadServerHandler")
@ChannelHandler.Sharable
public class FileDownloadServerHandler extends ChannelInboundHandlerAdapter {
	public Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MinioClientUtils minioClientUtils;

	@Autowired
	private FileDataService fileDataService;

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		// 接收到请求
		log.info("接受到下载请求，时间：" + new Date());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {

		FullHttpRequest request = (FullHttpRequest) msg;
		//对请求的解码结果进行判断：
		if (!request.decoderResult().isSuccess()) {
			// 400
			sendError(ctx, "参数解码错误",HttpResponseStatus.BAD_REQUEST);
			return;
		}

		//获取请求uri路径
		final String uri = request.uri();
		//对url进行分析，返回本地系统
		sanitizeUri(uri, ctx, request);

		//如果使用Chunked编码，最后则需要发送一个编码结束的看空消息体，进行标记，表示所有消息体已经成功发送完成。
		ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		//如果当前连接请求非Keep-Alive ，最后一包消息发送完成后 服务器主动关闭连接
		if (!HttpUtil.isKeepAlive(request)) {
			lastContentFuture.addListener(ChannelFutureListener.CLOSE);
		}
		ctx.flush();
	}

	/**
	 * <B>方法名称：</B>解析URI<BR>
	 * <B>概要说明：</B>对URI进行分析<BR>
	 * @param uri netty包装后的字符串对象
	 * @return path 解析结果
	 */
	private void sanitizeUri(String uri, ChannelHandlerContext ctx, FullHttpRequest request) {
		System.out.println(ctx.name());
		if (uri.contains("/download/")) {
			// 文件下载
			//对请求方式进行判断：如果不是get方式（如post方式）则返回异常
			if (request.method() != HttpMethod.GET) {
				// 405
				sendError(ctx, "请求方式错误", HttpResponseStatus.METHOD_NOT_ALLOWED);
				return;
			}
			String fileId = uri.substring(uri.indexOf("/download") + "/download".length() + 1);
			download(fileId, ctx, request);
		} else if (uri.contains("/upload")) {
			// 文件上传
			//对请求方式进行判断：如果不是post方式 则返回异常
			if (request.method() != HttpMethod.POST) {
				// 405
				sendError(ctx, "请求方式错误", HttpResponseStatus.METHOD_NOT_ALLOWED);
				return;
			}
			try {
				String fileId = upload(request);
				sendSuccess(ctx, fileId);
			} catch (IOException e) {
				sendError(ctx, "服务器内部错误", HttpResponseStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			sendError(ctx, "请求路径不正确", HttpResponseStatus.BAD_REQUEST);
			return;
		}
	}

		private String upload(FullHttpRequest request) throws IOException {
			MultipartRequest multipartBody = getMultipartBody(request);
			Map<String, FileUpload> fileUploads = multipartBody.getFileUploads();
			StringBuilder fileId = new StringBuilder();
			//输出文件信息
			for (String key : fileUploads.keySet()) {
				//获取文件对象
				FileUpload file = fileUploads.get(key);
				log.info("fileName is" + file.getFile().getPath());
				fileId.append(uploadFileData(file, multipartBody.getParams())).append(",");
			}
			return fileId.substring(0, fileId.length() - 1);
		}

		private String uploadFileData(FileUpload file, JSONObject param) throws IOException {
			QueryWrapper<FileData> queryWrapper = new QueryWrapper<>();
			String md5 = FileMD5Utils.getFileMD5String(file.getFile());
			queryWrapper.eq("md5", md5);
			FileData fileData = fileDataService.getOne(queryWrapper);
			// 如果文件已存在，则返回
			if (!Optional.ofNullable(fileData).isPresent()) {
				fileData = new FileData();
				fileData.setFileSize(file.length());
				fileData.setFileName(file.getFilename());
				fileData.setFileId(IdUtils.fastSimpleUUID());
				fileData.setMd5(md5);
				fileData.setExtend(FileUtils.suffix(file.getFilename()));
				fileData.setBucketName(param.getString("bucket"));
				fileData.setObjectName(fileData.getFileId() + File.separator + IdUtils.fastSimpleUUID() + "." + fileData.getExtend());
				//获取文件流
				InputStream in = new FileInputStream(file.getFile());
				minioClientUtils.uploadMinio(fileData.getBucketName(), fileData.getObjectName(), in, FileTypeUtils.getFileType(file.getFile()));
				fileDataService.save(fileData);
				in.close();
			}
			return fileData.getFileId();
		}

	/**
	 * 解析文件上传
	 * @param request
	 */
	private static MultipartRequest getMultipartBody(FullHttpRequest request) {
		try {
			//创建HTTP对象工厂
			HttpDataFactory factory = new DefaultHttpDataFactory(true);
			//使用HTTP POST解码器
			HttpPostRequestDecoder httpDecoder = new HttpPostRequestDecoder(factory, request);
			httpDecoder.setDiscardThreshold(0);
			if (httpDecoder != null) {
				//获取HTTP请求对象
				final HttpContent chunk = (HttpContent) request;
				//加载对象到加吗器。
				httpDecoder.offer(chunk);
				if (chunk instanceof LastHttpContent) {
					//自定义对象bean
					MultipartRequest multipartRequest = new MultipartRequest();
					//存放文件对象
					Map<String, FileUpload> fileUploads = new HashMap<>();
					//存放参数对象
					JSONObject body = new JSONObject();
					//通过迭代器获取HTTP的内容
					List<InterfaceHttpData> InterfaceHttpDataList = httpDecoder.getBodyHttpDatas();
					for (InterfaceHttpData data : InterfaceHttpDataList) {
						//如果数据类型为文件类型，则保存到fileUploads对象中
						if (data != null && InterfaceHttpData.HttpDataType.FileUpload.equals(data.getHttpDataType())) {
							FileUpload fileUpload = (FileUpload) data;
							fileUploads.put(data.getName(), fileUpload);
						}
						//如果数据类型为参数类型，则保存到body对象中
						if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
							Attribute attribute = (Attribute) data;
							body.put(attribute.getName(), attribute.getValue());
						}
					}
					//存放文件信息
					multipartRequest.setFileUploads(fileUploads);
					//存放参数信息
					multipartRequest.setParams(body);
					return multipartRequest;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void download(String fileId, ChannelHandlerContext ctx, FullHttpRequest request) {
		FileData fileData = fileDataService.getById(fileId);
		if (!Optional.ofNullable(fileData).isPresent()) {
			sendError(ctx, "未找到下载的文件", HttpResponseStatus.NOT_FOUND);
			return;
		}

		final double totalSize = fileData.getFileSize();
		InputStream inputStream = minioClientUtils.downloadFile(fileData.getBucketName(), fileData.getObjectName());
		//建立响应对象
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
		//设置响应信息
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileData.getFileSize());
		//设置响应头
		setContentTypeHeader(response, fileData.getObjectName());
		response.headers().set(CONTENT_DISPOSITION, "attachment;filename=" + fileData.getFileName());
		//如果一直保持连接则设置响应头信息为：HttpHeaders.Values.KEEP_ALIVE
		if (HttpUtil.isKeepAlive(request)) {
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}
		//进行写出
		ctx.write(response);

		//构造发送文件线程，将文件写入到Chunked缓冲区中
		ChannelFuture sendFileFuture;
		//写出ChunkedFile
		sendFileFuture = ctx.write(new ChunkedStream(inputStream, 8192), ctx.newProgressivePromise());
		//添加传输监听
		AtomicInteger i = new AtomicInteger(0);
		sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
			@Override
			public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
				if (i.incrementAndGet() % 10000 == 0) {
					log.debug("Transfer progress: " + String.format("%.3f", progress / totalSize));
				}
			}
			@Override
			public void operationComplete(ChannelProgressiveFuture future) throws Exception {
				if (future.isSuccess() && future.isDone()) {
					log.info("Transfer complete.");
					inputStream.close();
					future.channel().close();
				}
			}
		});
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		ctx.close();
	}

	//错误信息
	private static void sendError(ChannelHandlerContext ctx, Object message, HttpResponseStatus status) {
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("code", status.code());
		responseMap.put("msg", status.reasonPhrase());
		responseMap.put("data", message);

		//建立响应对象
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(JSONObject.toJSONString(responseMap), CharsetUtil.UTF_8));
		//设置响应头信息
		response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
		//使用ctx对象写出并且刷新到SocketChannel中去 并主动关闭连接(这里是指关闭处理发送数据的线程连接)
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	//成功信息
	private static void sendSuccess(ChannelHandlerContext ctx, Object message) {
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("code", 200);
		responseMap.put("msg", "success");
		responseMap.put("data", message);
		//建立响应对象
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(JSONObject.toJSONString(responseMap), CharsetUtil.UTF_8));
		//设置响应头信息
		response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
		//使用ctx对象写出并且刷新到SocketChannel中去 并主动关闭连接(这里是指关闭处理发送数据的线程连接)
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}


	private static void setContentTypeHeader(HttpResponse response, String fileName) {
		//使用mime对象获取文件类型
		response.headers().set(CONTENT_TYPE, "application/octet-stream");
	}
}

class MultipartRequest {
	private Map<String, FileUpload> fileUploads;
	private JSONObject params;
	public Map<String, FileUpload> getFileUploads() {
		return fileUploads;
	}
	public void setFileUploads(Map<String, FileUpload> fileUploads) {
		this.fileUploads = fileUploads;
	}
	public JSONObject getParams() {
		return params;
	}
	public void setParams(JSONObject params) {
		this.params = params;
	}
}
