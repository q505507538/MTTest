package com.mt.ble.mtble;

import java.util.List;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

@SuppressLint("NewApi")
public class MTBLEMBLE {
	private Context context;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;
	private BluetoothDevice device;

	private static final int DELAY_TIME = 15; // 操作间要有至少15ms的间隔

	public MTBLEMBLE(Context context, BluetoothManager mBluetoothManager,
			BluetoothAdapter mBluetoothAdapter) {
		this.context = context;
		this.mBluetoothManager = mBluetoothManager;
		this.mBluetoothAdapter = mBluetoothAdapter;
	}

	// 连接设备
	private String last_mac;
	private boolean connect_flag = false;
	private static final String DATA_SERVICE_UUID = "0000f1f0-0000-1000-8000-00805f9b34fb";
	private static final String TXD_CHARACT_UUID = "0000f1f1-0000-1000-8000-00805f9b34fb";
	private static final String RXD_CHARACT_UUID = "0000f1f2-0000-1000-8000-00805f9b34fb";
	private static final String CMD_SERVICE_UUID = "0000f2f0-0000-1000-8000-00805f9b34fb";
	private static final String ATSEND_CHARACT_UUID = "0000f2f1-0000-1000-8000-00805f9b34fb";
	private static final String ATRECV_CHARACT_UUID = "0000f2f2-0000-1000-8000-00805f9b34fb";
	private BluetoothGattCharacteristic txd_charact;
	private BluetoothGattCharacteristic rxd_charact;
	private BluetoothGattCharacteristic atsend_charact;
	private BluetoothGattCharacteristic atrecv_charact;

