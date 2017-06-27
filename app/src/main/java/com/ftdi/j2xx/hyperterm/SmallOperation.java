package com.ftdi.j2xx.hyperterm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wecan.service.WaterMeterService;
import com.wecanws.param.R;

import java.util.Timer;
import java.util.TimerTask;

public class SmallOperation extends Activity implements OnClickListener{
	private RelativeLayout s_back;
	private Timer timer;
	private TimerTask task;
	private int readflag = 0;

	private LinearLayout operation_net, operation_collect;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.small_operation);

		findView();
		initdata();
	}
	private void findView() {
		// TODO Auto-generated method stub
		operation_net = ((LinearLayout) findViewById(R.id.small_operation_net));
		operation_collect = ((LinearLayout) findViewById(R.id.small_operation_collect));
		s_back = (RelativeLayout) findViewById(R.id.s_back);
	}
	private void initdata() {
		// TODO Auto-generated method stub
		operation_net.setOnClickListener(this);
		operation_collect.setOnClickListener(this);
		s_back.setOnClickListener(this);
	}

	public void Key_Lock() {
		this.readflag = 0;
		operation_net.setClickable(false);
		operation_collect.setClickable(false);
		s_back.setClickable(false);
	}
	public void unKey_Lock() {
		operation_net.setClickable(true);
		operation_collect.setClickable(true);
		s_back.setClickable(true);
	}

	private ProgressDialog pialog;

	public void UpdateProgressMsg(String str){
		if(pialog.isShowing())
			pialog.setMessage(str);
	}
	public void ShowProgressDialogEnd(String str){
		pialog.dismiss();
		new AlertDialog.Builder(this)
				.setIcon(null)
				.setCancelable(false)
				.setTitle("提示")
				.setMessage(str)
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								dialog.dismiss();
							}
						}).create().show();
	}
	public void ShowProgressDialog(String str){
		pialog = new ProgressDialog(this);
		pialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pialog.setMessage(str);
		pialog.setCancelable(true);
		pialog.setCanceledOnTouchOutside(false);
		pialog.show();
	}

	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			unKey_Lock();
			String str = (String)msg.obj;
			switch(msg.what){
				case 9:
					UpdateProgressMsg(str);
					break;
				case 10:
					ShowProgressDialogEnd(str);
					break;
				default:
					ShowProgressDialog(str);
					break;
			}
		}
	};

	public void timing() {
		this.timer = new Timer(true);
		task = new TimerTask() {
			public void run() {
				if(readflag == 0){
					Message msg = new Message();
					msg.what = 10;
					msg.obj = "操作超时";
					handler.sendMessage(msg);
					timer.cancel();
				}else {
					timer.cancel();
				}
			}
		};
	}

	//operation: 1 组网    2 采集
	public void DoTask(int operation){
		Intent intent=new Intent();
		intent.putExtra("operation", operation);
		intent.setAction("com.weian.activity.BROAD_CAST");

		//timing();
		switch(operation){
			case 1:
				ShowProgressDialog("设备组网中... ...");
				sendBroadcast(intent);
				//this.timer.schedule(task, 5000, 5000);
				break;
			case 2:
				ShowProgressDialog("数据采集中... ...");
				sendBroadcast(intent);
				//this.timer.schedule(task, 5000, 5000);
				break;
			default:
				break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		WaterMeterService service = new WaterMeterService(this);
		switch(v.getId()){
			case R.id.s_back:
				this.finish();
				break;
			case R.id.small_operation_net:
				DoTask(1);
				Key_Lock();
				break;
			case R.id.small_operation_collect:
				DoTask(2);
				Key_Lock();
				break;
			default:

				break;
		}
	}
}
