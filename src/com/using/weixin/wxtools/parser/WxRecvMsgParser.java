package com.using.weixin.wxtools.parser;

import org.jdom.Document;
import org.jdom.JDOMException;

import com.using.weixin.wxtools.vo.recv.WxRecvMsg;

public interface WxRecvMsgParser {
	WxRecvMsg parser(Document doc) throws JDOMException;
}
