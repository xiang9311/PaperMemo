package com.xiang.model;

public class Memo {
	
	public enum yColor{
		blue,lightblue,lightpink,pink,red1,yellow
	}
	
	public String content;
	public String time;
	public yColor color;
	
	public Memo(String content, String time, yColor color) {
		super();
		this.content = content;
		this.time = time;
		this.color = color;
	}
	
}
