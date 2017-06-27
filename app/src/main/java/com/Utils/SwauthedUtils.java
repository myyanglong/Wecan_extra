package com.Utils;

import com.Bean.SmallDataBean;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

class swauthreq {
    int wid;
    double acclnow;
    double acclnew;
    float cx1;
    float cx2;
    float cx3;
    float cx4;
    float cx5;

    public static byte[] reqtag1(List<swauthreq> list) {
        int itemsize = (4 + 8 * 2 + 5 * 4);   // see above data section
        ByteBuffer bb = ByteBuffer.allocate(1024);

        bb.order(ByteOrder.LITTLE_ENDIAN);

        bb.put((byte) 1);                // tag
        bb.putShort((short) (itemsize * list.size()));        // dlen
        for (int i = 0; i < list.size(); i++) {    //values
            swauthreq d = list.get(i);
            bb.putInt(d.wid);            // wid
            bb.putDouble(d.acclnow);    // accl
            bb.putDouble(d.acclnew);    // new accl
            bb.putFloat(d.cx1);            // cx1
            bb.putFloat(d.cx2);            // cx2
            bb.putFloat(d.cx3);            // cx3
            bb.putFloat(d.cx4);         // cx4
            bb.putFloat(d.cx5);            // cx5
        }
        bb.flip();

        byte[] ba = new byte[bb.limit()];
        bb.get(ba);
        return ba;
    }

    static public List<swauthreq> simreq() {
        swauthreq d = new swauthreq();
        d.wid = 1;
        d.acclnow = 2;
        d.acclnew = 3;
        d.cx1 = 4;
        d.cx2 = 5;
        d.cx3 = 6;
        d.cx4 = 7;
        d.cx5 = 8;

        List<swauthreq> list = new ArrayList<swauthreq>();
        list.add(d);

        return list;
    }
}

class swauthrsp {
    int wid;
    byte result;


}


public class SwauthedUtils {



    static public List<SmallDataBean> rsptag1(byte[] ba, int offset, int len) {
        List<SmallDataBean> list = new ArrayList<SmallDataBean>();
        int itemsize = 4 + 1;   // see above data section

        if (0 != len / itemsize && 0 == len % itemsize) {
            for (int i = 0; i < len / itemsize; i++) {
                SmallDataBean d = new SmallDataBean();
                d.setWid(ba[offset + i * itemsize + 0] | (ba[offset + i * itemsize + 1] << 8) | (ba[offset + i * itemsize + 2] << 16) | (ba[offset + i * itemsize + 3] << 24));
                d.setResult(ba[offset + i * itemsize + 4]);
                list.add(d);
            }
        }

        return list;
    }

    static public void rsptaglist(byte[] ba, int offset, int len) {
        rsptag1(ba, offset, len);
        //return null;
    }

