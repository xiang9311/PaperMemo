package com.xiang.model;

import android.view.View;

public class LayoutZoom {
	public View view;
	public float sizeX;
	public float sizeY;
	public LayoutZoom(View view, float sizeX, float sizeY) {
		super();
		this.view = view;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	public long delay = 0;
	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	
}
