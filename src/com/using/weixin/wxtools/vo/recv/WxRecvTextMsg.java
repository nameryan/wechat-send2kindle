package com.using.weixin.wxtools.vo.recv;


public class WxRecvTextMsg extends WxRecvMsg {
	private String content;
	
	public WxRecvTextMsg(WxRecvMsg msg,String content) {
		super(msg);
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
