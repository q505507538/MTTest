package com.mt.help;

import com.mt.device.MTBLEDevice;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CfgInf {
	
	public static final String UuidDbName = "UuidDbName";
	public static final int UuidDbVersion = 1;
	
	public static final String PreferencesName     = "PreferencesName";
	public static final String KeyFirstUse         = "KeyFirstUse";
	public static final String KeyShowdistance_flag  = "KeyShowdistance_flag";
	public static final String KeyDynamicscantime  = "KeyDynamicscantime";
	public static final String KeyDynamicstopscantime  = "KeyDynamicstopscantime";
	public static final String KeyDynamicmisstimes  = "KeyDynamicmisstimes";
	public static final String KeyShowfinalsetbtn_flag  = "KeyShowfinalsetbtn_flag";
	public static final String KeyFilter_mtbeacon_flag  = "KeyFilter_mtbeacon_flag";
	public static final String KeyFilter_mtble_flag  = "KeyFilter_mtble_flag";
	public static final String KeyFilter_ble_flag  = "KeyFilter_ble_flag";
	public static final String KeySmartconnect_flag  = "KeySmartconnect_flag";
	
	private static CfgInf object;
	
	// 获取静态对象
	public static CfgInf getInstance() {
		if(object == null){
			object = new CfgInf();
		}
		return object;
	}
	
	// 读取保存的配置参数
	public static boolean getCfgDatas(SharedPreferences prefs, CfgInf mCfgInf){
		if (prefs.getBoolean(CfgInf.KeyFirstUse, false)) {
			mCfgInf.showdistance_flag = prefs.getBoolean(CfgInf.KeyShowdistance_flag, false);
			mCfgInf.dynamicscantime = prefs.getInt(CfgInf.KeyDynamicscantime,
					5000);
			mCfgInf.dynamicstopscantime = prefs.getInt(
					CfgInf.KeyDynamicstopscantime, 1000);
			mCfgInf.dynamicmisstimes = prefs.getInt(CfgInf.KeyDynamicmisstimes,
					2);
			mCfgInf.showfinalsetbtn_flag = prefs.getBoolean(CfgInf.KeyShowfinalsetbtn_flag, false);
			mCfgInf.filter_mtbeacon_flag = prefs.getBoolean(CfgInf.KeyFilter_mtbeacon_flag, true);
			mCfgInf.filter_mtble_flag = prefs.getBoolean(CfgInf.KeyFilter_mtble_flag, false);
			mCfgInf.filter_ble_flag = prefs.getBoolean(CfgInf.KeyFilter_ble_flag, false);
			mCfgInf.smartconnect_flag = prefs.getBoolean(CfgInf.KeySmartconnect_flag, false);
			
			return true;
		} else {
			Editor editor = prefs.edit(); // 设置不是第一次启动
			editor.putBoolean(CfgInf.KeyFirstUse, true);
			editor.putBoolean(CfgInf.KeyShowdistance_flag, mCfgInf.showdistance_flag);
			editor.putInt(CfgInf.KeyDynamicscantime, mCfgInf.dynamicscantime);
			editor.putInt(CfgInf.KeyDynamicstopscantime,
					mCfgInf.dynamicstopscantime);
			editor.putInt(CfgInf.KeyDynamicmisstimes, mCfgInf.dynamicmisstimes);
			editor.putBoolean(CfgInf.KeyShowfinalsetbtn_flag, mCfgInf.showfinalsetbtn_flag);
			editor.putBoolean(CfgInf.KeyFilter_mtbeacon_flag, mCfgInf.filter_mtbeacon_flag);
			editor.putBoolean(CfgInf.KeyFilter_mtble_flag, mCfgInf.filter_mtble_flag);
			editor.putBoolean(CfgInf.KeyFilter_ble_flag, mCfgInf.filter_ble_flag);
			editor.putBoolean(CfgInf.KeySmartconnect_flag, mCfgInf.smartconnect_flag);
			editor.commit();
			
			return false;
		}
	}
	
	// 保存配置参数
	public static void saveCfgDatas(SharedPreferences prefs, CfgInf mCfgInf){
		Editor editor = prefs.edit(); // 设置不是第一次启动
		editor.putBoolean(CfgInf.KeyFirstUse, true);
		editor.putBoolean(CfgInf.KeyShowdistance_flag, mCfgInf.showdistance_flag);
		editor.putInt(CfgInf.KeyDynamicscantime, mCfgInf.dynamicscantime);
		editor.putInt(CfgInf.KeyDynamicstopscantime,
				mCfgInf.dynamicstopscantime);
		editor.putInt(CfgInf.KeyDynamicmisstimes, mCfgInf.dynamicmisstimes);
		editor.putBoolean(CfgInf.KeyShowfinalsetbtn_flag, mCfgInf.showfinalsetbtn_flag);
		editor.putBoolean(CfgInf.KeyFilter_mtbeacon_flag, mCfgInf.filter_mtbeacon_flag);
		editor.putBoolean(CfgInf.KeyFilter_mtble_flag, mCfgInf.filter_mtble_flag);
		editor.putBoolean(CfgInf.KeyFilter_ble_flag, mCfgInf.filter_ble_flag);
		editor.putBoolean(CfgInf.KeySmartconnect_flag, mCfgInf.smartconnect_flag);
		editor.commit();
	}

	// 判断是否过滤
	public boolean isFilter(int type){
		
		if(filter_mtbeacon_flag){    // 查看是否是mtbeacon过滤
			if((type == MTBLEDevice.MTBeacon1) || (type == MTBLEDevice.MTBeacon2)){
				return true;
			}
		}
		
		if(filter_mtble_flag){
			if(type == MTBLEDevice.MTBLE){
				return true;
			}
		}
		
		if(filter_ble_flag){         // 只要有普通蓝牙过滤，则都是允许显示
			return true;
		}
		
		return false;
	}
	
	// 扫描模式选择
	public static final int STATIC_SCAN = 0;
	public static final int DYNAMIC_SCAN = 1;
	public int ScanType = STATIC_SCAN;      // 扫描模式：静态0 动态1
	
	public boolean showdistance_flag = false;   // 是否显示距离
	public int dynamicscantime = 5000;			// 动态扫描时间
	public int dynamicstopscantime = 1000;      // 动态扫描间隔时间
	public int dynamicmisstimes    = 2;         // 动态防抖次数
	
	public boolean showfinalsetbtn_flag = false;  // 显示终极部署按钮
	
	public boolean filter_mtbeacon_flag  = true;  // 过滤mtbeacon
	public boolean filter_mtble_flag     = false; // 过滤普通BLE
	public boolean filter_ble_flag       = false; // 过滤普通BLE
	
	public boolean smartconnect_flag     = true;  // 智能连接模式
}
