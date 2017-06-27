package com.ftdi.j2xx.hyperterm;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.wecan.service.PreferencesService;
import com.wecan.service.WaterAdapter;
import com.wecan.service.WaterMeter;
import com.wecan.service.WaterMeterService;
import com.wecanws.param.R;

import java.util.ArrayList;
import java.util.List;

public class SmallTabArea extends Activity {
	TextView tx;
	WaterMeterService wms;
	WaterMeter wm;
	private WaterAdapter meterAdapter = null;


	private ListView meter;
	List<WaterMeter> meterList = new ArrayList<WaterMeter>();
	private WaterMeterService service;

	private Spinner  spinner_area,spinner_build,spinner_floor;
	private ArrayAdapter<String> adapterarea,adapterbuild,adapterfloor;

	private int type;
	private String str_area=null, str_build=null, str_floor=null;
	private PreferencesService prservice =null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.small_tab_area);

		findView();
		initData();
		initView();
	}

	private void findView() {
		// TODO Auto-generated method stub
		meter = (ListView) this.findViewById(R.id.small_water_list);
		spinner_area = (Spinner) findViewById(R.id.spinner_area);
		spinner_build = (Spinner) findViewById(R.id.spinner_build);
		spinner_floor = (Spinner) findViewById(R.id.spinner_floor);
	}

	private void initView() {
		// TODO Auto-generated method stub

		int i = 1,j;
		for(;i<15;i++){
			meterList.add(new WaterMeter(String.format("WA%02d", i),"中智联","" + i,""+i));

		}
		//meterAdapter = new WaterMeterAdapter(this, meterList,true);

		//meter.setAdapter(meterAdapter);
		meterAdapter = new WaterAdapter(this, meterList);
		meter.setAdapter(meterAdapter);


		spinner_area.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0){
					spinner_build.setClickable(false);

					type = 0;
				}
				else{
					spinner_build.setClickable(true);
					//Log.i("debug", (String)parent.getAdapter().getItem(position));
					str_area = (String)parent.getAdapter().getItem(position);
					updatebuild(str_area);
					adapterbuild.notifyDataSetChanged();
					type = 1;
				}
				spinner_floor.setClickable(false);
				spinner_build.setSelection(0);
				spinner_floor.setSelection(0);
				updateListView();
			}
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		spinner_build.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("onItemSelected:" + position);
				if(!(type == 0)){
					if(position == 0){
						spinner_floor.setClickable(false);
						type = 1;
					}
					else{
						spinner_floor.setClickable(true);
						str_build = (String)parent.getAdapter().getItem(position);
						//Log.i("debug", str_build);
						updatefloor(str_build);
						adapterfloor.notifyDataSetChanged();
						type = 2;

					}
					spinner_floor.setSelection(0);
					updateListView();
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		spinner_floor.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				if(!(type == 0)){
					if(position == 0){
						type = 2;
					}
					else{
						type = 3;
						str_floor = (String)parent.getAdapter().getItem(position);
					}
					//spinner_floor.setSelection(0);
					updateListView();
				}
			}
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		//spinner_build.setOnItemSelectedListener(this);

	}
	private void updatefloor(String str) {
		// TODO Auto-generated method stub
		adapterfloor = new ArrayAdapter<String>(this, R.layout.spinner_item, service.getListFloor(str_area, str));
		adapterfloor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);//simple_spinner_dropdown_item
		spinner_floor.setAdapter(adapterfloor);
	}
	public void updatebuild(String str){
		adapterbuild = new ArrayAdapter<String>(this, R.layout.spinner_item, service.getListBuild(str));
		adapterbuild.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);//simple_spinner_dropdown_item
		spinner_build.setAdapter(adapterbuild);
	}
	public void updateListView(){
		Log.i("debug", "updateListView "+ type);
		prservice.save_SmallList(type,str_area,str_build,str_floor);
		meter.setAdapter(new WaterAdapter(this, service.getSmallListWater(type,str_area,str_build,str_floor,true)));
	}
	private void initData() {
		// TODO Auto-generated method stub
		service = new WaterMeterService(this);
		prservice = new PreferencesService(this);

		adapterarea = new ArrayAdapter<String>(this, R.layout.spinner_item, service.getListArea());
		adapterarea.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);//simple_spinner_dropdown_item
		spinner_area.setAdapter(adapterarea);

		adapterbuild = new ArrayAdapter<String>(this, R.layout.spinner_item, service.getListBuild(null));
		adapterbuild.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);//simple_spinner_dropdown_item
		spinner_build.setAdapter(adapterbuild);

		adapterfloor = new ArrayAdapter<String>(this, R.layout.spinner_item, service.getListFloor(null,null));
		adapterfloor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);//simple_spinner_dropdown_item
		spinner_floor.setAdapter(adapterfloor);

	}

	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();
		adapterarea = new ArrayAdapter<String>(this, R.layout.spinner_item, service.getListArea());
		adapterarea.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spinner_area.setAdapter(adapterarea);

		adapterbuild = new ArrayAdapter<String>(this, R.layout.spinner_item, service.getListBuild(null));
		adapterbuild.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spinner_build.setAdapter(adapterbuild);

		adapterfloor = new ArrayAdapter<String>(this, R.layout.spinner_item, service.getListFloor(null,null));
		adapterfloor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spinner_floor.setAdapter(adapterfloor);

	}

}

