package com.using.weixin.wxtools.parser;

import org.jdom.Element;
import org.jdom.JDOMException;

import com.using.weixin.wxtools.vo.recv.WxRecvMsg;
import com.using.weixin.wxtools.vo.recv.WxRecvPicMsg;

public class WxRecvPicMsgParser extends WxRecvMsgBaseParser {

	@Override
	protected WxRecvPicMsg parser(Element root, WxRecvMsg msg) throws JDOMException {
		return new WxRecvPicMsg(msg, getElementText(root, "PicUrl"));
	}

}
