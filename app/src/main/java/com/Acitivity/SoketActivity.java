

package com.Acitivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.Bean.SmallDataBean;
import com.Utils.CrcUtils;
import com.Utils.EmptyUtils;
import com.Utils.SwauthedUtils;
import com.Utils.ToastUtils;
import com.wecanws.param.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static com.Utils.CrcUtils.printHexBinary;

//import static com.Utils.CrcUtils.printHexBinary;
/**
 * Soket测试Activity
 */

public class SoketActivity extends Activity {

    private Button button;
    private EditText soketedtext;
    private Handler handler;
    private String soketdata;

    public SoketActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soket);
        final Context context;
        context = SoketActivity.this;
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soketdata = soketedtext.getText().toString().trim();

                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            // TODO Auto-generated method stub
                            //更新提示
                            Bundle b = msg.getData();
                            ToastUtils.showLongToast(SoketActivity.this, b.getString("prompt"));
                        }
                    };
                    Bundle bundle = null;
                    new Thread(new MyThread(handler, bundle,soketdata)).start();


            }
        });
        soketedtext = (EditText) findViewById(R.id.soketedtext);
    }
}

class MyThread extends Thread {
    private Handler handler;
    private Bundle bulde;
    private String socketid;

    public MyThread(Handler handler, Bundle bundle,String socketid) {
        this.handler = handler;
        this.bulde = bundle;
        this.socketid=socketid;
    }

    public void run() {
        try {
            try {
                Socket clientSocket;
                if (!EmptyUtils.isEmpty(socketid))
                {
                    clientSocket = new Socket(socketid, 11500);
                    clientSocket.setSoTimeout(5000);
                }
                else
                {
                     clientSocket = new Socket("121.196.205.55", 11500);
                    clientSocket.setSoTimeout(5000);
                }

//                SocketAddress address = new InetSocketAddress();
//                clientSocket.connect(address, 5000);
                // SocketChannel socltcjannel=SocketChannel.open(new InetSocketAddress("183.230.182.141",11500) {
                //  })
                ByteBuffer buffer = ByteBuffer.allocate(1024);

                buffer.order(ByteOrder.LITTLE_ENDIAN);

                //HD
                int n = 1;
                int sitem = 76;//表数据大小
                buffer.put((byte) 0x57);//0X57 帧标志
                buffer.put((byte) 0);//表类型
                buffer.putInt(1 + 2 + n * sitem);//包长度 //1+2+（24*n）
                buffer.putInt(0);                    //设备·
                buffer.put((byte) 0);                 //命令码
               byte x[] = {0, 0, 0, 0, 0};
               buffer.put(x);


                //命令选项
                // data
                //1+2+（24*n）
                buffer.put((byte) 0x01);       // tag
                buffer.putShort((short) (76));  // length short 2个字节
                for (int i = 0; i < n; i++) {   // values
                    buffer.putInt(1705100009);  // wid  水表ID
                    buffer.putDouble(2); // 累计值
                    buffer.putDouble(3);//新累计值
                    buffer.putFloat(2); //校准系数
                    buffer.putFloat(2); //校准系数

                    buffer.putFloat(1); //CX1
                    buffer.putFloat(1);//CX2
                    buffer.putFloat(1);//CX3
                    buffer.putFloat(1);//CX4
                    buffer.putFloat(1);//CX5
                    buffer.putFloat(1);//CX6

                    buffer.putFloat(1); //CX1
                    buffer.putFloat(1);//CX2
                    buffer.putFloat(1);//CX3
                    buffer.putFloat(1);//CX4
                    buffer.putFloat(1);//CX5
                    buffer.putFloat(1);//CX6

                }

               buffer.putShort((short)0);
                buffer.flip();
                byte[] bytes;
                bytes = new byte[buffer.limit()];
                buffer.get(bytes);
                Log.i("bytes",""+bytes);
               CrcUtils crc16 = new CrcUtils();
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
                                Log.i("byte", "" + printHexBinary(data));
                                if (smallDataBeen.get(0).getResult() != 0) {
                                    b.putString("prompt", "授权成功");
                                    message.setData(b);
                                    handler.sendMessage(message);
                                } else {
                                    b.putString("prompt", "授权失败.请联系管理员");
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

