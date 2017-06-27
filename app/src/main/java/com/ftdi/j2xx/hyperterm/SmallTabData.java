package com.ftdi.j2xx.hyperterm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.Bean.SmallDataBean;
import com.Utils.CrcUtils;
import com.Utils.SwauthedUtils;
import com.wecan.service.PreferencesService;
import com.wecan.service.WaterMeter;
import com.wecan.service.WaterMeterAdapter;
import com.wecan.service.WaterMeterService;
import com.wecanws.param.R;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static com.Utils.CrcUtils.printHexBinary;


public class SmallTabData extends Activity implements OnClickListener, OnItemLongClickListener {
	private Button onButton, offButton;
	private Button netButton, collectButton, clearButton;
	private Button btnSet;
	private Intent tableintent;
	private Bundle tabbundle;
	private Bundle databundle=new Bundle();
	private String total = "0.0";
	private String ratio = "1.0";
	private String cx1 = "1.0";
	private String cx2 = "1.0";
	private String cx3 = "1.0";
	private String cx4 = "1.0";
	private String cx5 = "1.0";
	private String cx6 = "1.0";

	private EditText etTotal;
	private EditText etRatio;
	private EditText etCX1;
	private EditText etCX2;
	private EditText etCX3;
	private EditText etCX4;
	private EditText etCX5;
	private EditText etCX6;

	private ListView listView;

	public String rev_str;
	private PreferencesService service;

	private WaterMeter wm;
	private WaterMeterAdapter userAdapter = null, missedAdapter = null;

	List<WaterMeter> userList = new ArrayList<WaterMeter>();
	List<WaterMeter> missedList = new ArrayList<WaterMeter>();
	List<WaterMeter> tempList = new ArrayList<WaterMeter>();


	private long updatetime = 0;
	private boolean bflag = true;

	public UartReceiver receiver = new UartReceiver();
	public IntentFilter filter = new IntentFilter();

