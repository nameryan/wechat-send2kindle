package com.using.weixin.wxtools.parser;

import org.jdom.Element;
import org.jdom.JDOMException;

import com.using.weixin.wxtools.vo.recv.WxRecvMsg;
import com.using.weixin.wxtools.vo.recv.WxRecvTextMsg;

public class WxRecvTextMsgParser extends WxRecvMsgBaseParser{

	@Override
	protected WxRecvTextMsg parser(Element root, WxRecvMsg msg) throws JDOMException {
		return new WxRecvTextMsg(msg, getElementText(root, "Content"));
	}

	
}
