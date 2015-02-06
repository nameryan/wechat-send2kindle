package com.using.weixin.wxtools.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.using.weixin.wxtools.vo.recv.WxRecvMsg;
import com.using.weixin.wxtools.vo.send.WxSendMsg;


public final class WxMsgKit {
	
	private static final Map<String, WxRecvMsgParser> recvParserMap = new HashMap<String, WxRecvMsgParser>();
	
	static {
		// 文本消息解析程序
		recvParserMap.put("text", new WxRecvTextMsgParser());
		// 链接消息解析程序
		recvParserMap.put("link", new WxRecvLinkMsgParser());
		// 地址消息解析程序
		recvParserMap.put("location", new WxRecvGeoMsgParser());
		// 图片消息解析程序
		recvParserMap.put("image", new WxRecvPicMsgParser());
		// 事件消息解析程序
		recvParserMap.put("event", new WxRecvEventMsgParser());
		// 语音消息
		recvParserMap.put("voice", new WxRecvVoiceMsgParser());
		
	}
	
	public static WxRecvMsg parse(InputStream in) throws JDOMException, IOException {
		Document dom = new SAXBuilder().build(in);
		Element msgType = dom.getRootElement().getChild("MsgType");
		if(null != msgType) {
			String txt = msgType.getText().toLowerCase();
			WxRecvMsgParser parser = recvParserMap.get(txt);
			if(null != parser) {
				return parser.parser(dom);
			} else {
				System.out.println(txt);
			}
		}
		return null;
	}
	
	public static Document parse(WxSendMsg msg) throws JDOMException {
		return msg.toDocument();
	}
}
