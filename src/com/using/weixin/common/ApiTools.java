package com.using.weixin.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.baidu.bae.api.factory.BaeFactory;
import com.baidu.bae.api.bcms.BaeBcms;
import com.baidu.bae.api.bcms.client.BCMSRestClient;
import com.baidu.bae.api.bcms.model.concrete.MailRequest;

import com.baidu.bae.api.util.BaeEnv;

public class ApiTools {

	
	public static void main(String[] args) {
		jokeApi();
	}
	
	/**
	 * 笑话api
	 * @return
	 */
	public static String jokeApi() {
		String json = HttpRequestTools.getHttpClientHtml("http://api.xiaojianjian.net/api/show.action?m=joke");
		//JSONObject obj = (JSONObject) JSON.parse(json);
		//return obj.get("contextText").toString();
        return json;//ryan.c add
	}
	
	/**
	 * 段子api
	 * @return
	 */
	public static String duanziApi() {
		String json = HttpRequestTools.getHttpClientHtml("http://api.xiaojianjian.net/api/show.action?m=duanzi");
		JSONObject obj = (JSONObject) JSON.parse(json);
		return obj.get("context").toString();
	}
  
  /*
   * 发送email
   *
   */
  public static Boolean sendEmail(String url){
    
    //String webContent = HttpRequestTools.getHttpClientHtml(url);
    
    //获取应用私有的临时文件夹的绝对路径
    String tmpfsPath = BaeEnv.getTmpfsPath();
    
    /*
	//（1）通过工厂类获得BCMSRestClient类实例
	BaeBcms bcms = BaeFactory.getBaeBcms();
	//（2）通过new创建实例
	//BaeBcms bcms = new BCMSRestClient();
	//创建一个队列
	CreateQueueRequest cre_request = new CreateQueueRequest();
	cre_request.setAliasQueueName("0bb302440281d55c39bf1bcedc29fdd2");
	cre_request.setQueueType(QueueType.BCMS_QUEUE_TYPE);
	CreateQueueResponse cre_response = bcms.createQueue(cre_request);

	//获取所创建的队列的名字
	String queueName = cre_response.getQueueName();

	//发送邮件
	MailRequest mailRequest = new MailRequest();
	mailRequest.setQueueName(queueName);
	mailRequest.setMessage("hello world!");
	mailRequest.addMailAddress("name.ryan@gmail.com");
	bcms.mail(mailRequest );
	*/                 
    

    //BCMSRestClient bcms = new BCMSRestClient();
    BaeBcms bcms = BaeFactory.getBaeBcms();
    MailRequest mailRequest = new MailRequest();
    mailRequest.setFrom("sendToKindle");
    mailRequest.setQueueName("0bb302440281d55c39bf1bcedc29fdd2");
    mailRequest.setSubject("mail subject");
    mailRequest.setMessage("send fail???");
    mailRequest.addMailAddress("name.ryan@gmail.com");
    bcms.mail(mailRequest);
    return true;
  }
}