	private WaterMeterService dbserv;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.small_tab_data);

		findView();
		initdata();
		initview();
	}

	private void findView() {
		// TODO Auto-generated method stub


        SQLiteDatabase.loadLibs(SmallTabData.this,getFilesDir());
		onButton = (Button) findViewById(R.id.meter_data);
		offButton = (Button) findViewById(R.id.missed_data);
		listView = (ListView) this.findViewById(R.id.datalistView);
		netButton = (Button) findViewById(R.id.tab_data_net);
		collectButton = (Button) findViewById(R.id.tab_data_collect);
		clearButton = (Button) findViewById(R.id.tab_data_clear);
		onButton.setOnClickListener(this);
		offButton.setOnClickListener(this);
		netButton.setOnClickListener(this);
		collectButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);
	}

	private void initview() {
		// TODO Auto-generated method stub
		onButton.setBackgroundResource(R.drawable.small_topbar_bt1);
		onButton.setText(String.format(getResources().getString(R.string.data_meter), userAdapter.getCount()));

		offButton.setBackgroundResource(R.drawable.small_topbar_bg);
		offButton.setText(String.format(getResources().getString(R.string.data_missed), missedAdapter.getCount()));

		listView.setOnItemLongClickListener(this);
	}

	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();

		//Log.i("IOT", "SmallTabData:  onPostResume");

		Bundle bundle = service.get_SmallListTag();
		int index = bundle.getInt("type");
		String area = bundle.getString("str_area");
		String build = bundle.getString("str_build");
		String floor = bundle.getString("str_floor");
		userList = (new WaterMeterService(this)).getSmallListWater(index + 10, area, build, floor, true);
		userAdapter = new WaterMeterAdapter(this, userList, false);
		onButton.setText(String.format(getResources().getString(R.string.data_meter), userList.size()));

		missedList = (new WaterMeterService(this)).getSmallListWater(bundle.getInt("type") + 10, bundle.getString("str_area"), bundle.getString("str_build"), bundle.getString("str_floor"), false);
		missedAdapter = new WaterMeterAdapter(this, missedList, false);
		offButton.setText(String.format(getResources().getString(R.string.data_missed), missedList.size()));

		if (bflag)
			listView.setAdapter(userAdapter);
		else
			listView.setAdapter(missedAdapter);
		//offButton.setText(String.format(getResources().getString(R.string.data_missed),missedList.size()));
		//Log.i("debug", "onPostResume" + missedList.size());
		registerReceiver(receiver, filter);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if ((System.currentTimeMillis() / 1000) - updatetime > 2) {
				//List<WaterMeter> updateList = (List<WaterMeter>) msg.obj;
				for (int i = 0; i < msg.what; i++) {
					userAdapter.CheckMeter(tempList.get(0), missedAdapter.CheckMissedMeter(tempList.get(0)));
					missedAdapter.removeMeter(tempList.get(0));
					tempList.remove(0);
				}
				listView.setVisibility(View.GONE);
				userAdapter.notifyDataSetChanged();
				listView.setVisibility(View.VISIBLE);
				onButton.setText(String.format(getResources().getString(R.string.data_meter), userAdapter.getCount()));
				offButton.setText(String.format(getResources().getString(R.string.data_missed), missedAdapter.getCount()));
				updatetime = System.currentTimeMillis() / 1000;
			}
		}
	};

	protected void onStart() {
		registerReceiver(receiver, filter);
		super.onStart();
		//Log.i("IOT", "SmallTabData:  onPostResume");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (receiver != null)
			unregisterReceiver(receiver);
		//Log.i("IOT", "SmallTabData:  onPause");
	}

	public class UartReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle.getInt("cmd") == 2) {
				Log.i("IOT", bundle.getString("id"));
				WaterMeter watermeter = new WaterMeter(bundle.getString("id"), bundle.getString("time"), bundle.getString("total"), bundle.getInt("status"), bundle.getInt("rf"));
				tempList.add(watermeter);
				//handler.sendMessage(handler.obtainMessage(tempList.size(), watermeter));
				UpdateProgressMsg("数据采集，水表号：" + bundle.getString("id"));
				//userAdapter.adapter_sort();
			} else if (bundle.getInt("cmd") == 1) {
				(new WaterMeterService(SmallTabData.this)).updateNet(bundle.getString("id"));
				UpdateProgressMsg("设备组网，水表号：" + bundle.getString("id"));
				//Log.i("IOT", "UartReceiver:  " + bundle.getString("id"));
			} else if (bundle.getInt("cmd") == 3) {
				handler.sendMessage(handler.obtainMessage(tempList.size(), null));
				ShowProgressDialogEnd("操作完成");
			} else if (bundle.getInt("cmd") == 4) {
				ShowProgressDialogEnd("数据清除完成");
//		    	listView.setVisibility(View.GONE);
//				userAdapter.notifyDataSetChanged();
//				listView.setVisibility(View.VISIBLE);
//				onButton.setText(String.format(getResources().getString(R.string.data_meter),userAdapter.getCount()));
//				offButton.setText(String.format(getResources().getString(R.string.data_missed),missedAdapter.getCount()));
			} else if (bundle.getInt("cmd") == 9) {
				ShowProgressDialogEnd("操作超时！");
			} else if (bundle.getInt("cmd") == 10) {
				dbserv.addUsed();
				String id = bundle.getString("id");
				String dev = bundle.getString("dev");
				int major = bundle.getInt("major");
				int minor = bundle.getInt("minor");
				String hint = "操作完成！\n"
						+ "水表地址：" + id + "\n"
						+ "组网设备：" + dev + "\n"
						+ String.format("主版本号：%d\n", major)
						+ String.format("次版本号：%d\n", minor);
				ShowProgressDialogEnd(hint);
			} else if (bundle.getInt("cmd") == 11) {
				int sta = bundle.getInt("status");
				if ((sta & 0x01) == 0x01) {
					String total = bundle.getString("total");
					String param = bundle.getString("param");
					String scx1 = bundle.getString("cx1");
					String scx2 = bundle.getString("cx2");
					String scx3 = bundle.getString("cx3");
					String scx4 = bundle.getString("cx4");
					String scx5 = bundle.getString("cx5");
					String scx6 = bundle.getString("cx6");

					//保存旧值
					databundle.putDouble("lowtotal",Double.valueOf(total).doubleValue());
					databundle.putFloat("lowratio",Float.parseFloat(param));
					databundle.putFloat("lowcx1",Float.parseFloat(scx1));
					databundle.putFloat("lowcx2",Float.parseFloat(scx2));
					databundle.putFloat("lowcx3",Float.parseFloat(scx3));
					databundle.putFloat("lowcx4",Float.parseFloat(scx4));
					databundle.putFloat("lowcx5",Float.parseFloat(scx5));
					databundle.putFloat("lowcx6",Float.parseFloat(scx6));



					etTotal.setText(total);
					etRatio.setText(param);
					etCX1.setText(scx1);
					etCX2.setText(scx2);
					etCX3.setText(scx3);
					etCX4.setText(scx4);
					etCX5.setText(scx5);
					etCX6.setText(scx6);

					ShowProgressDialogEnd("操作成功！");

					if (btnSet != null)
						btnSet.setEnabled(true);
				} else {
					ShowProgressDialogEnd("操作失败！");
				}
			}

		}
	}

	private void initdata() {

		service = new PreferencesService(this);
		rev_str = "空白";
		Bundle bundle = service.get_SmallListTag();
		userList = (new WaterMeterService(this)).getSmallListWater(bundle.getInt("type") + 10, bundle.getString("str_area"), bundle.getString("str_build"), bundle.getString("str_floor"), true);
		userAdapter = new WaterMeterAdapter(this, userList, false);

		missedList = (new WaterMeterService(this)).getSmallListWater(bundle.getInt("type") + 10, bundle.getString("str_area"), bundle.getString("str_build"), bundle.getString("str_floor"), false);
		missedAdapter = new WaterMeterAdapter(this, missedList, false);

		listView.setAdapter(userAdapter);

		filter.addAction("com.weian.service.BROAD_CAST");

		dbserv = new WaterMeterService(this);

		Log.i("debug", "Tab");
		//sendBroadcast(new IntentInfo(1,"040202030204020502"));
	}

	public void SendBroadCastToUart(int operation) {
		Intent intent = new Intent();
		intent.putExtra("operation", operation);
		intent.setAction("com.weian.TAB_DATA.BROAD_CAST");
		sendBroadcast(intent);
	}

	private ProgressDialog pialog;

	public void UpdateProgressMsg(String str) {
		if (pialog.isShowing())
			pialog.setMessage(str);
	}

	public void ShowProgressDialogEnd(String str) {
		pialog.dismiss();
		new AlertDialog.Builder(this)
				.setIcon(null)
				.setCancelable(false)
				.setTitle("提示")
				.setMessage(str)
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).create().show();
	}

	public void ShowProgressDialog(String str) {
		pialog = new ProgressDialog(this);
		pialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pialog.setMessage(str);
		pialog.setCancelable(true);
		pialog.setCanceledOnTouchOutside(false);
		pialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tab_data_net:
				ShowProgressDialog("设备组网中......");
				SendBroadCastToUart(1);
				break;
			case R.id.tab_data_collect:
				ShowProgressDialog("数据采集中......");
				SendBroadCastToUart(2);
				break;
			case R.id.tab_data_clear:
				SendBroadCastToUart(3);
				break;
			case R.id.meter_data:
				bflag = true;
				listView.setAdapter(userAdapter);
				onButton.setBackgroundResource(R.drawable.small_topbar_bt1);
				offButton.setBackgroundResource(R.drawable.small_topbar_bg);
				break;
			case R.id.missed_data:
				bflag = false;
				/*
				Toast.makeText(this,
						(new WaterMeterService(this)).select_data("200")
		    			, Toast.LENGTH_LONG).show();*/
				listView.setAdapter(missedAdapter);
				onButton.setBackgroundResource(R.drawable.small_topbar_bt);
				offButton.setBackgroundResource(R.drawable.small_topbar_bg1);
				break;
			default:
				break;
		}
	}

	public void setEditTextAccuracy(final EditText editText, final int limit) {
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				/**
				 * 限制输入最多为 limit 位小数
				 */
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > limit) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + limit + 1);
						editText.setText(s);
						editText.setSelection(s.length());
					}
				}
				/**
				 * 第一位输入小数点的话自动变换为 0.
				 */
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					editText.setText(s);
					editText.setSelection(2);
				}

				/**
				 * 避免重复输入小数点前的0 ,没有意义
				 */
				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						editText.setText(s.subSequence(0, 1));
						editText.setSelection(1);
						return;
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View v, int position,
								   long id) {
		// TODO Auto-generated method stub
		final int accuracyTotal = 3;
		final int accuracy = 6;
		WaterMeter wm = (WaterMeter) adapter.getAdapter().getItem(position);
		final String meterid = wm.id;
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.dialog, null);
		final TextView tvAddr = (TextView) textEntryView.findViewById(R.id.tvAddr);
		final TextView tvId = (TextView) textEntryView.findViewById(R.id.tvId);

		etTotal = (EditText) textEntryView.findViewById(R.id.etTotal);
		etRatio = (EditText) textEntryView.findViewById(R.id.etRatio);
		etCX1 = (EditText) textEntryView.findViewById(R.id.etCX1);
		etCX2 = (EditText) textEntryView.findViewById(R.id.etCX2);
		etCX3 = (EditText) textEntryView.findViewById(R.id.etCX3);
		etCX4 = (EditText) textEntryView.findViewById(R.id.etCX4);
		etCX5 = (EditText) textEntryView.findViewById(R.id.etCX5);
		etCX6 = (EditText) textEntryView.findViewById(R.id.etCX6);


		//final Button btnParam = (Button) textEntryView.findViewById(R.id.btn_param);

		tvAddr.setText(wm.unit + " " + wm.address + " " + wm.floor + "-" + wm.door);
		tvId.setText(meterid);

		etTotal.setText("");
		etRatio.setText("");
		etCX1.setText("");
		etCX2.setText("");
		etCX3.setText("");
		etCX4.setText("");
		etCX5.setText("");
		etCX6.setText("");

		etTotal.setKeyListener(new DigitsKeyListener(false, true));
		etRatio.setKeyListener(new DigitsKeyListener(false, true));
		etCX1.setKeyListener(new DigitsKeyListener(false, true));
		etCX2.setKeyListener(new DigitsKeyListener(false, true));
		etCX3.setKeyListener(new DigitsKeyListener(false, true));
		etCX4.setKeyListener(new DigitsKeyListener(false, true));
		etCX5.setKeyListener(new DigitsKeyListener(false, true));
		etCX6.setKeyListener(new DigitsKeyListener(false, true));
		setEditTextAccuracy(etTotal, accuracyTotal);
		setEditTextAccuracy(etRatio, accuracy);
		setEditTextAccuracy(etCX1, accuracy);
		setEditTextAccuracy(etCX2, accuracy);
		setEditTextAccuracy(etCX3, accuracy);
		setEditTextAccuracy(etCX4, accuracy);
		setEditTextAccuracy(etCX5, accuracy);
		setEditTextAccuracy(etCX6, accuracy);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("请输入配置信息").setIcon(android.R.drawable.ic_dialog_info).setView(textEntryView)
				.setNegativeButton("取消", null);
		builder.setNeutralButton("读取", null);
		builder.setPositiveButton("设置", null);
		builder.setNeutralButton("读取", null);

		final AlertDialog alertDialog = builder.create();
		alertDialog.show();

		alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
		alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String str1 = etTotal.getText().toString();
				String str2 = etRatio.getText().toString();
				String str3 = etCX1.getText().toString();
				String str4 = etCX2.getText().toString();
				String str5 = etCX3.getText().toString();
				String str6 = etCX4.getText().toString();
				String str7 = etCX5.getText().toString();
				String str8 = etCX6.getText().toString();
				if (str1 == null || str1.length() <= 0 || str2 == null || str2.length() <= 0 ||
						str3 == null || str3.length() <= 0 || str4 == null || str4.length() <= 0 ||
						str5 == null || str5.length() <= 0 || str6 == null || str6.length() <= 0 ||
						str7 == null || str7.length() <= 0 || str8 == null || str8.length() <= 0) {
					ShowProgressDialogEnd("请输入有效值！");
				} else {

					//传输服务器值
//					水表ID	累计值	新累计值	参数1--5
//					4字节无符号整数	8字节双精度浮点数	8字节双精度浮点数	4B单精度浮点数
					databundle.putInt("id",Integer.parseInt(meterid));
					databundle.putDouble("total",Float.parseFloat(str1));
					databundle.putFloat("ratio",Float.parseFloat(str2));
					databundle.putFloat("cx1",Float.parseFloat(str3));
					databundle.putFloat("cx2",Float.parseFloat(str4));
					databundle.putFloat("cx3",Float.parseFloat(str5));
					databundle.putFloat("cx4",Float.parseFloat(str6));
					databundle.putFloat("cx5",Float.parseFloat(str7));
					databundle.putFloat("cx6",Float.parseFloat(str8));



					tableintent = new Intent();
					tabbundle = new Bundle();
					tabbundle.putInt("operation", 10);
					tabbundle.putString("id", meterid);
					tabbundle.putString("total", str1);
					tabbundle.putString("ratio", str2);
					tabbundle.putString("cx1", str3);
					tabbundle.putString("cx2", str4);
					tabbundle.putString("cx3", str5);
					tabbundle.putString("cx4", str6);
					tabbundle.putString("cx5", str7);
					tabbundle.putString("cx6", str8);

					total = str1;
					ratio = str2;
					cx1 = str3;
					cx2 = str4;
					cx3 = str5;
					cx4 = str6;
					cx5 = str7;
					cx6 = str8;

					//成功后发广播到Mainactivity
					tableintent.putExtras(tabbundle);
					tableintent.setAction("com.weian.TAB_DATA.BROAD_CAST");
					sendBroadcast(tableintent);
					ShowProgressDialog("设置进行中......");
					alertDialog.dismiss();
					etTotal.setText("");
					etRatio.setText("");
					etCX1.setText("");
					etCX2.setText("");
					etCX3.setText("");
					etCX4.setText("");
					etCX5.setText("");
					etCX6.setText("");

					// 开始上传服务器数据
//					handler = new Handler() {
//						@Override
//						public void handleMessage(Message msg) {
//							// TODO Auto-generated method stub
//							//更新提示
//							Bundle b = msg.getData();
//							ToastUtils.showLongToast(SmallTabData.this, b.getString("prompt"));
//							int success=msg.what;
//							if (success==1)
//							{
//
//
//							}
//							else{
//								alertDialog.dismiss();
//							}
//
//
//
//						}
//					};
					//new Thread(new MyThread(handler,databundle)).start();
				}
			}
		});


		alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("operation", 11);
				bundle.putString("id", meterid);

				intent.putExtras(bundle);
				intent.setAction("com.weian.TAB_DATA.BROAD_CAST");
				sendBroadcast(intent);
				ShowProgressDialog("获取参数中...");
			}
		});

		btnSet = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

