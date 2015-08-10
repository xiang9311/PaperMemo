package com.xiang.model;

import android.view.ViewGroup;

public class LayoutMove {
	public ViewGroup layout;
	public int left;
	public int top;
	public int right;
	public int bottom;
	public LayoutMove(ViewGroup layout, int left, int top, int right, int bottom) {
		super();
		this.layout = layout;
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
}
