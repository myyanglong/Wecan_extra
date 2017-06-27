package com.wecan.service;

import android.content.Context;

import com.wecanws.param.R;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	Context ctx;

	public DBOpenHelper(Context context) {
		super(context, "weian.db", null, 2);	//<包>/databases/
		ctx = context;
	}

	public String getDbPassword() {
		String pwd = ctx.getString(R.string.db_password);
		String ret = SimpleCrypto.encrypt(pwd, "commdepart");
		return ret;
	}

	/* tb_info 数据表格主要存放水表详细信息
	 *
	 * id, time,total,status,rf,read=1//数据采集用到的字段
	 * keyid 表主键自增长
	 * id		水表ID（表号）
	 * remake	备注（如哪一栋楼）
	 * floor	楼层号
	 * door		门牌号
	 * time		采集时间
	 * action	检测action_id是否能接收
	 * total	总流量
	 * status	水表状态
	 * rf		RF状态
	 * read		采集状态（上传后自动复位为0）
	 * area_id	区域id，小区编号
	 *
	 * action_type;//设置的类型     0 集中器 1 采集器 2 中继器
	 * action_id;
	public String ac_c;
	public String ac_z;
	public int    tag = 0;
	 *
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {//是在数据库每一次被创建的时候调用的
		db.execSQL("CREATE TABLE tb_info(keyid integer primary key autoincrement, id,area_id,remarke,floor,door,time," +
				"action,total,status,rf,read,action_type,action_id,ac_c,ac_z,tag)");

		db.execSQL("CREATE TABLE tb_config(keyid integer primary key autoincrement,id,type,u_id,f_id,action,tag)");
		//db.execSQL("CREATE index userindex on tb_info (id)");
		db.execSQL("CREATE TABLE tb_big(keyid integer primary key autoincrement,id,address,type,gtime,time,total,status,min,max,value,read)");

		db.execSQL("CREATE TABLE tb_user(keyid integer primary key autoincrement,id,type,pwd)");

		db.execSQL("CREATE TABLE tb_err(keyid integer primary key autoincrement,id,img,dsc,time)");
		//<tb_err id="1501100001" img="1501100001_1,1501100001_2" dsc="水表流量异常"/>
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.execSQL("ALTER TABLE person ADD area_id integer");
	}

}