    static private int[] ccitt16tbl = {
            0x0000, 0x1189, 0x2312, 0x329B, 0x4624, 0x57AD, 0x6536, 0x74BF,
            0x8C48, 0x9DC1, 0xAF5A, 0xBED3, 0xCA6C, 0xDBE5, 0xE97E, 0xF8F7,
            0x1081, 0x0108, 0x3393, 0x221A, 0x56A5, 0x472C, 0x75B7, 0x643E,
            0x9CC9, 0x8D40, 0xBFDB, 0xAE52, 0xDAED, 0xCB64, 0xF9FF, 0xE876,
            0x2102, 0x308B, 0x0210, 0x1399, 0x6726, 0x76AF, 0x4434, 0x55BD,
            0xAD4A, 0xBCC3, 0x8E58, 0x9FD1, 0xEB6E, 0xFAE7, 0xC87C, 0xD9F5,
            0x3183, 0x200A, 0x1291, 0x0318, 0x77A7, 0x662E, 0x54B5, 0x453C,
            0xBDCB, 0xAC42, 0x9ED9, 0x8F50, 0xFBEF, 0xEA66, 0xD8FD, 0xC974,
            0x4204, 0x538D, 0x6116, 0x709F, 0x0420, 0x15A9, 0x2732, 0x36BB,
            0xCE4C, 0xDFC5, 0xED5E, 0xFCD7, 0x8868, 0x99E1, 0xAB7A, 0xBAF3,
            0x5285, 0x430C, 0x7197, 0x601E, 0x14A1, 0x0528, 0x37B3, 0x263A,
            0xDECD, 0xCF44, 0xFDDF, 0xEC56, 0x98E9, 0x8960, 0xBBFB, 0xAA72,
            0x6306, 0x728F, 0x4014, 0x519D, 0x2522, 0x34AB, 0x0630, 0x17B9,
            0xEF4E, 0xFEC7, 0xCC5C, 0xDDD5, 0xA96A, 0xB8E3, 0x8A78, 0x9BF1,
            0x7387, 0x620E, 0x5095, 0x411C, 0x35A3, 0x242A, 0x16B1, 0x0738,
            0xFFCF, 0xEE46, 0xDCDD, 0xCD54, 0xB9EB, 0xA862, 0x9AF9, 0x8B70,
            0x8408, 0x9581, 0xA71A, 0xB693, 0xC22C, 0xD3A5, 0xE13E, 0xF0B7,
            0x0840, 0x19C9, 0x2B52, 0x3ADB, 0x4E64, 0x5FED, 0x6D76, 0x7CFF,
            0x9489, 0x8500, 0xB79B, 0xA612, 0xD2AD, 0xC324, 0xF1BF, 0xE036,
            0x18C1, 0x0948, 0x3BD3, 0x2A5A, 0x5EE5, 0x4F6C, 0x7DF7, 0x6C7E,
            0xA50A, 0xB483, 0x8618, 0x9791, 0xE32E, 0xF2A7, 0xC03C, 0xD1B5,
            0x2942, 0x38CB, 0x0A50, 0x1BD9, 0x6F66, 0x7EEF, 0x4C74, 0x5DFD,
            0xB58B, 0xA402, 0x9699, 0x8710, 0xF3AF, 0xE226, 0xD0BD, 0xC134,
            0x39C3, 0x284A, 0x1AD1, 0x0B58, 0x7FE7, 0x6E6E, 0x5CF5, 0x4D7C,
            0xC60C, 0xD785, 0xE51E, 0xF497, 0x8028, 0x91A1, 0xA33A, 0xB2B3,
            0x4A44, 0x5BCD, 0x6956, 0x78DF, 0x0C60, 0x1DE9, 0x2F72, 0x3EFB,
            0xD68D, 0xC704, 0xF59F, 0xE416, 0x90A9, 0x8120, 0xB3BB, 0xA232,
            0x5AC5, 0x4B4C, 0x79D7, 0x685E, 0x1CE1, 0x0D68, 0x3FF3, 0x2E7A,
            0xE70E, 0xF687, 0xC41C, 0xD595, 0xA12A, 0xB0A3, 0x8238, 0x93B1,
            0x6B46, 0x7ACF, 0x4854, 0x59DD, 0x2D62, 0x3CEB, 0x0E70, 0x1FF9,
            0xF78F, 0xE606, 0xD49D, 0xC514, 0xB1AB, 0xA022, 0x92B9, 0x8330,
            0x7BC7, 0x6A4E, 0x58D5, 0x495C, 0x3DE3, 0x2C6A, 0x1EF1, 0x0F78,
    };

//    static short Ccitt16(byte[] buf, int offset, int len, short seed) {
//        int crc = 0;
//        int v = 0;
//
//        crc = seed & 0xffff;
//        for (int i = 0; i < len; i++) {
//            v = buf[offset + i];
//            crc = (ccitt16tbl[(crc ^ v) & 0xFF] ^ (crc >>> 8)) & 0xffff;
//        }
//
//        return (short) crc;
//    }


