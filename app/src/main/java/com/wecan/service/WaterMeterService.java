package com.wecan.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import com.wecan.debug.DataType;
import com.wecan.debug.InfoType;
import com.wecanws.param.R;

import android.content.Context;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;


public class WaterMeterService {
	private DBOpenHelper dbOpenHelper;
	Context ctx;

	public WaterMeterService(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
		ctx = context;
	}

	public int getRemain() {
		int remain = 0;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		Cursor cursor = db.rawQuery("select * from tb_user where id='wecan' and type='super'", null);
		if(cursor.moveToFirst()) {
			String tmp = cursor.getString(cursor.getColumnIndex("pwd"));
			String key = ctx.getString(R.string.used_key) + "P8h9";

			tmp = SimpleCrypto.decrypt(key, tmp);

			String[] ss = tmp.split(":");
			if ((ss != null) && (ss.length == 2)) {
				long vt = Long.parseLong(ss[0]);
				long cur = System.currentTimeMillis() / 1000;
				if ((cur - vt) < (3 * 24 * 3600)) {
					int used = Integer.parseInt(ss[1]);
					if ((used >= 0) && (used < 10)) {
						remain = 10 - used;
					}
				}
			}
		}

		cursor.close();

		return remain;
	}

	public int addUsed() {
		int used = 0;
		String tmp = Long.toString(System.currentTimeMillis()/1000) + ":" + '0';
		String key = ctx.getString(R.string.used_key) + "P8h9";

		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		Cursor cursor = db.rawQuery("select * from tb_user where id='wecan' and type='super'", null);
		if(cursor.moveToFirst()) {
			String s = cursor.getString(cursor.getColumnIndex("pwd"));
			s = SimpleCrypto.decrypt(key, s);
			String[] ss = s.split(":");
			if ((ss != null) && (ss.length == 2)) {
				used = Integer.parseInt(ss[1]);
				if (used < 0) {
					used = 1;
				} else if (used < 10) {
					used += 1;
				}

				tmp = ss[0] + ":" + Integer.toString(used);
			}
		}

		cursor.close();

		tmp = SimpleCrypto.encrypt(key, tmp);
		db.execSQL("replace into tb_user(id, type, pwd) values('wecan','super',?)", new Object[]{tmp});

		return used;
	}

	public int addUsed(int num) {
		String tmp = Long.toString(System.currentTimeMillis()/1000) + ":" + Integer.toString(num);
		String key = ctx.getString(R.string.used_key) + "P8h9";

		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		tmp = SimpleCrypto.encrypt(key, tmp);
		db.execSQL("replace into tb_user(id, type, pwd) values('wecan','super',?)", new Object[]{tmp});

		return num;
	}

	public String getHmacDatas() {
		String s = "";
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		Cursor cursor = db.rawQuery("select * from tb_user where id=maintain and type=super", null);
		if(cursor.moveToFirst()) {
			s = cursor.getString(cursor.getColumnIndex("pwd"));
		}
		cursor.close();
		return s;
	}


	public void printDataList(){
		Cursor cursor;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());

		cursor = db.rawQuery("select tb_area.name as name1,tb_region.name as name2,tb_district.name as name3 from tb_area,tb_region,tb_district " +
				"where tb_district.r_id=tb_region.id and tb_region.a_id=tb_area.id", null);

