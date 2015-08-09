package com.xiang.view;

import com.xiang.model.LayoutPosition;
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
	 * ������Ŀ��λ��
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
			else if(pullstate.showingcolor == state){
				break;
			}
			break;
		case MotionEvent.ACTION_UP:
			boolean pullisok = isPullOk(ev);
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


	private void layoutToAdding(MotionEvent ev) {
		// TODO Auto-generated method stub
		
	}


	private void layoutAddToNormal(MotionEvent ev) {
		// TODO Auto-generated method stub
		
	}


	private void layoutToNormal(MotionEvent ev) {
		// TODO Auto-generated method stub
		
	}


	private void layoutShowColor(MotionEvent ev) {
		new Thread(new Runnable(){

			@Override
			public void run() {
				long time = System.currentTimeMillis();
				long starttime = 0;
				int recometime = 200;
				float startY = ll_content.getTop();
				float startB = ll_content.getBottom();
				while(true){
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long t = System.currentTimeMillis();
					float y = t - time;
					if(y >= 200)
						break;
					y = y / 200 * (startY - desDistence);
					Log.d(TAG,y+"");
					Message msg = new Message();
					msg.what = 90;
					msg.obj = new LayoutPosition(ll_content.getLeft(), 
							(int)(startY - y), ll_content.getRight(), (int) (startB - y));
					MemoActivity.that.mHandler.sendMessage(msg);
				}
				Message msg = new Message();
				msg.what = 90;
				msg.obj = new LayoutPosition(ll_content.getLeft(), (int) desDistence, ll_content.getRight(), (int) (startB - startY + desDistence));
				MemoActivity.that.mHandler.sendMessage(msg);
			}
			
		}).start();
	}


	private boolean isPullOk(MotionEvent ev) {
		if(ev.getY() - downY > desDistence)
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
		lastY = y;
		
//		//�����������˶��ı��ʣ���ʾ����Ч��
		if(minus > maxDistence)
			rate = 0;
		else if(minus > 0)
			rate = (maxDistence - minus) / maxDistence;
//		Log.d(TAG,state+":"+my);
		
		//����scale
		float scale = 1.0f;
		if(minus > maxDisScale)
			scale = 1.1f;
		else
			scale = (float) ((minus / maxDisScale) * 1.1);
		
		ll_content.layout(ll_content.getLeft(), 
				(int) (ll_content.getTop() + my * rate),
				ll_content.getRight(),
				(int) (ll_content.getBottom() + my * rate));
		
//		fl_pullhead.layout(fl_pullhead.getLeft(),
//				(int) (fl_pullhead.getTop() + my * rate / 2),
//				fl_pullhead.getRight(),
//				(int) (fl_pullhead.getBottom() + my * rate * 2));
		
//		ll_pullhead_items.layout(ll_pullhead_items.getLeft(),
//				(int) (ll_pullhead_items.getTop() + my * rate / 2),
//				ll_pullhead_items.getRight(),
//				(int) (ll_pullhead_items.getBottom() + my * rate * 2));
		
		for(int i = 0 ;i < colorCount ; i ++){
			imageViews[i].setScaleX(scale);
			imageViews[i].setScaleY(scale);
		}
		
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
	
	
	interface PullToAddListener{
		/**
		 * ��������memo
		 */
		public void saveMemo();
	}
	
}
