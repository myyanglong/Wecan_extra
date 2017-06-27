package com.Utils;

import java.util.List;

/**
 * byte类型数据转化
 * Created by xiaotaozhu on 2017/6/21.
 */

public class ByteUtil {


    /**
     * 转换int为byte数组
     *
     * @param
     * @param
     * @param
     */
    public static byte[] putInt(int value)

        {
            byte[] src = new byte[4];
            src[0] =  (byte) ((value>>24) & 0xFF);
            src[1] =  (byte) ((value>>16) & 0xFF);
            src[2] =  (byte) ((value>>8) & 0xFF);
            src[3] =  (byte) (value & 0xFF);
            return src;
        }



    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 通过byte数组取到int
     *
     * @param bb
     * @param index 第几位开始
     * @return .
     */
    public static int getInt(byte[] bb, int index) {
        return (int) ((((bb[index + 3] & 0xff) << 24)
                | ((bb[index + 2] & 0xff) << 16)
                | ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));
    }

    /**
     * float转换byte
     *
     * @param
     * @param
     * @param
     */
    public static byte[] putFloat(float x) {
        byte[] data = new byte[4];
        int ivalue = Float.floatToIntBits(x);
        data[0] = (byte)(ivalue >> 24);
        data[1] = (byte)(ivalue >> 16);
        data[2] = (byte)(ivalue >>  8);
        data[3] = (byte) ivalue;
        return data;
    }

