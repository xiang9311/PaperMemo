package com.xiang.view;

import com.xiang.model.LayoutMove;
import com.xiang.model.LayoutPosition;
import com.xiang.model.LayoutZoom;
import com.xiang.papermemo.MemoActivity;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PullToAddLayout extends RelativeLayout {
	/**
	 * 标签
	 */
	public static final String TAG = "pulltoaddlayout";
	/**
	 * 颜色块数量
	 */
	public static final int colorCount = 6;
	/**
	 * 动画执行的毫秒数
	 */
	public static final int during = 200;
	/**
	 * 动画线程休眠的时间
	 */
	public static final int sleeptime = 20;
	
	
	/********************************   控件       ******************************************/
	/**
	 * 下拉时显示的头部
	 */
	public FrameLayout fl_pullhead = null;
	/**
	 * 下拉时显示的颜色块
	 */
	public ImageView iv_blue = null;
	/**
	 * 下拉时显示的颜色块
	 */
	public ImageView iv_lightblue = null;
	/**
	 * 下拉时显示的颜色块
	 */
	public ImageView iv_lightpink = null;
	/**
	 * 下拉时显示的颜色块
	 */
	public ImageView iv_yellow = null;
	/**
	 * 下拉时显示的颜色块
	 */
	public ImageView iv_red = null;
	/**
	 * 下拉时显示的颜色块
	 */
	public ImageView iv_pink = null;
	/**
	 * 按钮块数组
	 */
	public ImageView[] imageViews = new ImageView[colorCount];
	/**
	 * 下拉保存提示文字
	 */
	public TextView tv_pulltosave = null;
	/**
	 * 标签外面的ll
	 */
	public LinearLayout ll_tags = null;
	/**
	 * 列表
	 */
	public MyListView listView = null;
	/**
	 * ll_content 下面内容部分
	 */
	public LinearLayout ll_content = null;
	/**
	 * imageview的外面的布局
	 */
	public LinearLayout ll_pullhead_items = null;
	
	/*******************************************   状态     ******************************/
	/**
	 * 下拉列表处于的状态
	 * @author 祥祥
	 *
	 */
	public enum pullstate{
		/**
		 * 正常
		 */
		normal,
		/**
		 * 正在下拉
		 */
		pulltoadd,
		/**
		 * 下拉后显示色块
		 */
		showingcolor,
		/**
		 * 显示出编辑框，正在编辑
		 */
		adding,
		/**
		 * 下拉保存
		 */
		pulltosave
	}
	
	private pullstate state = pullstate.normal;
	
	/**
	 * 手指按下的X坐标
	 */
	float downX = 0.0f;
	/**
	 * 手指按下的Y坐标
	 */
	float downY = 0.0f;
	/**
	 * 手指按下的X坐标
	 */
	float lastX = 0.0f;
	/**
	 * 手指按下的Y坐标
	 */
	float lastY = 0.0f;
	
	/**
	 * 下拉与移动的比例
	 */
	float rate = 1.0f;
	/**
	 * 下拉的目标位置 初始值无用 ，下拉后根据顶部控件位置设置
	 */
	float desDistence = 350.0f;
	/**
	 * 下拉的最大位置
	 */
	float maxDistence = 500.0f;
	/**
	 * scale 的最大位置
	 */
	float maxDisScale = 400.0f;
	
	

	//如果执行该逻辑的滑动，则返回true。外层控件将不会滑动
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
//		Log.d(TAG,"state:"+ev.getActionMasked());
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			downX = ev.getX();
			downY = ev.getY();
			lastX = ev.getX();
			lastY = ev.getY();
			desDistence = fl_pullhead.getMeasuredHeight();
			break;
		case MotionEvent.ACTION_MOVE:
			//如果是初始状态的下拉，则转换成下拉状态
			if(pullstate.normal == state && this.getY() == 0){
				//如果已经位于顶部
				if(ev.getY() > downY){
					state = pullstate.pulltoadd;
					pulldown(ev);
					return true;
				}
			}
			//正在下拉的状态
			else if(pullstate.pulltoadd == state){
//				Log.d(TAG,"move");
				pulldown(ev);
				return true;
			}
			//如果正在编辑时候下拉
			else if(pullstate.adding == state){
				state = pullstate.pulltosave;
				pulldownadd(ev);
			}
			//如果正在下拉保存
			else if(pullstate.pulltosave == state){
				pulldownadd(ev);
			}
			else if(pullstate.showingcolor == state && ev.getY() > lastY && ll_pullhead_items.getY() == 0){
				pulldown(ev);
//				state = pullstate.pulltoadd;
				break;
			}
			break;
		case MotionEvent.ACTION_UP:
			boolean pullisok = isPullOk(ev);
			//下拉取消showcolor
			if(pullstate.showingcolor == state){
				if(isPullCloseColorOk(ev)){
					state = pullstate.normal;
					layoutCloseColor();
				}
				else{
					layoutShowColor(ev);
				}
			}
			//如果下拉的幅度够大，下拉成功
			if(pullisok){
				if(pullstate.pulltoadd == state){
					state = pullstate.showingcolor;
					layoutShowColor(ev);
				}
				if(pullstate.pulltosave == state){
					state = pullstate.normal;
					layoutToNormal(ev);
					//保存编辑的内容
					listener.saveMemo();
				}
			}
			//如果下拉距离不够
			else{
				if(pullstate.pulltoadd == state){
					state = pullstate.normal;
					layoutAddToNormal(ev);
				}
				if(pullstate.pulltosave == state){
					state = pullstate.adding;
					layoutToAdding(ev);
				}
			}
			break;
			default:break;
		}
		
		super.dispatchTouchEvent(ev);
		return false;
	}


	private void layoutCloseColor() {
		LayoutMove move = new LayoutMove(ll_content,ll_content.getLeft(),
				0 ,
				ll_content.getRight(),
				(int) (ll_content.getBottom() - ll_content.getTop() + 0));
		new MoveLayoutThread(move).start();
		for(int i = 0 ; i < colorCount ; i ++){
			LayoutZoom zoom = new LayoutZoom(imageViews[i],0.0f,0.0f);
			new SizeLayoutThread(zoom).start();
		}
	}


	private boolean isPullCloseColorOk(MotionEvent ev) {
		if(ev.getY() - downY >= 300)
			return true;
		return false;
	}


	private void layoutToAdding(MotionEvent ev) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * 首次下拉距离不够，回到初始位置
	 * @param ev
	 */
	private void layoutAddToNormal(MotionEvent ev) {
		LayoutMove move = new LayoutMove(ll_content,ll_content.getLeft(),
				0 ,
				ll_content.getRight(),
				(int) (ll_content.getBottom() - ll_content.getTop() + 0));
		new MoveLayoutThread(move).start();
		for(int i = 0 ; i < colorCount ; i ++){
			LayoutZoom zoom = new LayoutZoom(imageViews[i],0.0f,0.0f);
			new SizeLayoutThread(zoom).start();
		}
	}


	private void layoutToNormal(MotionEvent ev) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 下拉足够，显示颜色块
	 * @param ev
	 */
	private void layoutShowColor(MotionEvent ev) {
		LayoutMove move = new LayoutMove(ll_content,ll_content.getLeft(),
				(int) desDistence,
				ll_content.getRight(),
				(int) (ll_content.getBottom() - ll_content.getTop() + desDistence));
		new MoveLayoutThread(move).start();
		for(int i = 0 ; i < colorCount ; i ++){
			LayoutZoom zoom = new LayoutZoom(imageViews[i],1.0f,1.0f);
			new SizeLayoutThread(zoom).start();
		}
	}


	private boolean isPullOk(MotionEvent ev) {
		if(imageViews[0].getScaleX() >= 1.0f)
			return true;
		return false;
	}


	private void pulldownadd(MotionEvent ev) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 从 normal到add的下拉
	 * @param ev 
	 */
	private void pulldown(MotionEvent ev) {
		float y = ev.getY();
		float my = y - lastY;
		float minus = y - downY;
		float maxd = maxDistence;
		lastY = y;
		
		if(pullstate.showingcolor == state)
			maxd = 350;
		
//		//计算下拉跟运动的比率，显示弹性效果
		if(minus > maxd)
			rate = 0;
		else if(minus > 0)
			rate = (maxd - minus) / maxd;
//		Log.d(TAG,state+":"+my);
		
		if(pullstate.showingcolor != state){
			//计算scale
			float scale = 1.0f;
			if(minus > maxDisScale)
				scale = 1.15f;
			else
				scale = (float) ((minus / maxDisScale) * 1.15);
			for(int i = 0 ;i < colorCount ; i ++){
				imageViews[i].setScaleX(scale);
				imageViews[i].setScaleY(scale);
			}
		}
		
		ll_content.layout(ll_content.getLeft(), 
				(int) (ll_content.getTop() + my * rate),
				ll_content.getRight(),
				(int) (ll_content.getBottom() + my * rate));
		
		
		
	}


	/**
	 * 接口监听
	 */
	private PullToAddListener listener = null;
	
	
	/**
	 * 上下文
	 */
	private Context context = null;

	public PullToAddLayout(Context context) {
		
		super(context);
		this.context = context;
		
	}
	public PullToAddLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	public PullToAddLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}
	
	class SizeLayoutThread extends Thread{
		
		public LayoutZoom zoom;
		
		public SizeLayoutThread(LayoutZoom zoom){
			this.zoom = zoom;
		}
		
		@Override
		public void run() {
			long time = System.currentTimeMillis();
			float size_x = zoom.view.getScaleX();
			float size_y = zoom.view.getScaleY();
			while(true){
				try {
					Thread.sleep(sleeptime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long t = System.currentTimeMillis();
				float y = t - time;
				if(y >= during)
					break;
				float tosize = (zoom.sizeX - size_x) / 200 * y + size_x;
				LayoutZoom z = new LayoutZoom(zoom.view, tosize, tosize);
				Message msg = new Message();
				msg.what = 91;
				msg.obj = z;
				MemoActivity.that.mHandler.sendMessage(msg);
			}
			Message msg = new Message();
			msg.what = 91;
			msg.obj = zoom;
			MemoActivity.that.mHandler.sendMessage(msg);
		}
		
	}
	
	/**
	 * 移动layout的线程从当前位置一定到move中定义的目标位置
	 * @author 祥祥
	 *
	 */
	class MoveLayoutThread extends Thread{
		LayoutMove move;
		public MoveLayoutThread(LayoutMove move){
			this.move = move;
		}

		@Override
		public void run() {
			long time = System.currentTimeMillis();
			float startY = move.layout.getTop();
			float startB = move.layout.getBottom();
			while(true){
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long t = System.currentTimeMillis();
				float y = t - time;
				if(y >= during)
					break;
				y = y / during * (startY - move.top);
				Log.d(TAG,y+"");
				Message msg = new Message();
				msg.what = 90;
				msg.obj = new LayoutMove(move.layout, move.layout.getLeft(), 
						(int)(startY - y), move.layout.getRight(), (int) (startB - y));
				MemoActivity.that.mHandler.sendMessage(msg);
			}
			Message msg = new Message();
			msg.what = 90;
			msg.obj = new LayoutMove(move.layout, move.layout.getLeft(),
					(int) move.top, 
					move.layout.getRight(),
					(int) (startB - startY + move.top));
			MemoActivity.that.mHandler.sendMessage(msg);
		}
		
	}
	
	
	interface PullToAddListener{
		/**
		 * 下拉保存memo
		 */
		public void saveMemo();
	}
	
}
