package com.Acitivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.Utils.ByteUtil;
import com.Utils.EncryptUtils;
import com.wecanws.param.R;

import java.util.ArrayList;
import java.util.List;

import static com.ftdi.j2xx.hyperterm.J2xxHyperTerm.HexString2Bytes;

/**
 * AES加密测试Activity
 * AES加密为16进制
 */

public class AesActivity extends Activity implements View.OnClickListener {

    private TextView txtAesdata, txtDecryptdata;
    private String aes;
    private byte[] key;
    private byte[] writeBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aes);

        findViewById(R.id.btn_aes).setOnClickListener(this);
        txtAesdata = (TextView) findViewById(R.id.txt_aesdata);
        txtDecryptdata = (TextView) findViewById(R.id.txt_decryptdata);
        findViewById(R.id.btn_decrypt).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_aes:
                //TODO implement
                //转化为16进制
                String dataid = "1705100009";
                int meterId = Integer.valueOf(dataid);
                String strIdHex = String.format("%02X%02X%02X%02X", meterId & 0xff, (meterId >> 8) & 0xff, (meterId >> 16) & 0xff, (meterId >> 24) & 0xff);

                Double totalb = Double.parseDouble("1000.005");

                // String total = Integer.to(Double.doubleToLongBits(Double.parseDouble("500")));
                int bigOfTotal = (int) Math.floor(totalb);
                //int smallOfTotal = ((int)((total - bigOfTotal) * 1000) << 16) / 1000;
                double tmpF = (totalb - bigOfTotal);
                tmpF *= 4294967296.0;

                long smallOfTotal = (long) Math.floor(tmpF) & 0xffffffff;
                String strTotalHex = String.format("%02X%02X%02X%02X%02X%02X%02X%02X", (bigOfTotal >> 24) & 0xff, (bigOfTotal >> 16) & 0xff, (bigOfTotal >> 8) & 0xff, bigOfTotal & 0xff, (smallOfTotal >> 24) & 0xff, (smallOfTotal >> 16) & 0xff, (smallOfTotal >> 8) & 0xff, smallOfTotal & 0xff);
                String zRatio = Integer.toHexString(Float.floatToIntBits(Float.parseFloat("1.0")));
                String zCX1 = Integer.toHexString(Float.floatToIntBits(Float.parseFloat("1.0")));
                String zCX2 = Integer.toHexString(Float.floatToIntBits(Float.parseFloat("1.0")));
                String zCX3 = Integer.toHexString(Float.floatToIntBits(Float.parseFloat("1.0"))
                );
                String zCX4 = Integer.toHexString(Float.floatToIntBits(Float.parseFloat("1.0")));
                String zCX5 = Integer.toHexString(Float.floatToIntBits(Float.parseFloat("1.0")));
                String zCX6 = Integer.toHexString(Float.floatToIntBits(Float.parseFloat("1.0")));


                byte[] btotalb=ByteUtil.putDouble(totalb);//累计流量
                byte[] bration=ByteUtil.putFloat(Float.parseFloat("1.0"));
                byte[] bcx1=ByteUtil.putFloat(Float.parseFloat("1.0"));
                byte[] bcx2=ByteUtil.putFloat(Float.parseFloat("1.0"));
                byte[] bcx3=ByteUtil.putFloat(Float.parseFloat("1.0"));
                byte[] bcx4=ByteUtil.putFloat(Float.parseFloat("1.0"));
                byte[] bcx5=ByteUtil.putFloat(Float.parseFloat("1.0"));
                byte[] bcx6=ByteUtil.putFloat(Float.parseFloat("1.0"));
                List<byte[]> mergebyte=new ArrayList<byte[]>();
                mergebyte.add(btotalb);
                mergebyte.add(bration);
                mergebyte.add(bcx1);
                mergebyte.add(bcx2);
                mergebyte.add(bcx3);
                mergebyte.add(bcx4);
                mergebyte.add(bcx5);
              //  mergebyte.add(7,bcx6);
               byte[] bdata= ByteUtil.Mergebyte(mergebyte);
                byte[] zbbdata=ByteUtil.putbyte(mergebyte);