    /**
     * 通过byte数组取得float
     *
     * @param
     * @param index
     * @return
     */
    public static float getFloat(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    /**
     * double转换byte
     * .     *
     *
     * @param
     * @param
     * @param
     */
//    public static void putDouble(byte[] bb, double x, int index) {
//        // byte[] b = new byte[8];
//        long l = Double.doubleToLongBits(x);
//        for (int i = 0; i < 4; i++) {
//            bb[index + i] = new Long(l).byteValue();
//            l = l >> 8;
//        }
//    }
    public static byte[] putDouble(double Value) {
      //  long accum = Double.doubleToRawLongBits(Value);
        long x;
        int z = (int)Math.floor(Value);
        //z=(long) Value/1
        x=  (long) (((Value-z)*4294967296.0)+1);
        byte[] byteRet = new byte[8];
        byteRet[0] = (byte) ((z >> 24) & 0xFF);
        byteRet[1] = (byte) ((z >> 16) & 0xFF);
        byteRet[2] = (byte) ((z >> 8) & 0xFF);
        byteRet[3] = (byte) (z & 0xFF);

        byteRet[4] = (byte) ((x >> 24) & 0xFF);
        byteRet[5] = (byte) ((x >> 16) & 0xFF);
        byteRet[6] = (byte) ((x >> 8) & 0xFF);
        byteRet[7] = (byte) (x & 0xFF);
        return byteRet;

    }
    public static byte[] double2Bytes(double Value) {
        long accum = Double.doubleToRawLongBits(Value);
        byte[] byteRet = new byte[8];
        byteRet[0] = (byte)((accum>>56) & 0xFF);
        byteRet[1] = (byte)((accum>>48) & 0xFF);
        byteRet[2] = (byte)((accum>>40) & 0xFF);
        byteRet[3] = (byte)((accum>>32) & 0xFF);
        byteRet[4] = (byte)((accum>>24) & 0xFF);
        byteRet[5] = (byte)((accum>>16) & 0xFF);
        byteRet[6] = (byte)((accum>>8) & 0xFF);
        byteRet[7] = (byte)(accum & 0xFF);







        return byteRet;
    }


    /**
     * 通过byte数组取得double
     *
     * @param
     * @param index
     * @return
     */
    public static double getDouble(byte[] b, int index) {
        long l;
        l = b[0];
        l &= 0xff;
        l |= ((long) b[1] << 8);
        l &= 0xffff;
        l |= ((long) b[2] << 16);
        l &= 0xffffff;
        l |= ((long) b[3] << 24);
        l &= 0xffffffffl;
        l |= ((long) b[4] << 32);
        l &= 0xffffffffffl;
        l |= ((long) b[5] << 40);
        l &= 0xffffffffffffl;
        l |= ((long) b[6] << 48);
        l &= 0xffffffffffffffl;
        l |= ((long) b[7] << 56);
        return Double.longBitsToDouble(l);
    }

    /**
     * 组合多个byte[]为1个byte[]  优化后
     * @param byteList 需要组合的byte[]集合
     * @return 组合结果
     */

    public static byte[] Mergebyte(List<byte[]> byteList) {
        byte[] mergtoat=byteList.get(0);
        byte[] mergratio=byteList.get(1);
        byte[] merg1=byteList.get(2);
        byte[] merg2=byteList.get(3);
        byte[] merg3=byteList.get(4);
        byte[] merg4=byteList.get(5);
        byte[] merg5=byteList.get(6);
       // byte[] merg6=byteList.get(7);

        //第一个byte[]数组和第二个组合
        byte[] merg1plus2=new byte[mergtoat.length+mergratio.length];
        System.arraycopy(mergtoat,0,merg1plus2,0,mergtoat.length);
        System.arraycopy(mergratio,0,merg1plus2,mergtoat.length,mergratio.length);
        //一二byte[]组合数组组合第三个byte[]
        byte[] merg12plus3=new byte[mergtoat.length+mergratio.length+merg1.length];
        System.arraycopy(merg1plus2,0,merg12plus3,0,mergtoat.length+mergratio.length);
        System.arraycopy(merg1,0,merg12plus3,mergtoat.length+mergratio.length,merg1.length);

        byte[] merg123plus4=new byte[mergtoat.length+mergratio.length+merg1.length+merg2.length];
        System.arraycopy(merg12plus3,0,merg123plus4,0,mergtoat.length+mergratio.length+merg1.length);
        System.arraycopy(merg2,0,merg123plus4,mergtoat.length+mergratio.length+merg1.length,merg2.length);


        byte[] merg1234plus5=new byte[mergtoat.length+mergratio.length+merg1.length+merg2.length+merg3.length];
        System.arraycopy(merg123plus4,0,merg1234plus5,0,mergtoat.length+mergratio.length+merg1.length+merg2.length);
        System.arraycopy(merg3,0,merg1234plus5,mergtoat.length+mergratio.length+merg1.length+merg2.length,merg3.length);

        byte[] merg12345plus6=new byte[mergtoat.length+mergratio.length+merg1.length+merg2.length+merg3.length+merg4.length];
        System.arraycopy(merg1234plus5,0,merg12345plus6,0,mergtoat.length+mergratio.length+merg1.length+merg2.length+merg3.length);
        System.arraycopy(merg4,0,merg12345plus6,mergtoat.length+mergratio.length+merg1.length+merg2.length+merg3.length,merg4.length);


        byte[] data=new byte[mergtoat.length+mergratio.length+merg1.length+merg2.length+merg3.length+merg4.length+merg5.length];
        System.arraycopy(merg12345plus6,0,data,0,mergtoat.length+mergratio.length+merg1.length+merg2.length+merg3.length+merg4.length);
        System.arraycopy(merg5,0,data,mergtoat.length+mergratio.length+merg1.length+merg2.length+merg3.length+merg4.length,merg5.length);

//        byte[] data=new byte[mergtoat.length+mergratio.length+merg1.length+merg2.length+merg3.length+merg4.length+merg5.length+merg6.length];
//        System.arraycopy(merg123456plus7,0,data,0,mergtoat.length+mergratio.length+merg1.length+merg2.length+merg3.length+merg4.length+merg5.length);
//        System.arraycopy(merg6,0,data,mergtoat.length+mergratio.length+merg1.length+merg2.length+merg3.length+merg4.length+merg5.length,merg6.length);


        return  data;
    }


//    public static byte[] putbyte(List<byte[]> byteList) {
//        int length = 0;
//        for (byte[] merg:byteList){
//            length+=merg.length;
//        }
//        byte[] bytes = new byte[length];
//        int copyLength = 0;
//        for (byte[] temp:byteList){
//            if (copyLength == 0){
//                System.arraycopy(temp,0,bytes,copyLength,temp.length);
//            }else {
//                System.arraycopy(temp,0,bytes,copyLength-1,temp.length);
//            }
//            copyLength += temp.length;
//        }
//        return bytes;
//    }

    /**
     * 组合多个byte[]为1个byte[]  优化后
     * @param byteList 需要组合的byte[]集合
     * @return 组合结果
     */
    public static byte[] putbyte(List<byte[]> byteList) {
        int length = 0;
        for (byte[] merg:byteList){
            length+=merg.length;
        }
        byte[] bytes = new byte[length];
        int copyLength = 0;
        for (byte[] temp:byteList){
            System.arraycopy(temp,0,bytes,copyLength,temp.length);
            copyLength += temp.length;
        }
        return bytes;
    }


//
//
//
//        if (byteList.size() > 0) {
//            byte[] merge = new byte[32];
//            byte[] merge12 = new byte[0];
//            byte[] mergebyte;
//            int i = 0;
//            int length = byteList.size();
//            int bytelength = 0;
//            if (i == 0) {
//                byte[] mergebb1 = byteList.get(i);
//                byte[] mergebb2 = byteList.get(i + 1);
//                merge12=new byte[mergebb1.length+mergebb2.length];
//                System.arraycopy(mergebb1, 0, merge12, 0, mergebb1.length);
//                System.arraycopy(mergebb2, 0, merge12, mergebb1.length, mergebb2.length);
//                bytelength = mergebb1.length + mergebb2.length;
//                i++;
//            }
//            for (i = 1; i < length-1; i++) {
//               if (i==1)
//               {
//                   byte[] mergebb = byteList.get(i);
//                   mergebyte=new byte[bytelength+mergebb.length];
//                   System.arraycopy(merge12, 0, mergebyte, 0, bytelength);
//                   System.arraycopy(mergebb, 0, mergebyte, bytelength,bytelength);
//                   bytelength=+mergebb.length;
//               }
//               else {
//                   byte[] mergebb = byteList.get(i);
//                   mergebyte = new byte[bytelength + mergebb.length];
//                   System.arraycopy(merge, 0, mergebyte, 0, bytelength);
//                   System.arraycopy(mergebb, 0, mergebyte, bytelength, bytelength);
//                   bytelength = +mergebb.length;
//               }
//            }
//            return merge;
//        }
//        return null;
//    }
}