	public boolean connect(String mac, int sectime, int reset_times) {
		try {
			if (!mBluetoothAdapter.isEnabled()) { // 没有打开蓝牙
				return false;
			}
			disConnect();
			for (int i = 0; i < reset_times; i++) {
				initTimeFlag(WORK_onServicesDiscovered);
				System.out.println("开始连接");
				if ((mBluetoothGatt != null) && mac.equals(last_mac)) {
					if (connect_flag == true) { // 当前已经连接好了
						return true;
					}
					System.out.println("重连");
					mBluetoothGatt.connect();
				} else {
					System.out.println("新连接");
					disConnect(); // 新设备进行连接
					device = mBluetoothAdapter.getRemoteDevice(mac);
					if (device == null) {
						System.out.println("device == null");
						return false;
					}
					mBluetoothGatt = device.connectGatt(context, false,
							mGattCallback);
				}

				if (startTimeOut(sectime)) { // 连接超时
					System.out.println("连接超时");
					disConnect();
					continue;
				}

				connect_flag = true;

				txd_charact = getCharact(DATA_SERVICE_UUID, TXD_CHARACT_UUID);
				rxd_charact = getCharact(DATA_SERVICE_UUID, RXD_CHARACT_UUID);
				atsend_charact = getCharact(CMD_SERVICE_UUID,
						ATSEND_CHARACT_UUID);
				atrecv_charact = getCharact(CMD_SERVICE_UUID,
						ATRECV_CHARACT_UUID);

				if ((txd_charact == null) || (rxd_charact == null)
						|| (atsend_charact == null) || (atrecv_charact == null)) {
					// System.out.println("获取服务失败");
					return false;
				}

				Thread.sleep(100);

				setNotifyACK(atrecv_charact, 1000); // 接收通知使能
				setNotifyACK(rxd_charact, 1000);

				last_mac = mac;

				return true;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	// 获取特征值
	public BluetoothGattCharacteristic getCharact(String service_uuid_str,
			String charact_uuid_str) {
		if (!mBluetoothAdapter.isEnabled()) { // 没有打开蓝牙
			return null;
		}

		if (!isConnected()) {
			return null;
		}

		BluetoothGattService ble_service = mBluetoothGatt.getService(UUID
				.fromString(service_uuid_str));

		if (ble_service == null) { // 获取服务失败
			System.out.println("获取服务失败");
			return null;
		}

		BluetoothGattCharacteristic data_char = ble_service
				.getCharacteristic(UUID.fromString(charact_uuid_str));

		if (data_char == null) { // 获取特征值失败
			System.out.println("data_char == null");
			return null;
		}

		return data_char;
	}

	// 断开连接
	public boolean disConnect() {

		if (mBluetoothGatt != null) {
			System.out.println("断开连接");
			mBluetoothGatt.disconnect();
			mBluetoothGatt.close();
			mBluetoothGatt = null;
			connect_flag = false;

			return true;
		}
		return false;
	}

	// 销毁连接
	public void close() {
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	// 查看连接状态
	public boolean isConnected() {
		return connect_flag;
	}

	// BLE回调操作
	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			super.onConnectionStateChange(gatt, status, newState);
			if (newState == BluetoothProfile.STATE_CONNECTED) { // 连接成功
				System.out.println("STATE_CONNECTED");
				if (work_witch == WORK_onConnectionStateChange) {
					work_ok_flag = true;
				}
				mBluetoothGatt.discoverServices();
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // 断开连接
				System.out.println("STATE_DISCONNECTED");
				if (connect_flag) { // 如果外部已经主动调用了断开连接的话
					connect_flag = false;
					if (callback != null) {
						callback.onDisconnect();
					}
				}
			}

		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			super.onServicesDiscovered(gatt, status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				System.out.println("onServicesDiscovered");
				if (work_witch == WORK_onServicesDiscovered) {
					work_ok_flag = true;
				}
			} else {
				System.out.println("onServicesDiscovered fail-->" + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicRead(gatt, characteristic, status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (work_witch == WORK_onCharacteristicRead) {
					work_ok_flag = true;
				}
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			super.onCharacteristicChanged(gatt, characteristic);
			if (work_witch == WORK_onCharacteristicChanged) {
				work_ok_flag = true;
			}
			
			if (callback != null) {
				if(characteristic == rxd_charact){
					callback.onReviceDatas(characteristic);
				}else if(characteristic == atrecv_charact){
					callback.onReviceCMD(characteristic);
				}
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				// System.out.println("发送成功");
				if (work_witch == WORK_onCharacteristicWrite) {
					work_ok_flag = true;
				}
			} else {
				System.out.println("write fail->" + status);
			}

			super.onCharacteristicWrite(gatt, characteristic, status);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorWrite(gatt, descriptor, status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (work_witch == WORK_onDescriptorWrite) {
					work_ok_flag = true;
				}
			}
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			super.onReadRemoteRssi(gatt, rssi, status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (work_witch == WORK_onReadRemoteRssi) {
					work_ok_flag = true;
				}
				rssi_value = (rssi_value + rssi) / 2;
				// rssi_value = rssi;
			}
		}

	};

	public List<BluetoothGattService> getServiceList() {
		if (!mBluetoothAdapter.isEnabled()) { // 没有打开蓝牙
			return null;
		}

		if (!isConnected()) {
			return null;
		}

		return mBluetoothGatt.getServices();
	}

	// 设置可通知
	public boolean setNotifyACK(BluetoothGattCharacteristic data_char,
			int milsec) {
		if (exit_flag) {
			return false;
		}
		if (!mBluetoothAdapter.isEnabled()) { // 没有打开蓝牙
			return false;
		}

		if (data_char == null) {
			return false;
		}

		if (!isConnected()) {
			return false;
		}

		initTimeFlag(WORK_onDescriptorWrite);

		if ((0 != (data_char.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY))
				|| (0 != (data_char.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE))) { // 查看是否带有可通知属性
			mBluetoothGatt.setCharacteristicNotification(data_char, true);
			BluetoothGattDescriptor descriptor = data_char.getDescriptor(UUID
					.fromString("00002902-0000-1000-8000-00805f9b34fb"));
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}

		if (startTimeOut(milsec)) {
			System.out.println("startTimeOut");
			return false;
		}

		try { // 发送数据一定要有一些延迟
			Thread.sleep(DELAY_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}

	// 发送数据等待返回
	public boolean sendDataAck(byte[] value, int milsec) {
		if (!mBluetoothAdapter.isEnabled()) { // 没有打开蓝牙
			return false;
		}

		if (txd_charact == null) {
			return false;
		}

		if (!isConnected()) {
			return false;
		}

		initTimeFlag(WORK_onCharacteristicWrite);
		txd_charact.setValue(value);
		mBluetoothGatt.writeCharacteristic(txd_charact);

		if (startTimeOut(milsec)) {
			return false;
		}

		try { // 发送数据一定要有一些延迟
			Thread.sleep(DELAY_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}

	// 发送数据
	public boolean sendData(byte[] value) {
		if (!mBluetoothAdapter.isEnabled()) { // 没有打开蓝牙
			return false;
		}

		if (txd_charact == null) {
			return false;
		}

		if (!isConnected()) {
			return false;
		}

		txd_charact.setValue(value);
		mBluetoothGatt.writeCharacteristic(txd_charact);

		return true;
	}

	// 发送命令等待返回byte[]
	public boolean sendCmdAck(byte[] value, int milsec) {
		if (!mBluetoothAdapter.isEnabled()) { // 没有打开蓝牙
			return false;
		}

		if (atsend_charact == null) {
			return false;
		}

		if (!isConnected()) {
			return false;
		}

		initTimeFlag(WORK_onCharacteristicWrite);
		atsend_charact.setValue(value);
		mBluetoothGatt.writeCharacteristic(atsend_charact);

		if (startTimeOut(milsec)) {
			return false;
		}

		try { // 发送数据一定要有一些延迟
			Thread.sleep(DELAY_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}

	// 发送命令byte[]
	public boolean sendCmd(byte[] value) {
		if (!mBluetoothAdapter.isEnabled()) { // 没有打开蓝牙
			return false;
		}

		if (atsend_charact == null) {
			return false;
		}

		if (!isConnected()) {
			return false;
		}

		atsend_charact.setValue(value);
		mBluetoothGatt.writeCharacteristic(atsend_charact);

		return true;
	}

	// 发送命令等待返回String
	public boolean sendCmdAck(String value, int milsec) {
		if (!mBluetoothAdapter.isEnabled()) { // 没有打开蓝牙
			return false;
		}

		if (atsend_charact == null) {
			return false;
		}

		if (!isConnected()) {
			return false;
		}

		initTimeFlag(WORK_onCharacteristicWrite);
		atsend_charact.setValue(value);
		mBluetoothGatt.writeCharacteristic(atsend_charact);

		if (startTimeOut(milsec)) {
			return false;
		}

		try { // 发送数据一定要有一些延迟
			Thread.sleep(DELAY_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}

	// 发送命令String
	public boolean sendCmd(String value) {
		if (!mBluetoothAdapter.isEnabled()) { // 没有打开蓝牙
			return false;
		}

		if (atsend_charact == null) {
			return false;
		}

		if (!isConnected()) {
			return false;
		}

		atsend_charact.setValue(value);
		mBluetoothGatt.writeCharacteristic(atsend_charact);

		return true;
	}

	// 使能数据接收
	public void enableData(boolean enable) {
		mBluetoothGatt.setCharacteristicNotification(rxd_charact, enable);
	}

	// 使能命令接收
	public void enableCmd(boolean enable) {
		mBluetoothGatt.setCharacteristicNotification(atrecv_charact, enable);
	}

	// 读取
	private int rssi_value;

	public int getRssi(int milsec) {
		if (!mBluetoothAdapter.isEnabled()) { // 没有打开蓝牙
			return 0;
		}
		initTimeFlag(WORK_onReadRemoteRssi);

		mBluetoothGatt.readRemoteRssi();

		if (startTimeOut(milsec)) {
			return 0;
		}

		return rssi_value;
	}

	// 回调方法
	private CallBack callback;

	public interface CallBack {

		public void onReviceDatas(BluetoothGattCharacteristic data_char);

		public void onReviceCMD(BluetoothGattCharacteristic data_char);

		public void onDisconnect();
	}

	// 设置回调
	public void setCallback(CallBack callback) {
		this.callback = callback;
	}

	public BluetoothDevice getDevice() {
		return device;
	}

	// 初始化定时变量
	private int work_witch = 0;
	private final int WORK_onConnectionStateChange = 1;
	private final int WORK_onServicesDiscovered = 2;
	private final int WORK_onCharacteristicRead = 4;
	private final int WORK_onCharacteristicChanged = 5;
	private final int WORK_onCharacteristicWrite = 6;
	private final int WORK_onDescriptorWrite = 7;
	private final int WORK_onReadRemoteRssi = 8;

	private void initTimeFlag(int work_index) {
		work_witch = work_index;
		timeout_flag = false;
		work_ok_flag = false;
	}

	// 开始计时
	private boolean startTimeOut(int minsec) {
		handl.sendEmptyMessageDelayed(HANDLE_TIMEOUT, minsec);
		while (!work_ok_flag) {
			if (exit_flag) {
				return true;
			}
			if (timeout_flag) {
				return true;
			}
		}
		handl.removeMessages(HANDLE_TIMEOUT);

		return false;
	}

	// 强制退出
	private boolean exit_flag = false; // 强制退出

	public void exit() {
		disConnect();
		handl.removeMessages(HANDLE_TIMEOUT);
		exit_flag = true;
	}

	// 事件处理
	private static final int HANDLE_TIMEOUT = 0;
	private boolean timeout_flag = false;
	private boolean work_ok_flag = false;
	private Handler handl = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == HANDLE_TIMEOUT) {
				System.out.println("超时");
				timeout_flag = true;
				return;
			}

		}
	};
}
