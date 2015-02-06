package com.using.weixin.wxtools.vo.recv;

public class WxRecvVoiceMsg extends WxRecvMsg {
	
	private String event;
	private String eventKey;
	
	public WxRecvVoiceMsg(WxRecvMsg msg,String event,String eventKey) {
		super(msg);
		this.event = event;
		this.eventKey = eventKey;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getEventKey() {
		return eventKey;
	}
	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}
}
