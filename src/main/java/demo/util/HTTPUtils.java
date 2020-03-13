package demo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class HTTPUtils {
	public static final Logger logger = Logger.getLogger(HTTPUtils.class);

	private final static String OPERATER_NAME = "【HTTP操作】";

	private final static int SUCCESS = 200;

	private final static String UTF8 = "UTF-8";

	private HttpClient client;

	private static HTTPUtils instance = new HTTPUtils();

	/**
	 * 私有化构造器
	 */
	private HTTPUtils() {

		HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = httpConnectionManager.getParams();
		params.setConnectionTimeout(5000);
		params.setSoTimeout(300000);
		params.setDefaultMaxConnectionsPerHost(1000);
		params.setMaxTotalConnections(1000);
		client = new HttpClient(httpConnectionManager);
		client.getParams().setContentCharset(UTF8);
		client.getParams().setHttpElementCharset(UTF8);
	}

	/**
	 * get请求
	 */
	public static String get(String url) {
		return instance.doGet(url);
	}
  
	private String doGet(String url) {
		long beginTime = System.currentTimeMillis();
		String respStr = StringUtils.EMPTY;
		try {
			logger.info(OPERATER_NAME + "开始get通信，目标host:" + url);
			HttpMethod method = new GetMethod(url.toString());
			// 中文转码
			method.getParams().setContentCharset(UTF8);
			try {
				client.executeMethod(method);
			} catch (HttpException e) {

				logger.error(new StringBuffer("发送HTTP GET给\r\n").append(url).append("\r\nHTTP异常\r\n"), e);
			} catch (IOException e) {

				logger.error(new StringBuffer("发送HTTP GET给\r\n").append(url).append("\r\nIO异常\r\n"), e);
			}
			if (method.getStatusCode() == SUCCESS) {
				respStr = method.getResponseBodyAsString();
			}
			// 释放连接
			method.releaseConnection();

			logger.info(OPERATER_NAME + "通讯完成，返回码：" + method.getStatusCode());
			logger.info(OPERATER_NAME + "返回内容：" + method.getResponseBodyAsString());
			logger.info(OPERATER_NAME + "结束..返回结果:" + respStr);
		} catch (Exception e) {
			logger.info(OPERATER_NAME, e);
		}
		long endTime = System.currentTimeMillis();
		logger.info(OPERATER_NAME + "共计耗时:" + (endTime - beginTime) + "ms");

		return respStr;
	}

	/**
	 * POST请求
	 */
	public static String post(String url, String content) {
		return instance.doPost(url, content);
	}

	private String doPost(String url, String content) {
		long beginTime = System.currentTimeMillis();
		String respStr = StringUtils.EMPTY;
		try {
			logger.info(OPERATER_NAME + "开始post通信，目标host:" + url.toString());
			logger.info("通信内容:" + content);
			PostMethod post = new PostMethod(url.toString());
			RequestEntity requestEntity = new StringRequestEntity(content, "application/json;charse=UTF-8", UTF8);
			post.setRequestEntity(requestEntity);
			// 设置格式
			post.getParams().setContentCharset(UTF8);

			client.executeMethod(post);
			if (post.getStatusCode() == SUCCESS) {
				respStr = post.getResponseBodyAsString();
			}

			logger.info(OPERATER_NAME + "通讯完成，返回码：" + post.getStatusCode());
			logger.info(OPERATER_NAME + "返回内容：" + post.getResponseBodyAsString());
			logger.info(OPERATER_NAME + "结束..返回结果:" + respStr);
			post.releaseConnection();

		} catch (Exception e) {
			logger.error(OPERATER_NAME, e);
		}
		long endTime = System.currentTimeMillis();
		logger.info(OPERATER_NAME + "共计耗时:" + (endTime - beginTime) + "ms");
		return respStr;
	}
	
	public static String doPostForm(String url, Map<String, Object> params) {
		HttpClient httpClient = new HttpClient();    
//      httpClient.getHostConfiguration().setProxy("10.192.10.101", 8080); //设置代理 
//      httpClient.getParams().setAuthenticationPreemptive(true);  
		HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager().getParams();  // 设置连接超时时间(单位毫秒)  
		managerParams.setConnectionTimeout(30000);  // 设置读数据超时时间(单位毫秒) 
		managerParams.setSoTimeout(120000);   
		PostMethod postMethod = new PostMethod(url);  // 将请求参数XML的值放入postMethod中 
		postMethod.addRequestHeader("Content-type","application/x-www-form-urlencoded; charset=UTF-8");
		String strResponse = null; 
		StringBuffer buffer = new StringBuffer();  // end 
		try {   //设置参数到请求对象中，重点是map中有几个参数NameValuePair数组也必须设置成几，不然          
			//会空指针异常  
			logger.info(OPERATER_NAME + "开始post通信，目标host:" + url.toString());
			logger.info("通信内容:" + JSON.toJSONString(params));
//			List map = JSONObject.parseObject(content);
			NameValuePair[] nvps = new NameValuePair[params.keySet().size()];  
			int index = 0; 
			for(String key : params.keySet()){   
				nvps[index++]=new NameValuePair(key, params.get(key).toString());  
			}
			postMethod.setRequestBody(nvps);  
			int statusCode = httpClient.executeMethod(postMethod);   
			if (statusCode != HttpStatus.SC_OK) {   
				throw new IllegalStateException("Method failed: " + postMethod.getStatusLine());  
			}  
			BufferedReader reader = null;  
			reader = new BufferedReader(new InputStreamReader(postMethod.getResponseBodyAsStream(), "UTF-8"));
			while ((strResponse = reader.readLine()) != null) {  
				buffer.append(strResponse);  
			}
			logger.info(OPERATER_NAME + "通讯完成，返回码：" + statusCode);
			logger.info(OPERATER_NAME + "结束..返回结果:" + buffer.toString());
		} catch (Exception ex) { 
			throw new IllegalStateException(ex.toString()); 
        } finally {   // 释放连接  
        	postMethod.releaseConnection();  
        }
		return buffer.toString();
	}
	

}