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
	 * ��ǩ
	 */
	public static final String TAG = "pulltoaddlayout";
	/**
	 * ��ɫ������
	 */
	public static final int colorCount = 6;
	/**
	 * ����ִ�еĺ�����
	 */
	public static final int during = 200;
	/**
	 * �����߳����ߵ�ʱ��
	 */
	public static final int sleeptime = 20;
	
	
	/********************************   �ؼ�       ******************************************/
	/**
	 * ����ʱ��ʾ��ͷ��
	 */
	public FrameLayout fl_pullhead = null;
	/**
	 * ����ʱ��ʾ����ɫ��
	 */
	public ImageView iv_blue = null;
	/**
	 * ����ʱ��ʾ����ɫ��
	 */
	public ImageView iv_lightblue = null;
	/**
	 * ����ʱ��ʾ����ɫ��
	 */
	public ImageView iv_lightpink = null;
	/**
	 * ����ʱ��ʾ����ɫ��
	 */
	public ImageView iv_yellow = null;
	/**
	 * ����ʱ��ʾ����ɫ��
	 */
	public ImageView iv_red = null;
	/**
	 * ����ʱ��ʾ����ɫ��
	 */
	public ImageView iv_pink = null;
	/**
	 * ��ť������
	 */
	public ImageView[] imageViews = new ImageView[colorCount];
	/**
	 * ����������ʾ����
	 */
	public TextView tv_pulltosave = null;
	/**
	 * ��ǩ�����ll
	 */
	public LinearLayout ll_tags = null;
	/**
	 * �б�
	 */
	public MyListView listView = null;
	/**
	 * ll_content �������ݲ���
	 */
	public LinearLayout ll_content = null;
	/**
	 * imageview������Ĳ���
	 */
	public LinearLayout ll_pullhead_items = null;
	
	/*******************************************   ״̬     ******************************/
	/**
	 * �����б��ڵ�״̬
	 * @author ����
	 *
	 */
	public enum pullstate{
		/**
		 * ����
		 */
		normal,
		/**
		 * ��������
		 */
		pulltoadd,
		/**
		 * ��������ʾɫ��
		 */
		showingcolor,
		/**
		 * ��ʾ���༭�����ڱ༭
		 */
		adding,
		/**
		 * ��������
		 */
		pulltosave
	}
	
	private pullstate state = pullstate.normal;
	
	/**
	 * ��ָ���µ�X����
	 */
	float downX = 0.0f;
	/**
	 * ��ָ���µ�Y����
	 */
	float downY = 0.0f;
	/**
	 * ��ָ���µ�X����
	 */
	float lastX = 0.0f;
	/**
	 * ��ָ���µ�Y����
	 */
	float lastY = 0.0f;
	
	/**
	 * �������ƶ��ı���
	 */
	float rate = 1.0f;
	/**
	 * ������Ŀ��λ�� ��ʼֵ���� ����������ݶ����ؼ�λ������
	 */
	float desDistence = 350.0f;
	/**
	 * ���������λ��
	 */
	float maxDistence = 500.0f;
	/**
	 * scale �����λ��
	 */
	float maxDisScale = 400.0f;
	
	

	//���ִ�и��߼��Ļ������򷵻�true�����ؼ������Ử��
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
			//����ǳ�ʼ״̬����������ת��������״̬
			if(pullstate.normal == state && this.getY() == 0){
				//����Ѿ�λ�ڶ���
				if(ev.getY() > downY){
					state = pullstate.pulltoadd;
					pulldown(ev);
					return true;
				}
			}
			//����������״̬
			else if(pullstate.pulltoadd == state){
//				Log.d(TAG,"move");
				pulldown(ev);
				return true;
			}
			//������ڱ༭ʱ������
			else if(pullstate.adding == state){
				state = pullstate.pulltosave;
				pulldownadd(ev);
			}
			//���������������
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
			//����ȡ��showcolor
			if(pullstate.showingcolor == state){
				if(isPullCloseColorOk(ev)){
					state = pullstate.normal;
					layoutCloseColor();
				}
				else{
					layoutShowColor(ev);
				}
			}
			//��������ķ��ȹ��������ɹ�
			if(pullisok){
				if(pullstate.pulltoadd == state){
					state = pullstate.showingcolor;
					layoutShowColor(ev);
				}
				if(pullstate.pulltosave == state){
					state = pullstate.normal;
					layoutToNormal(ev);
					//����༭������
					listener.saveMemo();
				}
			}
			//����������벻��
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
	 * �״��������벻�����ص���ʼλ��
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
	 * �����㹻����ʾ��ɫ��
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
	 * �� normal��add������
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
		
//		//�����������˶��ı��ʣ���ʾ����Ч��
		if(minus > maxd)
			rate = 0;
		else if(minus > 0)
			rate = (maxd - minus) / maxd;
//		Log.d(TAG,state+":"+my);
		
		if(pullstate.showingcolor != state){
			//����scale
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
	 * �ӿڼ���
	 */
	private PullToAddListener listener = null;
	
	
	/**
	 * ������
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
	 * �ƶ�layout���̴߳ӵ�ǰλ��һ����move�ж����Ŀ��λ��
	 * @author ����
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
		 * ��������memo
		 */
		public void saveMemo();
	}
	
}
