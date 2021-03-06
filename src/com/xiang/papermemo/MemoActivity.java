package com.xiang.papermemo;

import java.util.ArrayList;
import java.util.List;

import com.xiang.adapter.MemoListAdapter;
import com.xiang.model.LayoutAlpha;
import com.xiang.model.LayoutMove;
import com.xiang.model.LayoutPosition;
import com.xiang.model.LayoutZoom;
import com.xiang.model.Memo;
import com.xiang.model.Memo.yColor;
import com.xiang.view.MyListView;
import com.xiang.view.MyScrollView;
import com.xiang.view.PullToAddLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MemoActivity extends Activity {
	
	public static MemoActivity that = null; 
	
	MyListView listview = null;
	List<Memo> memos = null;
	
	/***********************     views         **************/
	/**
	 * “所有”  标签
	 */
	Button b_all = null;
	/**
	 * 下拉Layout
	 */
	PullToAddLayout pullToAddLayout = null;
	/**
	 * 顶部的fl布局
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
	public ImageView[] imageViews = new ImageView[PullToAddLayout.colorCount];
	/**
	 * 下拉保存提示文字
	 */
	public TextView tv_pulltosave = null;
	/**
	 * 标签外面的ll
	 */
	public LinearLayout ll_tags = null;
	/**
	 * 下面的显示内容
	 */
	public LinearLayout ll_content = null;
	/**
	 * scrollview
	 */
	public MyScrollView scrollView = null;
	/**
	 * 编辑框他爹
	 */
	public RelativeLayout rl_edit = null;
	/**
	 * 编辑框
	 */
	public EditText et_add = null;
	
	/*****/
	
	/**
	 * 当前选的颜色
	 */
	private yColor currentcolor;
	
	
	public MyHandler mHandler = null;
	
	/**
	 * 图片按钮的点击监听
	 */
	private ColorImageOnClickListener onclickListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memo);
		
		that = this;
		
		onclickListener = new ColorImageOnClickListener();
		
		init();
		
		initImageViewListener();
		
		mHandler = new MyHandler();
		
	}

	private void initImageViewListener() {
		iv_blue.setOnClickListener(onclickListener);
		iv_lightblue.setOnClickListener(onclickListener);
		iv_lightpink.setOnClickListener(onclickListener);
		iv_pink.setOnClickListener(onclickListener);
		iv_red.setOnClickListener(onclickListener);
		iv_yellow.setOnClickListener(onclickListener);
	}

	private void init() {
		
		
		listview = (MyListView) findViewById(R.id.lv_memos);
		b_all = (Button) findViewById(R.id.b_all);
		pullToAddLayout = (PullToAddLayout) findViewById(R.id.pullToAddLayout);
		fl_pullhead = (FrameLayout) findViewById(R.id.fl_pullhead);
		iv_blue = (ImageView) findViewById(R.id.iv_blue);
		iv_lightblue = (ImageView) findViewById(R.id.iv_lightblue);
		iv_lightpink = (ImageView) findViewById(R.id.iv_lightpink);
		iv_yellow = (ImageView) findViewById(R.id.iv_yellow);
		iv_red = (ImageView) findViewById(R.id.iv_red);
		iv_pink = (ImageView) findViewById(R.id.iv_pink);
		tv_pulltosave = (TextView) findViewById(R.id.tv_pulltosave);
		ll_tags = (LinearLayout) findViewById(R.id.ll_tags);
		ll_content = (LinearLayout) findViewById(R.id.ll_content);
		scrollView = (MyScrollView) findViewById(R.id.ScrollView1);
		rl_edit = (RelativeLayout) findViewById(R.id.rl_edit);
		et_add = (EditText) findViewById(R.id.et_add);
		
		InitPullToAddLayout();
		
		memos = new ArrayList<Memo>();
		addTestData();
		listview.setAdapter(new MemoListAdapter(this,listview,memos));
		
		//获取焦点，防止scrollview下滑,使用listview上面的一个控件获取焦点
		b_all.setFocusable(true);
		b_all.setFocusableInTouchMode(true);
		b_all.requestFocus();
	}

	//初始化PullToAddLayout中的控件
	private void InitPullToAddLayout() {
		pullToAddLayout.fl_pullhead = fl_pullhead;
		pullToAddLayout.iv_blue = iv_blue;
		pullToAddLayout.iv_lightblue = iv_lightblue;
		pullToAddLayout.iv_lightpink = iv_lightpink;
		pullToAddLayout.iv_pink = iv_pink;
		pullToAddLayout.iv_red = iv_red;
		pullToAddLayout.iv_yellow = iv_yellow;
		pullToAddLayout.tv_pulltosave = tv_pulltosave;
		pullToAddLayout.ll_tags = ll_tags;
		pullToAddLayout.listView = listview;
		pullToAddLayout.ll_content = ll_content;
		pullToAddLayout.ll_pullhead_items = (LinearLayout) findViewById(R.id.ll_pullhead_items);
		pullToAddLayout.scrollView = scrollView;
		pullToAddLayout.rl_edit = rl_edit;
		
		pullToAddLayout.imageViews[0] = iv_blue;
		pullToAddLayout.imageViews[1] = iv_lightblue;
		pullToAddLayout.imageViews[2] = iv_lightpink;
		pullToAddLayout.imageViews[3] = iv_pink;
		pullToAddLayout.imageViews[4] = iv_red;
		pullToAddLayout.imageViews[5] = iv_yellow;
		
//		pullToAddLayout.init();
	}

	private void addTestData() {
		memos.add(new Memo("注意：这些枚举值都是public static fina ","12:00",yColor.blue));
		memos.add(new Memo("  ordinal()方法: 返回枚举值在枚举类种的顺序。这个顺序根据枚举值inal()方法: 返回枚举值在枚举类种的顺序。这个顺序根据枚举值声明的顺序而定。 ","12:00",yColor.lightblue));
		memos.add(new Memo("注意：这些枚举值都是public static final的，也就是我们经常所定义的常量方式，因此枚举类中的枚举值最好全部大写。 ","12:00",yColor.lightpink));
		memos.add(new Memo("注意：这些枚举值都是pu ","12:00",yColor.yellow));
		memos.add(new Memo("2、即然枚举类是class，当然在枚举类型中有构造器，方法和数据域。但是，枚举类的构造器有很大的不同：  ","12:00",yColor.blue));
		memos.add(new Memo("注意：这些枚举值都是public static final的，也就是我们经常所定义的常量方式，因此枚举类中的枚举值最好全部大写。 ","12:00",yColor.pink));
	}
	
	public class MyHandler extends Handler{

		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 90:
				LayoutMove lp =  (LayoutMove) msg.obj;
				lp.layout.layout(lp.left, lp.top, lp.right, lp.bottom);
				break;
			case 91:
				LayoutZoom lz = (LayoutZoom) msg.obj;
				lz.view.setScaleX(lz.sizeX);
				lz.view.setScaleY(lz.sizeX);
				break;
			case 92:
				LayoutAlpha alpha = (LayoutAlpha) msg.obj;
				alpha.view.setAlpha(alpha.alpha);
				break;
				//清晰度为0，则隐藏
			case 94:
				View v = (View) msg.obj;
				v.setVisibility(View.GONE);
				break;
			}
			super.handleMessage(msg);
		}
		
	}
	
	public class ColorImageOnClickListener implements OnClickListener{

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.iv_blue:
				pullToAddLayout.ShowingColor2Edit(yColor.blue);
				et_add.setBackground(getResources().getDrawable(R.drawable.blue));
				break;
			case R.id.iv_lightblue:
				pullToAddLayout.ShowingColor2Edit(yColor.lightblue);
				et_add.setBackground(getResources().getDrawable(R.drawable.lightblue));
				break;
			case R.id.iv_lightpink:
				pullToAddLayout.ShowingColor2Edit(yColor.lightpink);
				et_add.setBackground(getResources().getDrawable(R.drawable.lightpink));
				break;
			case R.id.iv_pink:
				pullToAddLayout.ShowingColor2Edit(yColor.pink);
				et_add.setBackground(getResources().getDrawable(R.drawable.pink));
				break;
			case R.id.iv_red:
				pullToAddLayout.ShowingColor2Edit(yColor.red1);
				et_add.setBackground(getResources().getDrawable(R.drawable.red1));
				break;
			case R.id.iv_yellow:
				pullToAddLayout.ShowingColor2Edit(yColor.yellow);
				et_add.setBackground(getResources().getDrawable(R.drawable.yellow));
				break;
			}
		}
		
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.memo, menu);
		return true;
	}

}