		while(cursor.moveToNext()){
			Log.i("debug",cursor.getString(cursor.getColumnIndex("name1")) + "," +
					cursor.getString(cursor.getColumnIndex("name2")) + "," +
					cursor.getString(cursor.getColumnIndex("name3")));
		}
		cursor.close();
	}
	/*
	 * 添加记录
	 * @param WaterMeter
	 */
	public void save(WaterMeter watermeter){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase(dbOpenHelper.getDbPassword());
		db.execSQL("insert into tb_info(id, remarke, floor,door,read) values(?,?,?,?,1)",
				new Object[]{watermeter.id, watermeter.unit, watermeter.address,watermeter.door});
	}
	/*
	 * 删除记录
	 * @param id 记录ID
	 */
	public void delete(String id){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase(dbOpenHelper.getDbPassword());
		db.execSQL("delete from tb_info where id=?", new Object[]{id});
	}
	/*
	 * 更新记录
	 * @param person
	 */
	public void update(WaterMeter watermeter){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase(dbOpenHelper.getDbPassword());
		db.execSQL("update tb_info set action_id=?,action=? where id=?",
				new Object[]{watermeter.action_id,watermeter.action_type, watermeter.id});
	}

	public void updateNet(String id){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase(dbOpenHelper.getDbPassword());
		db.execSQL("update tb_info set tag=? where id=?", new Object[]{2, id});
		Log.i("IOT", "updateNet: " + id);
	}
	/*
	 * 添加采集到的数据，空白数据（数据库没有此ID）
	 * id,area_id,remarke,floor,door,time,action_id ,action,total,status,rf,read
	 */
	public void save_data(WaterMeter watermeter){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase(dbOpenHelper.getDbPassword());

		db.execSQL("insert into tb_info(id,time,total,status,rf,read) values(?,?,?,?,?,1)",
				new Object[]{watermeter.id,watermeter.time,watermeter.total,watermeter.status,watermeter.rf});
	}
	/**
	 * 更新采集到的数据
	 * String id, String time, String total,int status,int rf,read=1
	 */
	public void update_data(WaterMeter watermeter){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase(dbOpenHelper.getDbPassword());
		db.execSQL("update tb_info set time=?,total=?,status=?,rf=?,read=1 where id=?",
				new Object[]{watermeter.time, watermeter.total,watermeter.status + "",watermeter.rf+"",watermeter.id});
	}

	/*
	 *
	 */
	public boolean find(String id){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		Cursor cursor = db.rawQuery("select * from tb_info where id=?", new String[]{id});
		if(cursor.moveToFirst()){
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}
	/*
	 * 查询采集的数据
	 * id,area_id,remarke,floor,door,time,action_id ,action,total,status,rf,read
	 */
	public Boolean select_data(String id){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());

		Cursor cursor = db.rawQuery("select * from tb_info where id=?", new String[]{id});
		if(cursor.moveToFirst()){
			String mid = cursor.getString(cursor.getColumnIndex("id"));
			String remarke = cursor.getString(cursor.getColumnIndex("remarke"));
			String floor = cursor.getString(cursor.getColumnIndex("floor"));
			String door = cursor.getString(cursor.getColumnIndex("door"));
			String action_id = cursor.getString(cursor.getColumnIndex("action_id"));
			String action = cursor.getString(cursor.getColumnIndex("action"));
			String area_id = cursor.getString(cursor.getColumnIndex("area_id"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String total = cursor.getString(cursor.getColumnIndex("total"));
			String status = cursor.getString(cursor.getColumnIndex("status"));
			String rf = cursor.getString(cursor.getColumnIndex("rf"));
			String read = cursor.getString(cursor.getColumnIndex("read"));
			//return (new WaterMeter(mid,remarke,floor,door, action_id, action)).toString();
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}

	//db.execSQL("insert into tb_info(id,area_id,remarke,floor) values('" +"1512100001','伟岸测试','38栋','101')");
	public void saveWaterMeters(List<WaterMeter> wms){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase(dbOpenHelper.getDbPassword());
		db.execSQL("delete from tb_info");
		db.beginTransaction();//开启事务
		try{
			for(WaterMeter wm:wms)
				db.execSQL("insert into tb_info(id,area_id,remarke,floor,door,read,tag) values(?,?,?,?,?,?,?)",
						new Object[]{wm.id, wm.unit,wm.address,wm.floor,wm.door,wm.read,wm.tag});
			db.setTransactionSuccessful();//设置事务的标志为True
		}finally{
			db.endTransaction();//结束事务,有两种情况：commit,rollback,
			//事务的提交或回滚是由事务的标志决定的,如果事务的标志为True，事务就会提交，否侧回滚,默认情况下事务的标志为False
		}
	}
	public void insertWaterMeters(WaterMeter wm){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase(dbOpenHelper.getDbPassword());
		db.beginTransaction();//开启事务
		try{

				db.execSQL("insert into tb_info(id,area_id,remarke,floor,door,read,tag) values(?,?,?,?,?,?,?)",
						new Object[]{wm.id, wm.unit,wm.address,wm.floor,wm.door,wm.read,wm.tag});
			db.setTransactionSuccessful();//设置事务的标志为True
		}finally{
			db.endTransaction();//结束事务,有两种情况：commit,rollback,
			//事务的提交或回滚是由事务的标志决定的,如果事务的标志为True，事务就会提交，否侧回滚,默认情况下事务的标志为False
		}
	}

	/**
	 * 查询配置信息
	 */
	public List<String> selectConfigs(String fid){
		List<String> list= new ArrayList<String>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());

		Cursor cursor = db.rawQuery("select * from tb_config where f_id=?", new String[]{fid});
		//Cursor cursor = db.rawQuery("select * from tb_config", null);
		/*
		cursor.moveToFirst();
		long result = cursor.getLong(0);
		*/
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			//int type = cursor.getInt(cursor.getColumnIndex("type"));
			//String u_id = cursor.getString(cursor.getColumnIndex("u_id"));
			//String f_id = cursor.getString(cursor.getColumnIndex("f_id"));
			//Log.i("debug",id+","+type+"," + u_id+"," + f_id);
			list.add(id);
			List<String> temp= selectConfigs(id);
			if(temp.size() > 0){
				for(String str:temp)
					list.add(str);
			}
		}
		cursor.close();
		return list;
	}
	/**
	 * 删除配置
	 * @param id
	 */
	public void deleteConfigs(String id){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase(dbOpenHelper.getDbPassword());
		db.execSQL("delete from tb_config where id=?", new Object[]{id});
		db.execSQL("update tb_config set f_id='0' where f_id=?", new Object[]{id});
	}
	/**
	 * 获取当前小区列表
	 *
	 */
	public List<String> getListArea(){
		List<String> arealist = new ArrayList<String>();
		arealist.add("全部小区");
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		Cursor cursor = db.rawQuery("select area_id,count(*) from tb_info group by area_id", null);
		while(cursor.moveToNext()){
			String unit = cursor.getString(cursor.getColumnIndex("area_id"));
			if(unit !=null)
				arealist.add(unit);
			else
				arealist.add("未知");
		}
		cursor.close();
		return arealist;
	}
	/**
	 * 获取楼栋列表
	 * @param area	小区名称
	 * @return
	 */
	public List<String> getListBuild(String area){
		List<String> buildlist = new ArrayList<String>();
		buildlist.add("全部楼栋");
		if(area == null)
			return buildlist;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		Cursor cursor = db.rawQuery("select remarke,count(*) from tb_info where area_id=? group by remarke", new String[]{area});

		while(cursor.moveToNext()){
			String build = cursor.getString(cursor.getColumnIndex("remarke"));
			buildlist.add(build);
		}
		cursor.close();
		return buildlist;
	}
	public List<String> getListFloor(String area,String build) {
		List<String> floorlist = new ArrayList<String>();
		floorlist.add("全部楼层");
		if(area == null)
			return floorlist;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		Cursor cursor = db.rawQuery("select floor,count(*) from tb_info where area_id=? and remarke=? group by floor", new String[]{area,build});

		while(cursor.moveToNext()){
			String floor = cursor.getString(cursor.getColumnIndex("floor"));
			floorlist.add(floor);
		}
		cursor.close();
		return floorlist;
	}

	//cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=? and floor=?", new String[]{str_area,str_build,str_floor});
	public List<String> getSmallMeterIDList(int operation, int index, String area, String build, String floor){
		Cursor cursor = null;
		List<String> meterIDList = new ArrayList<String>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());

		int type = 0;
		if(operation == 1)
			type = index + 20;

		if(operation == 2)
			type = index + 10;

		switch(type){
			case 0:
				cursor = db.rawQuery("select * from tb_info", null);
				break;
			case 1:
				cursor = db.rawQuery("select * from tb_info where area_id=?", new String[]{area});
				break;
			case 2:
				cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=?", new String[]{area,build});
				break;
			case 3:
				cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=? and floor=?", new String[]{area,build,floor});
				break;
			case 10:
				cursor = db.rawQuery("select * from tb_info where read=0", null);
				break;
			case 11:
				cursor = db.rawQuery("select * from tb_info where read=0 and area_id=?", new String[]{area});
				break;
			case 12:
				cursor = db.rawQuery("select * from tb_info where read=0 and area_id=? and remarke=?", new String[]{area,build});
				break;
			case 13:
				cursor = db.rawQuery("select * from tb_info where read=0 and area_id=? and remarke=? and floor=?", new String[]{area,build,floor});
				break;
			case 20:
				cursor = db.rawQuery("select * from tb_info where tag<>2", null);
				break;
			case 21:
				cursor = db.rawQuery("select * from tb_info where area_id=? and tag<>2", new String[]{area});
				break;
			case 22:
				cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=? and tag<>2", new String[]{area,build});
				break;
			case 23:
				cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=? and floor=? and tag<>2", new String[]{area,build,floor});
				break;
			default:
				break;
		}

		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			meterIDList.add(id);
		}
		cursor.close();
		return meterIDList;
	}

	public void clearCollectData(){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase(dbOpenHelper.getDbPassword());
		db.execSQL("update tb_info set read=0, time='null' where read=1");
	}


	public boolean findOutNetList(String id){
		Cursor cursor = null;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		cursor = db.rawQuery("select * from tb_info where id=?", new String[]{id});
		while(cursor.moveToNext()){
			int tag = cursor.getInt(cursor.getColumnIndex("tag"));

			if(tag != 2)
			{
				//Log.i("IOT", id + String.format(" %d", tag));
				cursor.close();
				return true;
			}
			else
			{
				cursor.close();
				return false;
			}
		}
		cursor.close();
		return false;
	}

	public List<WaterMeter> getSmallListWater(int index,String str_area,String str_build,String str_floor,boolean flag){
		Cursor cursor = null;
		List<WaterMeter> meter = new ArrayList<WaterMeter>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());

		switch(index){
			case 0:
				cursor = db.rawQuery("select * from tb_info", null);
				break;
			case 1:
				cursor = db.rawQuery("select * from tb_info where area_id=?", new String[]{str_area});
				break;
			case 2:
				cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=?", new String[]{str_area,str_build});
				break;
			case 3:
				cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=? and floor=?", new String[]{str_area,str_build,str_floor});
				break;
			case 10:
				if(flag)
					cursor = db.rawQuery("select * from tb_info where read=1", null);
				else
					cursor = db.rawQuery("select * from tb_info where read=0", null);
				break;
			case 11:
				if(flag)
					cursor = db.rawQuery("select * from tb_info where read=1 and area_id=?", new String[]{str_area});
				else
					cursor = db.rawQuery("select * from tb_info where read=0 and area_id=?", new String[]{str_area});
				break;
			case 12:
				if(flag)
					cursor = db.rawQuery("select * from tb_info where read=1 and area_id=? and remarke=?", new String[]{str_area,str_build});
				else
					cursor = db.rawQuery("select * from tb_info where read=0 and area_id=? and remarke=?", new String[]{str_area,str_build});
				break;
			case 13:
				if(flag)
					cursor = db.rawQuery("select * from tb_info where read=1 and area_id=? and remarke=? and floor=?", new String[]{str_area,str_build,str_floor});
				else
					cursor = db.rawQuery("select * from tb_info where read=0 and area_id=? and remarke=? and floor=?", new String[]{str_area,str_build,str_floor});
				break;
			case 20:
				cursor = db.rawQuery("select * from tb_info where tag=0", null);
				break;
			case 21:
				cursor = db.rawQuery("select * from tb_info where area_id=? and tag=0", new String[]{str_area});
				break;
			case 22:
				cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=? and tag=0", new String[]{str_area,str_build});
				break;
			case 23:
				cursor = db.rawQuery("select * from tb_info where area_id=? and remarke=? and floor=? and tag=0", new String[]{str_area,str_build,str_floor});
				break;
			default:
				break;
		}
		//WaterMeter(String id, String unit, String address,String floor,String door)
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String unit = cursor.getString(cursor.getColumnIndex("area_id"));
			String address = cursor.getString(cursor.getColumnIndex("remarke"));
			String floor = cursor.getString(cursor.getColumnIndex("floor"));
			String door = cursor.getString(cursor.getColumnIndex("door"));

			if(unit == null)
				unit="";
			if(address == null)
				address="";
			if(floor == null)
				floor="";
			if(door == null)
				door="";


			switch(index){
				case 0:
				case 1:
				case 2:
				case 3:
					//meter.add(new WaterMeter(id,unit,address,floor,door,time,total,status,rf,read));
					//break;
				case 10:
				case 11:
				case 12:
				case 13:
					String time = cursor.getString(cursor.getColumnIndex("time"));
					String total = cursor.getString(cursor.getColumnIndex("total"));
					int status = cursor.getInt(cursor.getColumnIndex("status"));
					int rf = cursor.getInt(cursor.getColumnIndex("rf"));
					int read = cursor.getInt(cursor.getColumnIndex("read"));
					if(total == null)
						total = "0";
					meter.add(new WaterMeter(id,unit,address,floor,door,time,total,status,rf,read));
					break;
				default:
					String action_id = cursor.getString(cursor.getColumnIndex("action_id"));
					//int tag = cursor.getInt(cursor.getColumnIndex("tag"));
					//int bflag = cursor.getInt(cursor.getColumnIndex("bflag"));
					meter.add(new WaterMeter(id,unit,address,floor,door,action_id));
					break;
			}
		}
		db.close();
		cursor.close();
		return meter;
	}
	public List<WaterMeter> getSmallListWaterForXML(){
		Cursor cursor = null;

		List<WaterMeter> meter = new ArrayList<WaterMeter>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		cursor = db.rawQuery("select * from tb_info where read=1", null);

		//<tb_info id="1501100001" time="2014-03-04 15:30:29" total="1.792" status="3" rf="1"/>
		//<tb_info id="0000000420" action_id="00000000" action_type="0"/>
		while(cursor.moveToNext()){
			WaterMeter wm = new WaterMeter(0);
			wm.id = cursor.getString(cursor.getColumnIndex("id"));
			wm.time = cursor.getString(cursor.getColumnIndex("time"));
			wm.total = cursor.getString(cursor.getColumnIndex("total"));
			wm.status = cursor.getInt(cursor.getColumnIndex("status"));
			wm.rf = cursor.getInt(cursor.getColumnIndex("rf"));
			wm.action_type = cursor.getInt(cursor.getColumnIndex("action_type"));
			wm.action_id = cursor.getString(cursor.getColumnIndex("action_id"));
			wm.ac_c = cursor.getString(cursor.getColumnIndex("ac_c"));
			wm.ac_z = cursor.getString(cursor.getColumnIndex("ac_z"));

			wm.unit = cursor.getString(cursor.getColumnIndex("area_id"));
			wm.address = cursor.getString(cursor.getColumnIndex("remarke"));
			wm.floor = cursor.getString(cursor.getColumnIndex("floor"));
			wm.door = cursor.getString(cursor.getColumnIndex("door"));
			meter.add(wm);
		}
		cursor.close();
		return meter;
	}
	/**
	 * 测试函数
	 * @param info
	 */
	public void print_Tb_info(String info){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		Cursor cursor = db.rawQuery("select * from tb_info", null);
		/*
		cursor.moveToFirst();
		long result = cursor.getLong(0);
		area_id,remarke,floor,door
		*/
		Log.i("debug","getCount" + cursor.getCount());
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String keyid = cursor.getString(cursor.getColumnIndex("keyid"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String read = cursor.getString(cursor.getColumnIndex("read"));

			//String area_id = cursor.getString(cursor.getColumnIndex("area_id"));
			//String remarke = cursor.getString(cursor.getColumnIndex("remarke"));
			//String floor = cursor.getString(cursor.getColumnIndex("floor"));


			int status = cursor.getInt(cursor.getColumnIndex("status"));
			int rf = cursor.getInt(cursor.getColumnIndex("rf"));

			Log.i("debug",keyid+","+ id+","+time+",read=" + read+",status=" + status +",rf=" +rf);
		}
		cursor.close();
	}

	public void print_area(String info){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		Cursor cursor = db.rawQuery("select area_id,count(*) from tb_info group by area_id", null);

		Log.i("debug","getCount" + cursor.getCount());
		while(cursor.moveToNext()){

			String area_id = cursor.getString(cursor.getColumnIndex("area_id"));

			Log.i("debug","area_id=" + area_id);
		}
		cursor.close();
	}
	public void debug_set_data(){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase(dbOpenHelper.getDbPassword());
		db.execSQL("update tb_info set id=123455 where id='FFFFFFFF'");
		/*
		db.execSQL("update tb_info set area_id = '伟岸小区' ");
		db.execSQL("update tb_info set area_id = '东和春天' where keyid < 3");
		db.execSQL("update tb_info set area_id = '测试小区' where keyid = 4");
		*/
		//db.execSQL("update tb_info set read=0 where read ");
	}
	/*
	 * 获取列表
	 * String id,  time, String total,int status,int rf
	 */
	public List<WaterMeter> getInitDataList(String info){
		Cursor cursor;
		List<WaterMeter> meter = new ArrayList<WaterMeter>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());

		if(info.equals("0"))
			cursor = db.rawQuery("select * from tb_info", null);
		else
			cursor = db.rawQuery("select * from tb_info where floor=?", new String[]{info});

		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String total = cursor.getString(cursor.getColumnIndex("total"));
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			int rf = cursor.getInt(cursor.getColumnIndex("rf"));
			meter.add(new WaterMeter(id,time,total,status,rf));
		}
		cursor.close();
		return meter;
	}
	public List<WaterMeter> getDataList(Integer read,Integer area_id){
		List<WaterMeter> meter = new ArrayList<WaterMeter>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());

		//Cursor cursor = db.rawQuery("select * from tb_info where read =? and area_id=?",new Object[]{read.toString(),area_id.toString()});
		Cursor cursor = db.rawQuery("select * from tb_info",null);

		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String total = cursor.getString(cursor.getColumnIndex("total"));
			int status = cursor.getInt(cursor.getColumnIndex("status"));
			int rf = cursor.getInt(cursor.getColumnIndex("rf"));
			meter.add(new WaterMeter(id,time,total,status,rf));
		}
		cursor.close();
		return meter;
	}
	/**
	 * 清空水表数据
	 */
	public void clearWaterMeter(){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase(dbOpenHelper.getDbPassword());
		db.execSQL("delete from tb_info");
	}
	public void getAutoCount(){
		//SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		//db.rawQuery("create index userindex on tb_info (id)", null);
		//db.rawQuery("insert into tb_info(id,read) values(?,1)", new String[]{"98"});

		//db.rawQuery("delete from tb_info where id in (select id from tb_info GROUP by id having count(id)>1)", null);
		/*Cursor cursor = db.rawQuery("select count(*) from tb_info where id in (select id from tb_info GROUP by id having count(id)>1)", null);
		cursor.moveToFirst();
		long result = cursor.getLong(0);
		Log.i("debug",result + "");*/
		Log.i("debug","asdfasf");
	}
	/**
	 * 获取记录总数
	 * @return
	 */
	public long getCount(String info){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		Cursor cursor = db.rawQuery("select * from tb_info", null);
		/*
		cursor.moveToFirst();
		long result = cursor.getLong(0);
		area_id,remarke,floor,door
		*/
		Log.i("debug","getCount" + cursor.getCount());
		while(cursor.moveToNext()){
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String keyid = cursor.getString(cursor.getColumnIndex("keyid"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String read = cursor.getString(cursor.getColumnIndex("read"));
			String floor = cursor.getString(cursor.getColumnIndex("floor"));

			Log.i("debug",keyid+","+ id+","+time+",read=" + read+",floor=" + floor);
		}

		cursor.close();
		return 1;
	}
	/*
	 *this.id = id;
	this.remarke = remarke;
	this.floor = floor;
	this.door = door;
	this.time = time;
	this.action_id = action_id;
	this.action = action;

	 */

	public String TestDebug(String id){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		Cursor cursor = db.rawQuery("select * from tb_info where id=?", new String[]{id});
		if(cursor.moveToNext()){
			String mid = cursor.getString(cursor.getColumnIndex("id"));
			String remarke = cursor.getString(cursor.getColumnIndex("remarke"));
			String floor = cursor.getString(cursor.getColumnIndex("floor"));
			String door = cursor.getString(cursor.getColumnIndex("door"));
			String action_id = cursor.getString(cursor.getColumnIndex("action_id"));
			String action = cursor.getString(cursor.getColumnIndex("action"));
			//return (new WaterMeter(mid,remarke,floor,door, action_id, action)).toString();
			cursor.close();
			return "tb_info [id=" + mid + "," + remarke + " " + floor
					+ " " + door + ", " + action_id + " " + action +"]";
		}
		cursor.close();
		return "空白";
	}
	/**
	 * 查询记录
	 * @param id 记录ID
	 * @return

	public WaterMeter find(Integer action_id,Integer action){
	SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
	Cursor cursor = db.rawQuery("select * from tb_info where action_id=? and action=?", new String[]{action_id.toString(),action.toString()});
	if(cursor.moveToFirst()){
	int personid = cursor.getInt(cursor.getColumnIndex("personid"));
	int amount = cursor.getInt(cursor.getColumnIndex("amount"));
	String name = cursor.getString(cursor.getColumnIndex("name"));
	String phone = cursor.getString(cursor.getColumnIndex("phone"));
	return new Person(personid, name, phone, amount);
	}
	cursor.close();
	return null;
	}*/
	/**
	 * 分页获取记录
	 * @param offset 跳过前面多少条记录
	 * @param maxResult 每页获取多少条记录
	 * @return
	 */
	public List<WaterMeter> getScrollData(int offset, int maxResult){
		List<WaterMeter> persons = new ArrayList<WaterMeter>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		Cursor cursor = db.rawQuery("select * from person order by personid asc limit ?,?",
				new String[]{String.valueOf(offset), String.valueOf(maxResult)});
		while(cursor.moveToNext()){
			//int personid = cursor.getInt(cursor.getColumnIndex("personid"));
			//int amount = cursor.getInt(cursor.getColumnIndex("amount"));
			//String name = cursor.getString(cursor.getColumnIndex("name"));
			//String phone = cursor.getString(cursor.getColumnIndex("phone"));
			//persons.add(new WaterMeter(personid, name, phone, amount));
		}
		cursor.close();
		return persons;
	}
	/**
	 * 分页获取记录
	 * @param offset 跳过前面多少条记录
	 * @param maxResult 每页获取多少条记录
	 * @return
	 */
	public Cursor getCursorScrollData(int offset, int maxResult){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());
		Cursor cursor = db.rawQuery("select personid as _id,name,phone,amount from person order by personid asc limit ?,?",
				new String[]{String.valueOf(offset), String.valueOf(maxResult)});
		return cursor;
	}
	public static final void Data_initFile() throws Exception{
		File filex = new File(Environment.getExternalStorageDirectory(),"wecan");
		if(!filex.exists()){
			filex.mkdir();
		}
		filex = new File(Environment.getExternalStorageDirectory(),"wecan/upload");
		if(!filex.exists()){
			filex.mkdir();
		}
		filex = new File(Environment.getExternalStorageDirectory(),"wecan/download");
		if(!filex.exists()){
			filex.mkdir();
		}
	}

	public static void DB_ExportToXML(List<WaterMeter> wms, OutputStream out) throws Exception{
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(out, "UTF-8");
		serializer.startDocument("UTF-8", true);
		serializer.startTag(null, "data");

		serializer.startTag(null, "tb_smalls");
		for(WaterMeter wm : wms){
			serializer.startTag(null, "tb_small");
			serializer.attribute(null, "id", wm.id);
			serializer.attribute(null, "value", wm.total);
			serializer.attribute(null, "date", wm.time.substring(0, 10));
			serializer.endTag(null, "tb_small");
		}
		serializer.endTag(null, "tb_smalls");
		serializer.endTag(null, "data");
		serializer.endDocument();
		out.flush();
		out.close();
	}

	/**
	 * 数据库导出到XML
	 * @param type	操作人员类型
	 * @param flag	水表类型
	 * @param wms
	 * @param bms
	 * @param cgs
	 * @param users
	 * @param out
	 * @throws Exception
	 */
	public static void DB_SaveToXML(int type,int flag,List<WaterMeter> wms, OutputStream out) throws Exception{
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(out, "UTF-8");
		serializer.startDocument("UTF-8", true);
		serializer.startTag(null, "data");

		if(flag == 0){
			serializer.startTag(null, "tb_infos");
			Log.i("debug", "List " + wms.size());
			//<tb_info id="1501100001" time="2014-03-04 15:30:29" total="1.792" status="3" rf="1"/>
			//<tb_info id="0000000420" action_id="00000000" action_type="0"/>
			for(WaterMeter wm : wms){
				serializer.startTag(null, "tb_info");
				serializer.attribute(null, "id", wm.id);
				if(type == 3){
					if(wm.action_type == 0)
						serializer.attribute(null, "action_id", wm.action_id);
					else if(wm.action_type == 1)
						serializer.attribute(null, "action_id", wm.ac_c);
					else
						serializer.attribute(null, "action_id", wm.ac_z);
					serializer.attribute(null, "action_type", wm.action_type + "");
				}
				else{
					serializer.attribute(null, "time", wm.time);
					serializer.attribute(null, "total", wm.total);
					serializer.attribute(null, "status", wm.status + "");
					serializer.attribute(null, "rf", wm.rf + "");
				}
				serializer.endTag(null, "tb_info");
			}
			serializer.endTag(null, "tb_infos");
		}
		serializer.endTag(null, "data");
		serializer.endDocument();
		out.flush();
		out.close();
	}
	/*
	public void Data_SaveToDB(InputStream xml) throws Exception{

		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(xml, "UTF-8");
		int event = pullParser.getEventType();
		while(event != XmlPullParser.END_DOCUMENT){
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				break;
		}
		Log.i("debug", "List " + wms.size());
		saveConfigs(cgs);
		selectConfigs();
		//Log.i("debug", "selectConfigs " + );
		File xmlFile = new File(Environment.getExternalStorageDirectory(), "wecan/upload/updata.xml");
		FileOutputStream outStream = new FileOutputStream(xmlFile);
		Data_SaveToXML(wms,bms,cgs,users,outStream);
		//return wmList;
	}
	*/
	//* @param type	操作人员类型
	//* @param flag	水表类型
	public int initDataForUpload(int type,int flag){
		int bflag = 0;
		try {
			Data_initFile();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File xmlFile = new File(Environment.getExternalStorageDirectory(), "wecan/upload/updata.xml");
		FileOutputStream outStream = null;
		//InputStream inStream = null;
		try {
			outStream = new FileOutputStream(xmlFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			bflag = 1;
		}

		try {
			//List<WaterMeter> wms,List<BigMeter>  bms,List<Configs> cgs,List<Fault> faults
			DB_SaveToXML(type,flag,getSmallListWaterForXML(),outStream);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			bflag = 2;
		}
		return bflag;
	}

	public boolean findInList(List<String> timeList, String time){
		Iterator<String> it = timeList.iterator();
		while (it.hasNext()) {
			String value = it.next();
			if(value.equals(time))
			{
				return true;
			}
		}

		return false;
	}

	public List<String> getCollectTimeList(){
		Cursor cursor = null;
		List<String> timeList = new ArrayList<String>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase(dbOpenHelper.getDbPassword());

		cursor = db.rawQuery("select * from tb_info where read=1", null);
		while(cursor.moveToNext()){
			String tmp = cursor.getString(cursor.getColumnIndex("time"));
			String time = tmp.substring(0, 9);
			if(!findInList(timeList, time))
				timeList.add(time);
		}
		cursor.close();
		return timeList;
	}

	public int exportDataToXML(){
		int bflag = 0;
		try {
			Data_initFile();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		File xmlFile = new File(Environment.getExternalStorageDirectory(), "wecan/small/upload/data.xml");
		FileOutputStream outStream = null;
		//InputStream inStream = null;
		try {
			outStream = new FileOutputStream(xmlFile);
		} catch (FileNotFoundException e) {
			bflag = 1;
			return bflag;
		}

		try {
			DB_ExportToXML(getSmallListWaterForXML(),outStream);

		} catch (Exception e) {
			bflag = 2;
			return bflag;
		}

		return bflag;
	}

	public void initXMLSaveToDB(InputStream xml,int type) throws Exception{
		List<WaterMeter> wms = new ArrayList<WaterMeter>();
		WaterMeter  wm = null;
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(xml, "utf-8");  //baizze
		int event = pullParser.getEventType();
		while(event != XmlPullParser.END_DOCUMENT){
			switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					//单个类型判断
					if("tb_info".equals(pullParser.getName())){
						wm = new WaterMeter(type);
						wm.id = new String(pullParser.getAttributeValue(0));
						wm.unit = new String(pullParser.getAttributeValue(1));
						wm.address =new String(pullParser.getAttributeValue(2));
						wm.floor = new String(pullParser.getAttributeValue(3));
						wm.door =new String(pullParser.getAttributeValue(4));
						if(type == 2){
							wm.action_id =new String(pullParser.getAttributeValue(5));
						}
						Log.i("debug", "wm " + wm.id + "," + wm.unit + "," + wm.address + "," + wm.floor + "," + wm.door);
					}
					break;

				case XmlPullParser.END_TAG:
					if("tb_info".equals(pullParser.getName())){
						wms.add(wm);
						wm = null;
					}

					break;
			}
			event = pullParser.next();
		}

		switch(type){
			case 0://抄表人员
				if(wms.size() > 0)
					saveWaterMeters(wms);
				break;
			case 1://稽查人员
				break;
		}
		Log.i("debug", "List " + wms.size());
	}

	/**
	 * 初始化数据
	 * @param type 0 抄表人员，1 稽查人员，2 安装人员
	 * @return 1文件打开失败      2文件读取失败
	 */
	public int initXMLData(int type){
		int bflag = 0;
		//Log.i("IOT", Environment.getExternalStorageDirectory());
		File xmlFile = new File(Environment.getExternalStorageDirectory(), "wecan/small/download/data.xml");
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(xmlFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			bflag = 1;
		}
		//Data_SaveToDB(inStream);
		try {
			initXMLSaveToDB(inStream,type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			bflag = 2;
		}
		return bflag;
	}

	public int writeExcel(String fileName)
	{
		int err = 0;
		WritableWorkbook wwb = null;
		try
		{
			//创建一个可写入的工作薄(Workbook)对象
			wwb = Workbook.createWorkbook(new File(fileName));
		} catch (IOException e)
		{
			err = 1;
			e.printStackTrace();
		}
		if (wwb != null)
		{
			// 第一个参数是工作表的名称，第二个是工作表在工作薄中的位置
			WritableSheet ws = wwb.createSheet("sheet1", 0);
			// 在指定单元格插入数据
			List<WaterMeter> wms = new ArrayList<WaterMeter>();
			wms = getSmallListWaterForXML();
			//Label lbl1 = new Label(4, 5, "Excel");
			//Label bll2 = new Label(10, 10, "的操作");
			try
			{
            	/*
            	for(int i = 0;i < 5;i++){
            		Label debug = new Label(0, i, "Excel");
            		ws.addCell(debug);
            	}*/
				ws.addCell(new Label(0,0,"序号"));
				ws.addCell(new Label(1,0,"编号"));
				ws.addCell(new Label(2,0,"地址"));
				ws.addCell(new Label(3,0,"止度"));
				ws.addCell(new Label(4,0,"最小瞬时流量"));
				ws.addCell(new Label(5,0,"状态"));
				ws.addCell(new Label(6,0,"采集时间"));

				for(int i = 0;i < wms.size();i++){
					Log.i("debug", i + "," + wms.get(i).id);
					ws.addCell(new Label(0,i+1,(i+1) +""));
					ws.addCell(new Label(1,i+1,wms.get(i).id));
					ws.addCell(new Label(2,i+1,wms.get(i).unit +" " + wms.get(i).address + " " + wms.get(i).floor + "-" + wms.get(i).door));
					ws.addCell(new Label(3,i+1,wms.get(i).total));
					ws.addCell(new Label(4,i+1,wms.get(i).rf + ""));

					String status_str = null;
					if(wms.get(i).status == 0){
						status_str = "工作正常";
					}
					else{
						if((wms.get(i).status&0x01) != 0)
							status_str = "测量电路电量低";
						if((wms.get(i).status&0x02) != 0){
							if(status_str != null)
								status_str = status_str + "/已超过Q4";
							else
								status_str = "已超过Q4";
						}
						if((wms.get(i).status&0x04) != 0){
							if(status_str != null)
								status_str = status_str + "/无线模块电量低";
							else
								status_str = "无线模块电量低";
						}
						if((wms.get(i).status&0x08) != 0){
							if(status_str != null)
								status_str = status_str + "/水表安装反向";
							else
								status_str = "水表安装反向";
						}
						if(status_str == null)
							status_str = "未知报错";
					}
					ws.addCell(new Label(5,i+1,status_str));
					ws.addCell(new Label(6,i+1,wms.get(i).time));
				}
				//ws.addCell(lbl1);
				//ws.addCell(bll2);
			} catch (RowsExceededException e1)
			{
				err = 2;
				e1.printStackTrace();
			} catch (WriteException e1)
			{
				err = 3;
				e1.printStackTrace();
			}
			try
			{
				// 从内存中写入文件中
				wwb.write();
				wwb.close();
			} catch (IOException e)
			{
				err = 1;
				e.printStackTrace();
			} catch (WriteException e)
			{
				err = 2;
				e.printStackTrace();
			}
		}
		return err;
	}

	public int readExcel(String path)
	{
		int content = 0;
		try
		{
			Workbook book = Workbook.getWorkbook(new File(path));
			Sheet sheet = book.getSheet(0);
			//得到x行y列所在单元格的内容
			//String cellStr = "";
			Log.i("debug", sheet.getColumns()+","+sheet.getRows());
			List<WaterMeter> wms = new ArrayList<WaterMeter>();
			WaterMeter  wm = null;
			if(sheet.getColumns() > 1){
				for(int x = 1;x < sheet.getRows() - 1;x++){
					if(sheet.getCell(1, x).getContents().length() == 10){
						wm = new WaterMeter(sheet.getCell(1, x).getContents(),sheet.getCell(2, x).getContents(),sheet.getCell(3, x).
								getContents(),sheet.getCell(4, x).getContents(),sheet.getCell(5, x).getContents());
						wms.add(wm);
					}
				}
				if(wms.size() > 0)
					saveWaterMeters(wms);
			}
			// Log.i("debug", "wms.size: " + wms.size());

		} catch (BiffException e)
		{
			content = 1;
			e.printStackTrace();
		} catch (IOException e)
		{
			content = 2;
			e.printStackTrace();
		}
		return content;
	}
	public int reset_data2(){
		String path = "mnt/sdcard/wecan/data.xls";
		return readExcel(path);
	}
	public int export_data(){
		String path = "mnt/sdcard/wecan/updata.xls";
		return writeExcel(path);
	}

    /*数据测部分 */

	public List<DataType> readExcel2(String path)
	{
		List<DataType> dts = new ArrayList<DataType>();
		try
		{
			Workbook book = Workbook.getWorkbook(new File(path));
			Sheet sheet = book.getSheet(0);
			//得到x行y列所在单元格的内容
			Log.i("debug", sheet.getColumns()+","+sheet.getRows());
			if(sheet.getColumns() > 1){
				for(int x = 1;x < sheet.getRows();x++){
					if(sheet.getCell(0, x).getContents().length() == 7){
						DataType  dt = new DataType();
						dt.index = sheet.getCell(0, x).getContents();
						for(int i = 0;i < 6 ;i ++){
							//Log.i("debug", sheet.getCell(i + 1, x).getContents());
							dt.num[i] = Integer.valueOf(sheet.getCell(i + 1, x).getContents());
						}
						//Log.i("debug", "Td" + sheet.getCell(7, x).getContents().trim());
						dt.blue = Integer.valueOf(sheet.getCell(7, x).getContents().trim());
						dts.add(dt);
					}
				}
			}
			// Log.i("debug", "wms.size: " + wms.size());

		} catch (BiffException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return dts;
	}
	public InfoType print_list(List<DataType> dts,int size,int offset){
		InfoType it = new InfoType();
		int[] temp = new int[35];
		if(dts.size() > 0){
			for(int m = dts.size() - size - offset;m < dts.size() - offset;m ++){
				DataType dm = dts.get(m);
				for(int i = 0;i < 6 ;i ++){
					temp[dm.num[i] -1] = temp[dm.num[i] -1] + 1;
					temp[dm.num[i]]    = temp[dm.num[i]] + 1;
					temp[dm.num[i] +1] = temp[dm.num[i] +1] + 1;
				}
				Log.i("debug", "Time:" + dm.index + " " + dm.num[0] + " " + dm.num[1] + " " + dm.num[2] + " " + dm.num[3] + " " + dm.num[4] + " " + dm.num[5]  + " - " + dm.blue);
			}
			String str = "";
			for(int i=1;i< 34;i++){
				if(temp[i] > 0 ){
					str += i + " ";
					it.index ++;
				}
			}
			it.id = dts.size() - offset ;
			if(temp[dts.get(it.id).num[0]] > 0)
				it.num ++;
			if(temp[dts.get(it.id).num[1]] > 0)
				it.num ++;
			if(temp[dts.get(it.id).num[2]] > 0)
				it.num ++;
			if(temp[dts.get(it.id).num[3]] > 0)
				it.num ++;
			if(temp[dts.get(it.id).num[4]] > 0)
				it.num ++;
			if(temp[dts.get(it.id).num[5]] > 0)
				it.num ++;

			Log.i("debug",str);
			Log.i("debug", "ID:" + it.id + " index:" + it.index + " Num:" + it.num);
			//2 12 20 24 29 31
			//11 12 13 14 15 18 19 20 26 27 28 29 30 
			//1 2 3 11 12 13 14 15 18 19 20 21 23 24 25 26 27 28 29 30 31 32 
			//10 12	14	22	25	33
			//9 10 11 12 13 14 15 21 22 23 24 25 26 32 33 
		}
		return it;
	}
	public int reset_data(){
		String path = "mnt/sdcard/wecan/debug.xls";
		List<DataType> dts = new ArrayList<DataType>();
		dts = readExcel2(path);
		print_list(dts,1,1);

		return 0;
	}
}
