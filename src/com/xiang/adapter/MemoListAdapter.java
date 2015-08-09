package com.xiang.adapter;

import java.util.List;

import com.xiang.model.Memo;
import com.xiang.model.ViewHolder;
import com.xiang.papermemo.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MemoListAdapter extends BaseAdapter {
	
	public Context context;
	public ListView listview;
	public List<Memo> memos;
	
	

	public MemoListAdapter(Context context, ListView listview, List<Memo> memos) {
		super();
		this.context = context;
		this.listview = listview;
		this.memos = memos;
	}

	@Override
	public int getCount() {
		return memos.size();
	}

	@Override
	public Object getItem(int position) {
		return memos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(null == convertView || ((ViewHolder)convertView.getTag()).flag != position)
		{
			Memo memo = memos.get(position);
			holder = new ViewHolder(position);
			
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_memo, null);
			RelativeLayout rl_list = (RelativeLayout) convertView.findViewById(R.id.rl_list);
			TextView tv_memo_content = (TextView) convertView.findViewById(R.id.tv_memo_content);
			TextView tv_memo_time = (TextView) convertView.findViewById(R.id.tv_memo_time);
			
			tv_memo_content.setText(memo.content);
			tv_memo_time.setText(memo.time);
			
			switch(memo.color){
			case blue:rl_list.setBackground(context.getResources().getDrawable(R.drawable.blue));break;
			case lightblue:rl_list.setBackground(context.getResources().getDrawable(R.drawable.lightblue));break;
			case lightpink:rl_list.setBackground(context.getResources().getDrawable(R.drawable.lightpink));break;
			case pink:rl_list.setBackground(context.getResources().getDrawable(R.drawable.pink));break;
			case red1:rl_list.setBackground(context.getResources().getDrawable(R.drawable.red1));break;
			case yellow:rl_list.setBackground(context.getResources().getDrawable(R.drawable.yellow));break;
			default: break;
			}
			
//			switch(memo.color){
//			case blue:rl_list.setBackgroundColor(context.getResources().getColor(R.color.y_blue));break;
//			case lightblue:rl_list.setBackgroundColor(context.getResources().getColor(R.color.y_lightblue));break;
//			case lightpink:rl_list.setBackgroundColor(context.getResources().getColor(R.color.y_lightpink));break;
//			case pink:rl_list.setBackgroundColor(context.getResources().getColor(R.color.y_pink));break;
//			case red1:rl_list.setBackgroundColor(context.getResources().getColor(R.color.y_red));break;
//			case yellow:rl_list.setBackgroundColor(context.getResources().getColor(R.color.y_yellow));break;
//			default: break;
//			}
			
			convertView.setTag(holder);
		}
		
		return convertView;
	}

}
