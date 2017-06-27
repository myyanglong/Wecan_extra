package com.wecan.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wecanws.param.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class WaterAdapter extends BaseAdapter {

	private Context context = null;
	private List<WaterMeter> datas = null;

	public WaterAdapter(Context context, List<WaterMeter> datas) {
		this.datas = datas;
		this.context = context;
	}

	/**
	 * 首先,默认情况下,所有项目都是没有选中的.这里进行初始化
	 */
	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		TextView tx_1= null;
		TextView tx_2= null;
		ImageView img_id = null;

		/**
		 * 进行ListView 的优化
		 */
		if (convertView == null) {
			convertView = (ViewGroup) LayoutInflater.from(context).inflate(
					R.layout.small_tab_area_list, parent, false);

			tx_1 = (TextView) convertView.findViewById(R.id.tx_1);
			tx_2 = (TextView) convertView.findViewById(R.id.tx_2);
			img_id = (ImageView) convertView.findViewById(R.id.img_id);

			convertView.setTag(new ViewHolder(tx_1,tx_2,img_id));
		} else {
			ViewHolder dataWrapper = (ViewHolder) convertView.getTag();
			tx_1 = dataWrapper.tx_1;
			tx_2 = dataWrapper.tx_2;
			img_id = dataWrapper.img_id;

		}
		WaterMeter watermeter = datas.get(position);
		/*
		 * 获得该item 是否允许删除
		 */
		tx_1.setText(watermeter.unit +" " + watermeter.address + " " + watermeter.floor + "-"+watermeter.door);
		tx_2.setText(String.format(context.getString(R.string.meter_list_id),watermeter.id));

		//if(position == 1)
		//img_id.setImageResource(R.drawable.del_icon_normal);

		return convertView;
	}


	public void adapter_sort(){
		Collections.sort(datas,new reorder());
	}
	public static final class reorder implements Comparator<WaterMeter>{

		@Override
		public int compare(WaterMeter arg0, WaterMeter arg1) {
			// TODO Auto-generated method stub
			if(Integer.parseInt(arg0.id) > Integer.parseInt(arg1.id))
				return 1;
			else
				return -1;
		}

	}
	public final class ViewHolder{
		public TextView tx_1= null;
		public TextView tx_2= null;
		public ImageView img_id = null;

		public ViewHolder(TextView tx_1, TextView tx_2 ,ImageView img_id) {
			this.tx_1 = tx_1;
			this.tx_2 = tx_2;
			this.img_id = img_id;
		}
	}
	public List<WaterMeter> getDatas() {
		return datas;
	}

}

