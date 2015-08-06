package com.sdk.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

public class MTBLEManager {
	private static MTBLEManager mMTBLEManager;

	public BluetoothManager mBluetoothManager;
	public BluetoothAdapter mBluetoothAdapter;

	/*
	 * 初始化
	 */
	public void init(Context context) {
		mBluetoothManager = (BluetoothManager) context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
	}

	/*
	 * 是否可以工作
	 */
	public boolean canWork() {
		if ((mBluetoothManager == null) || (null == mBluetoothAdapter)) {
			return false;
		}
		return true;
	}

	/*
	 * 强行设置蓝牙开关
	 */
	public void setEnable(boolean flag) {
		if (flag) {
			mBluetoothAdapter.enable();
		} else {
			mBluetoothAdapter.disable();
		}
	}

	// 获取开关状态
	public boolean isEnable() {
		return mBluetoothAdapter.isEnabled();
	}

	// 获取自身mac
	public String getSelfMac() {
		if (mBluetoothAdapter == null) {
			return null;
		}
		return mBluetoothAdapter.getAddress();
	}

	/*
	 * 获取静态对象
	 */
	public static MTBLEManager getInstance() {
		if (mMTBLEManager == null) {
			mMTBLEManager = new MTBLEManager();
		}
		return mMTBLEManager;
	}

}
