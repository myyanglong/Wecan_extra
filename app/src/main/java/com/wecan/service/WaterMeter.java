package com.wecan.service;


/*
 * tb_info 数据表格主要存放水表详细信息
 * keyid 表主键自增长
 * id		水表ID（表号）
 * unit 	小区
 * address	单元
 * door		门牌号
 * time		采集时间
 * action_id设置（采集器或者中继器，默认为0）
 * Type     0，稽查，1，安装，2，更换，3，上传采 集数据,4上传安装数据
 * Total    累计流量（小数点后三位）
 * Status   水表状态(0-9)
 * Rf      rf状态（0-7）保留
 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
 *
 *
 */
public class WaterMeter {
    public String id;
    public String unit;
    public String address;
    public String floor;
    public String door;
    public int type;//0 抄表人员 1 稽查人员 2 安装人员

    public String time;
    public int status = 0;
    public int rf;
    public String total;
    public int read = 0;


    public int action_type;//设置的类型     0 集中器 1 采集器 2 中继器
    public String action_id;
    public String ac_c;
    public String ac_z;
    public int tag = 0;   //tag: 0 空白 1 配置 2正常 3异常   baizze

    public boolean bflag = false;//选中备用

    //安装人员初始化
    public WaterMeter(int action_type) {
        this.action_type = action_type;
    }

    /**
     * PDA设置集中器、采集器、中继器专用类
     *
     * @param id
     * @param remarke
     * @param floor
     * @param door
     * @param bflag
     */
    public WaterMeter(String id, int type, String unit, String address, String door) {
        this.id = id;
        this.type = type;
        this.unit = unit;
        this.address = address;
        this.door = door;
    }

    public WaterMeter(String id, String time, String total, int status, int rf) {
        this.id = id;
        this.time = time;
        this.total = total;
        this.status = status;
        this.rf = rf;
        //Log.i("debug", id+","+time+","+total+","+status+","+rf);
    }

    public WaterMeter(String id, String unit, String address, String floor, String door) {
        this.id = id;
        this.unit = unit;
        this.address = address;
        this.floor = floor;
        this.door = door;
    }
    //meter.add(new WaterMeter(id,unit,address,floor,door,action_id));

    /**
     * 安装人员导入XML数据结构体
     *
     * @param id
     * @param unit
     * @param address
     * @param floor
     * @param door
     * @param action_id
     */
    public WaterMeter(String id, String unit, String address, String floor, String door, String action_id) {
        this.id = id;
        this.unit = unit;
        this.address = address;
        this.floor = floor;
        this.door = door;
        this.action_id = action_id;
    }

    /**
     * 安装人员添加水表结构体
     *
     * @param id
     * @param unit
     * @param address
     * @param floor
     * @param door
     * @param action_id
     * @param action_type
     * @param ac_c
     * @param ac_z
     * @param tag
     */
    public WaterMeter(String id, String unit, String address, String floor, String door, String action_id,
                      int action_type, String ac_c, String ac_z, int tag) {
        this.id = id;
        this.unit = unit;
        this.address = address;
        this.floor = floor;
        this.door = door;
        this.action_id = action_id;
        this.action_type = action_type;
        this.ac_c = ac_c;
        this.ac_z = ac_z;
        this.tag = tag;
    }

    public WaterMeter(String id, String unit, String address, String door) {
        this.id = id;
        this.unit = unit;
        this.address = address;
        this.door = door;
    }

    public WaterMeter(String id, String unit, String address, String door, String action_id, int action_type) {
        this.id = id;
        this.unit = unit;
        this.address = address;
        this.door = door;
        this.action_id = action_id;
        this.action_type = action_type;
        this.time = "0";
    }

    public WaterMeter(String id, String unit, String address, String door, String time, String action_id, int action_type) {
        this.id = id;
        this.unit = unit;
        this.address = address;
        this.door = door;
        this.time = time;
        this.action_id = action_id;
        this.action_type = action_type;
    }

    /**
     * 小表结构体
     *
     * @param id
     * @param unit
     * @param address
     * @param floor
     * @param door
     * @param time
     * @param total
     * @param status
     * @param rf
     * @param read
     */
    public WaterMeter(String id, String unit, String address, String floor, String door, String time, String total, int status, int rf, int read) {
        this.id = id;
        this.unit = unit;
        this.address = address;
        this.floor = floor;

        this.door = door;
        this.time = time;
        this.total = total;
        this.status = status;
        this.rf = rf;
        this.read = read;
    }

    @Override
    public String toString() {
        return "tb_info [id=" + id + ", unit=" + unit + ", address=" + address
                + ", door=" + door + ", action_id=" + action_id + "]";
    }


}