    static short Ccitt16(byte[] buf, int offset, int len, short seed) {
        int crc = 0;
        int v = 0;
        int x = 0;

        crc = seed & 0xffff;
        for (int i = 0; i < len; i++) {
            v = buf[offset + i];
            v &= 0xff;

            x = ccitt16tbl[(crc ^ v) & 0xFF];
            x &= 0xffff;

            crc = (x ^ (crc >>> 8));
            crc &= 0xffff;
        }

        return (short) (crc & 0xffff) ;
    }





    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    static public String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            //r.append("0x");
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
            //r.append(",");
            r.append(" ");
        }
        return r.toString();
    }

    static final byte HDFLAG = 0x57;
    static final int HDFLEN = 16;
    static final int TLSize = (1 + 2);
    static final int CRCSIZE = 2;

    static public void encode(List<byte[]> list) {
        ByteBuffer bb = ByteBuffer.allocate(1024);
        bb.order(ByteOrder.LITTLE_ENDIAN);

		/* 0: get total length */
        int tlen = 0;
        for (byte[] d : list) {
            tlen += d.length;
        }

        // 1: fill hd
        bb.put(HDFLAG);                        // hd flag
        bb.put((byte) 0);                    // type
        bb.putInt(tlen);                    // payload length
        bb.putInt(0);                        // dev id
        for (int i = 0; i < 6; i++) {            // cmd & cmd oper(5B)
            bb.put((byte) 0);
        }

        // 2: fill tlv
        for (byte[] d : list) {
            bb.put(d);
        }

        // 3: fill crc with dummy
        bb.putShort((short) 0);

        bb.flip();
        byte[] ba = new byte[bb.limit()];
        bb.get(ba);

        // 3: cal crc & fill it
        short crc = Ccitt16(ba, HDFLEN, ba.length - (HDFLEN + 2), (short) 0xffff);
        ba[ba.length - 2] = (byte) (crc);
        ba[ba.length - 1] = (byte) (crc >> 8);

        System.out.println(printHexBinary(ba));
    }

    static int hexle2int(byte low, byte high) {
        int x1, x2;
        x1 = low;
        x2 = high;

        x1 &= 0xff;
        x2 &= 0xff;

        return x1 + x2 * 256;
    }


    static public List<SmallDataBean> decode(byte[] ba) {
        //DataInputStream d = new DataInputStream( new ByteArrayInputStream(ba) );
        List<SmallDataBean> listsm=new ArrayList<>();
        // 1: check HD & totallen
        if (HDFLAG != ba[0] || ba.length < (HDFLEN + CRCSIZE)) {
            return null;
        }

        // 2: get & check payload len
        int payloadlen = ba[2] | (ba[3] << 8) | (ba[4] << 16) | (ba[5] << 24);
        if ((payloadlen + HDFLEN + CRCSIZE) > ba.length) {
            return null;
        }

        // 3: check crc
        {

            int crc = Ccitt16(ba, HDFLEN, payloadlen, (short) 0xffff) & 0xffff;
            int crc2 = hexle2int(ba[HDFLEN + payloadlen], ba[HDFLEN + payloadlen+1]);

//            int  crc = Ccitt16(ba, HDFLEN, payloadlen, (short) 0xffff) & 0xffff;
//            int  crc2 = (ba[HDFLEN + payloadlen]) & 0xff +  (ba[HDFLEN + payloadlen + 1] << 8) & 0xffff ;
            if (crc != crc2) {
                return null;
            }
        }

        // 4: check tlv validate
        int i;
        for (i = 0; i < payloadlen; ) {
            int dlen = ba[HDFLEN + i + 1] | (ba[HDFLEN + i + 2] << 8);
            i += TLSize + dlen;
        }
        if (i != payloadlen) {
            return null;
        }

        // 5: process tag
        for (i = 0; i < payloadlen; ) {
            byte tag = (byte) (ba[HDFLEN + i] & ~0x80);
            int dlen = ba[HDFLEN + i + 1] | (ba[HDFLEN + i + 2] << 8);

            // add tag u support
            if (1 == tag) {
                listsm =rsptag1(ba, HDFLEN + i + 3, dlen);//这里面返回一个list
            }

            i += TLSize + dlen;

        }
        return   listsm;
    }
}

//{
// simualte req
//	byte[] x = swauthreq.reqtag1(swauthreq.simreq());
//  List<byte[]> y = new ArrayList<byte[]>();
//	y.add(x);
//  swaued.encode(y);

// simualte rsp decode
//	byte[]  in = {87, 0, 8, 0, 0, 0, 0, 0, 0, 0, (byte)128, 0, 0, 0, 0, 0, (byte)129, 5, 0, 1, 0, 0, 0, 0, 54, (byte)205};
//	swaued.decode(in);
//}
//public class SwauthedUtils {
//}