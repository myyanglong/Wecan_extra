package com.wecan.debug;

import com.wecan.service.WaterMeterService;
import android.test.AndroidTestCase;


public class TestDemo extends AndroidTestCase {
	public void testfunction() throws Exception{
		
	}
	public void testdebug() throws Exception{
		
	}
	public void initDB() throws Exception{
		WaterMeterService service = new WaterMeterService(this.getContext());
		service.reset_data2();
		//service.initRegion();
		//service.initData();
		
		//service.printDataList();
		
	}
	
}