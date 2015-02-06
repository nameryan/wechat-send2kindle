package com.using.weixin.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.using.weixin.common.ApiTools;
import com.using.weixin.wxtools.WeiXinTools;
import com.using.weixin.wxtools.vo.recv.WxRecvEventMsg;
import com.using.weixin.wxtools.vo.recv.WxRecvGeoMsg;
import com.using.weixin.wxtools.vo.recv.WxRecvMsg;
import com.using.weixin.wxtools.vo.recv.WxRecvPicMsg;
import com.using.weixin.wxtools.vo.recv.WxRecvTextMsg;
import com.using.weixin.wxtools.vo.recv.WxRecvVoiceMsg;
import com.using.weixin.wxtools.vo.send.WxSendMsg;
import com.using.weixin.wxtools.vo.send.WxSendMusicMsg;
import com.using.weixin.wxtools.vo.send.WxSendNewsMsg;
import com.using.weixin.wxtools.vo.send.WxSendTextMsg;

import com.using.weixin.common.JavaMail3;
import com.using.weixin.common.SendGmail;
import com.using.weixin.common.JdbcTools;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 微信消息处理 请求地址 http://域名/weixin.do
 */
public class IndexServletAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// token标识
	private static final String TOKEN = "weixin141200";
  
    private final String STRING_WELLCOME = "本公众号支持将朋友圈中的文章发送到kindle设备中，您只需要复制文章地址后发送给我，我们将会把文章自动推送到您的账号所绑定的Kindle设备。\n详细使用说明：http://goo.gl/XgCvHM \n回复kindle推送email地址绑定帐号。\n";

	private Logger logger = Logger.getLogger("IndexServletAction"); 
  
    private JdbcTools jdbc = new JdbcTools();
  
	/**
	 * post请求接受用户输入的消息，和消息回复
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			WxRecvMsg msg = WeiXinTools.recv(request.getInputStream());
          
          	String fromUser = msg.getFromUser(); //获取用户Id
            logger.log(Level.INFO, "fromUser="+fromUser);
           
			WxSendMsg sendMsg = WeiXinTools.builderSendByRecv(msg);

			/** -------------------1.接受到的文本消息，回复处理-------------------------- */
			if (msg instanceof WxRecvTextMsg) {
				WxRecvTextMsg recvMsg = (WxRecvTextMsg) msg;
				// 用户输入的内容
				String text = recvMsg.getContent().trim();
              
               if (text.equals("进度") || text.equals("帮助") || text.equals("？")) {
					String retMsg = "目前已经基本完成功能，正在后期测试完善阶段，也希望你提出宝贵的意见。";
                   sendMsg = new WxSendTextMsg(sendMsg,retMsg);
					WeiXinTools.send(sendMsg, response.getOutputStream());
					return;
                }
              else if(isNameAdressFormat(text)){                
                //用户输入的是email地址
                if (isNameKindleAdressFormat(text)){
                    //用户输入的是kindle或多看地址
                	jdbc.insertUserInfo(fromUser,text);
                
                	sendMsg = new WxSendTextMsg(sendMsg,"绑定成功，您输入的kindle推送帐号是"+text);
					WeiXinTools.send(sendMsg, response.getOutputStream());
                	return;                  
                }else{
                  sendMsg = new WxSendTextMsg(sendMsg,"很抱歉，目前只支持推送到kindle账号或多看账号，不支持其他email地址！");
					WeiXinTools.send(sendMsg, response.getOutputStream());
                	return;
                }      
              }
                else if (text.indexOf("http://")>=0
                         || text.indexOf("https://")>=0 
                        ){                 
                  String emailAddr = jdbc.queryUserEmail(fromUser);
                  
                  final String STRING_SEND_OK = "文章内容已推送到您的kindle账户"+emailAddr+"，请在kindle设备上打开wifi同步内容。";
                  final String STRING_SEND_FAIL = "获取网页内容出错，请联系公众号。";
                  final String STRING_NEED_EMAIL = "很抱歉，您还没有绑定您的kindle推送帐号。回复您的kindle设备的推送邮箱立刻绑定！";
                  String retMsg;
                  int ret = 0;
                  
                  //send email
                  //SendGmail.main(null);
                  
                  //logger.log(Level.INFO, "emailAddr="+emailAddr);
                  if (emailAddr == null){
                    retMsg = STRING_NEED_EMAIL;
                  }else{
                    JavaMail3 mail = new JavaMail3();                  
                  
				  	if(mail.main(emailAddr,text)){
                   	 retMsg= STRING_SEND_OK;
                     ret = 1;
                  	}else{
                      retMsg = STRING_SEND_FAIL;
                      ret = -1;
                  	}
                    jdbc.insertUserLog(fromUser,emailAddr,text,ret);
                  }
                  
                  
                  //logger.log(Level.INFO, "retMsg="+retMsg);
                  sendMsg = new WxSendTextMsg(sendMsg,retMsg);
				  WeiXinTools.send(sendMsg, response.getOutputStream());
                  return;
                  
				} else {
					// 文本消息回复
					sendMsg = new WxSendTextMsg(sendMsg, "以下是你发送的内容：" + text);
					WeiXinTools.send(sendMsg, response.getOutputStream());
					return;
				}
				
				
				/** ----------- 消息回复示例：文字回复、单(多)图文回复、音乐回复 end ------------- */
				
				
			}

			/** -------------------2.接受到的事件消息-------------------------- */
			else if (msg instanceof WxRecvEventMsg) {
				WxRecvEventMsg recvMsg = (WxRecvEventMsg) msg;
				String event = recvMsg.getEvent();

				if ("subscribe".equals(event)) {
					// 订阅消息
					sendMsg = new WxSendTextMsg(sendMsg, STRING_WELLCOME);
					WeiXinTools.send(sendMsg, response.getOutputStream());
					return;
				} else if ("unsubscribe".equals(event)) {
					// 取消订阅

					return;

				} else if ("CLICK".equals(event)) {
					// 自定义菜单点击事件
					String eventKey = recvMsg.getEventKey();

					// 判断自定义菜单中的key回复消息
					if ("自定义菜单中的key".equals(eventKey)) {

						return;
					}
				} else {
					// 无法识别的事件消息
					return;
				}

			}

			/** -------------------3.接受到的地理位置信息-------------------------- */
			else if (msg instanceof WxRecvGeoMsg) {
				WxRecvGeoMsg recvMsg = (WxRecvGeoMsg) msg;

				return;
			}

			/** -------------------4.接受到的音频消息-------------------------- */
			else if (msg instanceof WxRecvVoiceMsg) {
				WxRecvVoiceMsg recvMsg = (WxRecvVoiceMsg) msg;

				return;
			}

			/** -------------------5.接受到的图片消息-------------------------- */
			else if (msg instanceof WxRecvPicMsg) {
				WxRecvPicMsg recvMsg = (WxRecvPicMsg) msg;

				return;
			}

			/** ------------------6.接受到的未能识别的消息-------------------- */
			else {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * get请求进行验证服务器是否正常
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 进行接口验证
		 */
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		if (null != timestamp && null != nonce && null != echostr && null != signature) {
			if (WeiXinTools.access(TOKEN, signature, timestamp, nonce)) {
				response.getWriter().write(echostr);
				return;
			}
			return;
		} else {
			return;
		}
	}

  /*
在正则表达式中\w表示任意单个字符范围是a-z,A-Z,0-9,因为在java中\本来就是转义符 
号，如果只写为\w则会发生歧义，甚至错误，因此要写为：\\w 
+的意思就是出现一次以上，所以\\w+就代表任意长度的字符串，但不包括其他特殊字符 
，如_,-,$,&,*等，呵呵，如果真想进行完全的邮件有效性检查，那正则表达式就不止这 
么长了，呵呵，有兴趣的可以自己写写看 

后面的我想就简单了,@必须出现，而且只准出现一次，因此直接写成@就行了 

\\w+.任意字符串后面加上DOT，大家都知道这是域名的特点，另外就是我写成了 
(\\w+.)+，为什么呢，因为邮件服务器有可能是二级域名，三级域名，或者…… 
如果不带()+的话，abc@sina.com有效，而abc@mail.sina.com就是无效的了，因此这个 
是必须的。 

最后是[a-z]{2,3}，考虑到一般的域名最后不会出现数字，大写也很少见（我想一般应 
该忽略大小写的），并且最少不少于两位，如cn,us,等，最多不超过三位，如com,org, 
等，所以就写成了如上形式

  */
  
  
  private boolean isNameAdressFormat(String email){  
        boolean isExist = false;  
       
    /*
	"\\w+@(\\w+.)+[a-z]{2,3}" 
	为了匹配xx.xx_123@xxx.com 这种情况，将\\w改为了. 匹配所有字符
    */
        Pattern p = Pattern.compile(".+@(\\w+.)+[a-z]{2,3}");  
        Matcher m = p.matcher(email);  
        boolean b = m.matches();  
        if(b) {  
            logger.log(Level.INFO, "有效的邮件地址"); 
            isExist=true;  
        } else {  
            logger.log(Level.INFO, "无效的邮件地址"); 
        }  
        return isExist;  
    }
  
  private boolean isNameKindleAdressFormat(String email){  
        boolean isExist = false;  

        Pattern p = Pattern.compile(".+@((free.kindle|kindle|iduokan).)+[a-z]{2,3}");  
        Matcher m = p.matcher(email);  
        boolean b = m.matches();  
        if(b) {  
            logger.log(Level.INFO, "有效的kindle或多看地址");
            isExist=true;  
        } else {  
            logger.log(Level.INFO, "无效的kindle或多看地址");
        }  
        return isExist;  
    }
  
}