//                byte[] Tota = total.getBytes();
//                byte[] Ratio = zRatio.getBytes();
//                byte[] CX1 = zCX1.getBytes();
//                byte[] CX2 = zCX2.getBytes();
//                byte[] CX3 = zCX3.getBytes();
//                byte[] CX4 = zCX4.getBytes();
//                byte[] CX5 = zCX5.getBytes();
//                byte[] CX6 = zCX6.getBytes();
                // int datalength = Tota.length + Ratio.length + CX1.length + CX2.length;toUpperCase
                String sdata = strAddNum("B33102E073021DDC055731" + (strTotalHex + zRatio + zCX1 + zCX2 + zCX3 + zCX4 + zCX5 + zCX6).toUpperCase() + "3B");

                writeBuffer = new byte[512];
                byte numBytes = (byte) sdata.length();
                for (int i = 0; i < numBytes; i++) {
                    writeBuffer[i] = (byte) sdata.charAt(i);
                }

                byte[] data = (strTotalHex + zRatio + zCX1 + zCX2 + zCX3 + zCX4 + zCX5 + zCX6).getBytes();
                //  byte[] data = new byte[datalength];
//                System.arraycopy(Tota, 0, data, 0, Tota.length);
//                System.arraycopy(Ratio, 0, data, Tota.length,Ratio.length + Tota.length);
//                System.arraycopy(CX1, 0, data, Ratio.length + Tota.length, CX1.length + Ratio.length + Tota.length);
//			    System.arraycopy(CX2,0,data,CX1.length,CX2.length);
//			System.arraycopy(CX3,0,data,CX2.length,CX3.length);
//			System.arraycopy(CX4,0,data,CX3.length,6);
//			System.arraycopy(CX5,0,data,6,7);
                key = new byte[16];
                byte[] zbcx1 ;
                zbcx1=ByteUtil.putFloat(Float.parseFloat("1.0"));


                byte[] zbtotalb=ByteUtil.putDouble(Double.parseDouble("100.5"));//累计流量
                byte[]ssbtotalb=ByteUtil.double2Bytes(Double.parseDouble("100.123"));//累计流量
                byte[] aaa={(byte) 0.8};
                String txttalb=EncryptUtils.bytes2HexString(zbtotalb);
                String txttalb1=EncryptUtils.bytes2HexString(ssbtotalb);
                byte[] bid = ByteUtil.intToBytes(meterId);
                byte[] zdata = new byte[zbtotalb.length + bid.length];


                System.arraycopy(zbtotalb, 0, zdata, 0, zbtotalb.length);
                System.arraycopy(bid, 0, zdata, zbtotalb.length, bid.length);

                System.arraycopy(zdata, 0, key, 0, 12);
                System.arraycopy(zbcx1, 0, key, 12, 4);


//                key =(strIdHex+total).getBytes(); int+Float+Float
//                System.arraycopy(id, 0, key, 0, id.length);
//                System.arraycopy(Tota, 0, key, id.length, Tota.length + id.length);
//                System.arraycopy(CX6, 0, key, Tota.length + id.length, Tota.length + id.length + CX6.length);
//                byte[] a={1,1,1,1,1};
//                byte[] b={123};
              byte  plaintext[] = {0x00, 0x00, 0x03, (byte) 0xe8, 0x00, 0x00, 0x00, 0x00, 0x3f, (byte) 0x80, 0x00, 0x00, 0x3f,(byte) 0x80, 0x00, 0x00};
             byte   keyt[] = {0x00,0x00,0x00,0x64,0x00,0x00,0x00,0x00,0x65,(byte) 0xa1,(byte)0xc2,(byte)0xe9,0x3f,(byte)0x80,0x00,0x00};

                    aes = EncryptUtils.encryptAES2HexString(plaintext, keyt);
                    String keyaes=EncryptUtils.encryptAES2HexString(bdata, key);
                    String txt16=EncryptUtils.bytes2HexString(key);
                    txtAesdata.setText(aes);


                break;
            case R.id.btn_decrypt:
                //TODO implement
                byte[] decrypt = EncryptUtils.decryptHexStringAES(aes, key);
                String txt = new String(decrypt);
                txtDecryptdata.setText(txt);

                break;
        }
    }

    public String strAddNum(String str) {
        byte[] temp = HexString2Bytes(str);
        String ret = String.format("%s%02X", str, getNum(temp, temp.length));

        return hexToAscii(ret);
    }

    private byte getNum(final byte[] buffer, final int size) {
        byte num = 0;
        for (int i = 0; i < size; i++) {
            num += buffer[i];
        }
        return num;
    }

    String hexToAscii(String s) throws IllegalArgumentException {
        int n = s.length();
        StringBuilder sb = new StringBuilder(n / 2);
        for (int i = 0; i < n; i += 2) {
            char a = s.charAt(i);
            char b = s.charAt(i + 1);
            sb.append((char) ((hexToInt(a) << 4) | hexToInt(b)));
        }
        return sb.toString();
    }

    /**
     * @param ch 需要转化16进制的内容
     * @return
     */
    static int hexToInt(char ch) {
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        throw new IllegalArgumentException(String.valueOf(ch));
    }
}