//        final Button btnSet = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
//        int remain = dbserv.getRemain();
//        if (remain == 0) {
//        	btnSet.setEnabled(false);
//        } else {
//        	btnSet.setText("设置("+Integer.toString(remain)+")");
//        }

		return true;
	}

	class MyThread extends Thread {
		private Handler handler;
		private Bundle bundle;

		public MyThread(Handler handler,Bundle bundle) {
			this.handler = handler;
			this.bundle=bundle;
		}

		public void run() {
			try {
				try {

					Socket clientSocket = new Socket("183.230.182.141", 11600);
					clientSocket.setSoTimeout(5000);
//                SocketAddress address = new InetSocketAddress();
//                clientSocket.connect(address, 5000);
					// SocketChannel socltcjannel=SocketChannel.open(new InetSocketAddress("183.230.182.141",11500) {
					//  })
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					buffer.order(ByteOrder.LITTLE_ENDIAN);
					//HD
					int n = 1;
					int sitem = 76;
					buffer.put((byte) 0x57);//0X57 帧标志
					buffer.put((byte) 0);//表类型
					buffer.putInt(1 + 2 + n * sitem);//包长度 //1+2+（24*n）
					buffer.putInt(0);                    //设备·
					buffer.put((byte) 0);                 //命令码
					byte x[] = {0, 0, 0, 0, 0};
					buffer.put(x);                 //命令选项
					// data
					//1+2+（24*n）
					buffer.put((byte) 0x01);       // tag
					buffer.putShort((short) (n * 76));  // length short 2个字节
					for (int i = 0; i < n; i++) {   // values

						buffer.putInt(databundle.getInt("id"));  // wid  水表ID
						//设置参数 low表示旧参数

						buffer.putDouble(databundle.getDouble("lowtotal")); // 旧累计值
						buffer.putDouble(databundle.getDouble("total"));//累计值

						buffer.putFloat(databundle.getFloat("lowratio"));//旧准系数
						buffer.putFloat(databundle.getFloat("ratio"));//准系数


						buffer.putFloat(databundle.getFloat("lowcx1")); //CX1
						buffer.putFloat(databundle.getFloat("lowcx2"));//CX2
						buffer.putFloat(databundle.getFloat("lowcx3"));//CX3
						buffer.putFloat(databundle.getFloat("lowcx4"));//CX4
						buffer.putFloat(databundle.getFloat("lowcx5"));//CX5
						buffer.putFloat(databundle.getFloat("lowcx6"));//CX6


						buffer.putFloat(databundle.getFloat("cx1")); //CX1
						buffer.putFloat(databundle.getFloat("cx2"));//CX2
						buffer.putFloat(databundle.getFloat("cx3"));//CX3
						buffer.putFloat(databundle.getFloat("cx4"));//CX4
						buffer.putFloat(databundle.getFloat("cx5"));//CX5
						buffer.putFloat(databundle.getFloat("cx6"));//CX6

					}
					buffer.putShort((short)0);
					buffer.flip();
					byte[] bytes;
					bytes = new byte[buffer.limit()];
					buffer.get(bytes);
					CrcUtils crc16 = new CrcUtils();
				//	printHexBinary(bytes);
					byte[] crec16 = crc16.fill(bytes);
					OutputStream osSend = clientSocket.getOutputStream();
					osSend.write(crec16);
					osSend.flush();
					//clientSocket.close();
					//osSend.close();
					//发送完成接收数据
					try {
						// serverSocket.isConnected 代表是否连接成功过
						// 判断 Socket 是否处于连接状态

						if (true == clientSocket.isConnected() && false == clientSocket.isClosed()) {
							// 客户端接收服务器端的响应，读取服务器端向客户端的输入流\
//                        try
//                        {
//                            Thread.currentThread().sleep(1000);//毫秒
//                        }
//                        catch(Exception e){}
							Message message = new Message();
							Bundle b = new Bundle();
							InputStream isRead = clientSocket.getInputStream();


							byte[] one = new byte[1];
							isRead.read(one, 0, 1);
							int rlen = isRead.available();
							if (rlen > 0) {
								byte[] rb = new byte[rlen];
								isRead.read(rb);
								byte[] data = new byte[rlen + 1];
								System.arraycopy(one, 0, data, 0, one.length);
								System.arraycopy(rb, 0, data, one.length, rb.length);
								Log.i("", "" + data);
								List<SmallDataBean> smallDataBeen = new ArrayList<>();
								smallDataBeen = SwauthedUtils.decode(data);
								if (smallDataBeen.size() > 0) {
									Log.i("byte", "" + printHexBinary(rb));

									if (smallDataBeen.get(0).getResult() != 0) {
										b.putString("prompt", "授权成功");
										message.setData(b);
										message.what=1;
										handler.sendMessage(message);
									} else {

										b.putString("prompt", "授权失败,后台拒绝.请联系管理员");
										message.setData(b);
										handler.sendMessage(message);
									}
								} else {

									b.putString("prompt", "授权失败,暂未获取到数据");
									message.setData(b);
									handler.sendMessage(message);
								}
							}
						}
						// 关闭网络
						// clientSocket.close();
					} catch (Exception e) {
						Log.e("获取数据失败", "请检查网络");
						e.printStackTrace();
					}
					Log.e("发送成功", "1111");
//                Message message = new Message();
//                Bundle b = new Bundle();
//                b.putString("prompt", "发送数据成功");
//                message.setData(b);
					//  handler.sendMessage(message);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("发送失败", "");
					Message message = new Message();
					Bundle b = new Bundle();
					b.putString("prompt", "发送失败,请检查网络情况");
					message.setData(b);
					handler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("发送失败", "");
				Message message = new Message();
				Bundle b = new Bundle();
				b.putString("prompt", "发送失败,请检查网络情况");
				message.setData(b);
				handler.sendMessage(message);
			}
		}
	}
}
