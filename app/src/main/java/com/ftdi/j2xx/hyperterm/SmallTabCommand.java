package com.ftdi.j2xx.hyperterm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.Utils.EmptyUtils;
import com.wecan.service.SimpleCrypto;
import com.wecan.service.WaterMeter;
import com.wecan.service.WaterMeterService;
import com.wecanws.param.R;

import static com.wecanws.param.R.id.ly_command_small;


public class SmallTabCommand extends Activity implements OnClickListener {
    private LinearLayout ly_command_init, ly_command_activate, lycommandsmall, linearsamalltab;
    String sDigest;
    WaterMeterService service;
    private Context mContext = null;

    //private PreferencesService preservice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.small_tab_command);

        service = new WaterMeterService(this);
        mContext = this;
        findView();
        initdata();
    }

    private void findView() {
        ly_command_activate = ((LinearLayout) findViewById(R.id.ly_command_activate));
        ly_command_init = ((LinearLayout) findViewById(R.id.ly_command_init));
        lycommandsmall = (LinearLayout) findViewById(R.id.ly_command_small);
        linearsamalltab = (LinearLayout) findViewById(R.id.linear_samalltab);

    }

    private void initdata() {
        ly_command_init.setOnClickListener(this);
        ly_command_activate.setOnClickListener(this);
        lycommandsmall.setOnClickListener(this);
    }


    private void showActiveDialog() {
        final Context ctx = this;
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.activate_dialog, null);
        final TextView tvDigest = (TextView) textEntryView.findViewById(R.id.tvDigest);
        sDigest = SimpleCrypto.generateKey();
        tvDigest.setText("挑战:" + sDigest);
        final EditText edActivate = (EditText) textEntryView.findViewById(R.id.etActivate);
        edActivate.setKeyListener(new DigitsKeyListener(false, true));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入激活码").setIcon(android.R.drawable.ic_dialog_info).setView(textEntryView)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String sActivate = edActivate.getText().toString();
                String tmp = SimpleCrypto.hmacSha(sDigest);
                if (tmp.equals(sActivate)) {
                    service.addUsed(0);
                    Toast.makeText(ctx, "激活成功！", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ctx, "激活失败，请重新激活！", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_command_init:
                service.clearWaterMeter();
                switch (service.initXMLData(0)) {  //baizze  service.reset_data()
                    case 0:
                        Toast.makeText(this,
                                "初始化成功"
                                , Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(this,
                                "打开文件失败，请先下载数据"
                                , Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(this,
                                "读取数据失败，请重新下载数据"
                                , Toast.LENGTH_LONG).show();
                        break;
                }
                break;
            case R.id.ly_command_activate:
                showActiveDialog();
                break;
            case ly_command_small:

                linearsamalltab.setBackgroundColor(ContextCompat.getColor(SmallTabCommand.this, R.color.backgroundcolor));
                showPopupWindow();
//
                break;
        }
    }

    private void showPopupWindow() {
        View viewpopipwindow = LayoutInflater.from(SmallTabCommand.this).inflate(R.layout.ppwindow_samll, null);


        final PopupWindow popupWindow = new PopupWindow(viewpopipwindow,
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);


        ColorDrawable cd = new ColorDrawable(0x000000);
        popupWindow.setBackgroundDrawable(cd);
//		WindowManager.LayoutParams lp=getWindow().getAttributes();
//		lp.alpha = 0.7f;
//		getWindow().setAttributes(lp);

        //键盘不覆盖POPUPWINDOW
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);

        popupWindow.showAtLocation(lycommandsmall, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

        popupWindow.update();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                linearsamalltab.setBackgroundColor(ContextCompat.getColor(SmallTabCommand.this, R.color.white));
            }
        });
        Button button = (Button) viewpopipwindow.findViewById(R.id.btn_pressrvation);
        Button buttonbtnclers = (Button) viewpopipwindow.findViewById(R.id.btn_clers);
        final EditText smallid = (EditText) viewpopipwindow.findViewById(R.id.edittextext_id);
        final EditText smallname = (EditText) viewpopipwindow.findViewById(R.id.edittextext_village);
        final EditText smallunitnumber = (EditText) viewpopipwindow.findViewById(R.id.edittextext_sunit);
        final EditText smallfloor = (EditText) viewpopipwindow.findViewById(R.id.edittextext_floor);
        final EditText smallhousenumber = (EditText) viewpopipwindow.findViewById(R.id.edittextext_house);


        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断输入框是否为空

                //获取输入框值
                String datasmallid = smallid.getText().toString().trim();
                String datasmallname = smallname.getText().toString().trim();
                String datasunit = smallunitnumber.getText().toString().trim();
                String datafloor = smallfloor.getText().toString().trim();
                String datahouse = smallhousenumber.getText().toString().trim();
                WaterMeterService waterMeterService = new WaterMeterService(SmallTabCommand.this);
                if (!EmptyUtils.isEmpty(datasmallid)) {

                    if (EmptyUtils.isEmpty(datasmallname)) {
                        datasmallname = "临时区";
                    }

                    if (EmptyUtils.isEmpty(datasunit)) {
                        datasunit = "1栋";
                    }

                    if (EmptyUtils.isEmpty(datafloor)) {
                        datafloor = "1";
                    }


//					if ((datasmallid.length() != 10) {
//						Toast.makeText(SmallTabCommand.this, "非法表号，添加失败！"	, Toast.LENGTH_LONG).show();
//					} else if (service.select_data(wm.id) != null) {
//						Toast.makeText(ctx, "表号已存在，添加失败！"	, Toast.LENGTH_LONG).show();
//					} else {
//						service.add_water(wm);
//						Toast.makeText(ctx, "临时表号添加成功！"	, Toast.LENGTH_LONG).show();
//					}
                    if (datasmallid.length() == 10) {
                        if (waterMeterService.select_data(datasmallid)) {

                            Toast.makeText(SmallTabCommand.this, "表已存在重新输入", Toast.LENGTH_LONG).show();
                            popupWindow.dismiss();
                        } else {
                            if (EmptyUtils.isEmpty(datahouse)) {

                                datahouse = datasmallid;
                            }

                            WaterMeter waterMeter1 = new WaterMeter(datasmallid, datasmallname, datasunit, datafloor, datahouse);
                            waterMeterService.insertWaterMeters(waterMeter1);
                            Toast.makeText(SmallTabCommand.this, "配置成功", Toast.LENGTH_LONG).show();
                            popupWindow.dismiss();
                        }

                    } else {
                        Toast.makeText(SmallTabCommand.this, "表号为10位，请检查！", Toast.LENGTH_LONG).show();
                    }


                    //所有输入框不为空 数据插入数据库


                } else {
                    Toast.makeText(SmallTabCommand.this, R.string.stable_id_Prompt, Toast.LENGTH_LONG).show();
                }


            }
        });
        buttonbtnclers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        // 设置好参数之后再show
        popupWindow.showAsDropDown(viewpopipwindow);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
//				WindowManager.LayoutParams lp=getWindow().getAttributes();
//				lp.alpha = 1f;
//				getWindow().setAttributes(lp);
                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss


            }
        });

    }
}
