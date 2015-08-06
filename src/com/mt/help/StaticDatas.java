package com.mt.help;

import java.util.ArrayList;
import java.util.List;
import com.mt.device.MTBLEDevice;

public class StaticDatas {

	private static StaticDatas object;
	
	// 获取静态对象
	public static StaticDatas getInstance() {
		if (object == null) {
			object = new StaticDatas();
		}
		return object;
	}
	
	public StaticDatas() {
		scandevice_list = new ArrayList<MTBLEDevice>();
		
		uuid_list  = new ArrayList<UuidAndName>();
	}

	public List<MTBLEDevice> scandevice_list;     // 扫描到的设备列表
	public List<UuidAndName> uuid_list;           // 储存到的uuid和对应的设备名
}
