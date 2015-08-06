package com.example.mttest;

import com.mt.ble.mtble.MTBLEMBLE;
import com.mt.help.LogText;
import com.sdk.ble.MTBLEManager;
import com.sdk.help.Helpful;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private MTBLEManager mMTBLEManager;
	private MTBLEMBLE mBle;

	private static final int DATAMODE = 0;
	private static final int CMDMODE = 1;
	private int talkmode = DATAMODE;

	private Handler handl = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
		initBLE();
	}

	// 初始化控件
//	private Spinner read_fmt_select;
	private Button qibeiButton;
	private Button pingtangButton;
	private Button xiatuiButton;
	private Button taituiButton;
	private Button zuofansenButton;
	private Button youfansenButton;
	private Button zidongfansenButton;
	private Button resetButton;
	private Button stopButton;
	private Button jiqizunbeiButton;
	private Button duanlianButton;

	private void initView() {
		// 获取按钮
		qibeiButton = (Button) findViewById(R.id.qibeiButton);
		pingtangButton = (Button) findViewById(R.id.pingtangButton);
		xiatuiButton = (Button) findViewById(R.id.xiatuiButton);
		taituiButton = (Button) findViewById(R.id.taituiButton);
		zuofansenButton = (Button) findViewById(R.id.zuofansenButton);
		youfansenButton = (Button) findViewById(R.id.youfansenButton);
		zidongfansenButton = (Button) findViewById(R.id.zidongfansenButton);
		resetButton = (Button) findViewById(R.id.resetButton);
		stopButton = (Button) findViewById(R.id.stopButton);
		jiqizunbeiButton = (Button) findViewById(R.id.jiqizunbeiButton);
		duanlianButton = (Button) findViewById(R.id.duanlianButton);
		// 设置按钮监听
		qibeiButton.setOnClickListener(ButtonOnClickListener);
		pingtangButton.setOnClickListener(ButtonOnClickListener);
		xiatuiButton.setOnClickListener(ButtonOnClickListener);
		taituiButton.setOnClickListener(ButtonOnClickListener);
		zuofansenButton.setOnClickListener(ButtonOnClickListener);
		youfansenButton.setOnClickListener(ButtonOnClickListener);
		zidongfansenButton.setOnClickListener(ButtonOnClickListener);
		resetButton.setOnClickListener(ButtonOnClickListener);
		stopButton.setOnClickListener(ButtonOnClickListener);
		jiqizunbeiButton.setOnClickListener(ButtonOnClickListener);
		duanlianButton.setOnClickListener(ButtonOnClickListener);
//		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.datafmts, android.R.layout.simple_spinner_item);
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		read_fmt_select.setAdapter(adapter);
	}

	// 初始化BLE
//	private static final String mac = "F4:B8:5E:E6:90:AC";
	private static final String mac = "78:A5:04:8D:18:2A";

	private void initBLE() {
		if (android.os.Build.VERSION.SDK_INT < 18) {
			Toast.makeText(MainActivity.this, "你out了，系统尽然还没有到android 4.3", Toast.LENGTH_LONG).show();
			return;
		}
		mMTBLEManager = MTBLEManager.getInstance();
		mMTBLEManager.init(this);
		mBle = new MTBLEMBLE(MainActivity.this, mMTBLEManager.mBluetoothManager, mMTBLEManager.mBluetoothAdapter);

		mBle.setCallback(blecallback);

		new connectThread().start();
	}

	// 按钮监听
	private boolean[] flag = { true, true, true, true, true, true, true, true };
	private OnClickListener ButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			byte[] sendbytes = null;
			switch (v.getId()) {
			case R.id.qibeiButton:
				if (flag[0]){
					sendbytes = getSendDatas("b1010a010000001b11220d0a", 1, true);
					qibeiButton.setText("起背暂停");
				}else{
					sendbytes = getSendDatas("b1010a000000001b11220d0a", 1, true);
					qibeiButton.setText("起背开始");
				}
				flag[0] = !flag[0];
				break;
			case R.id.pingtangButton:
				if (flag[1]){
					sendbytes = getSendDatas("b1010a000100001b11220d0a", 1, true);
					pingtangButton.setText("躺平暂停");
				}else{
					sendbytes = getSendDatas("b1010a000000001b11220d0a", 1, true);
					pingtangButton.setText("躺平开始");
				}
				flag[1] = !flag[1];
				break;
			case R.id.xiatuiButton:
				if (flag[2]){
					sendbytes = getSendDatas("b1010a000001001b11220d0a", 1, true);
					xiatuiButton.setText("抬腿暂停");
				}else{
					sendbytes = getSendDatas("b1010a000000001b11220d0a", 1, true);
					xiatuiButton.setText("抬腿开始");
				}
					
				flag[2] = !flag[2];
				break;
			case R.id.taituiButton:
				if (flag[3]){
					sendbytes = getSendDatas("b1010a000000011b11220d0a", 1, true);
					taituiButton.setText("折腿暂停");
				}else{
					sendbytes = getSendDatas("b1010a000000001b11220d0a", 1, true);
					taituiButton.setText("折腿开始");
				}
				flag[3] = !flag[3];
				break;
			case R.id.zuofansenButton:
				if (flag[4]){
					sendbytes = getSendDatas("b2020a010000002b11220d0a", 1, true);
					zuofansenButton.setText("左翻身暂停");
				}else{
					sendbytes = getSendDatas("b2020a000000002b11220d0a", 1, true);
					zuofansenButton.setText("左翻身开始");
				}
				flag[4] = !flag[4];
				break;
			case R.id.youfansenButton:
				if (flag[5]){
					sendbytes = getSendDatas("b2020a000100002b11220d0a", 1, true);
					youfansenButton.setText("右翻身暂停");
				}else{
					sendbytes = getSendDatas("b2020a000000002b11220d0a", 1, true);
					youfansenButton.setText("右翻身开始");
				}
				flag[5] = !flag[5];
				break;
			case R.id.zidongfansenButton:
				if (flag[6]){
					sendbytes = getSendDatas("b2020a000001002b11220d0a", 1, true);
					zidongfansenButton.setText("自动翻身暂停");
				}else{
					sendbytes = getSendDatas("b2020a000000002b11220d0a", 1, true);
					zidongfansenButton.setText("自动翻身开始");
				}
				flag[6] = !flag[6];
				break;
			case R.id.resetButton:
				sendbytes = getSendDatas("b3030a010000003b11220d0a", 1, true);
				break;
			case R.id.stopButton:
				sendbytes = getSendDatas("b3030a000100003b11220d0a", 1, true);
				break;
			case R.id.jiqizunbeiButton:
				sendbytes = getSendDatas("b8080a010000008b11220d0a", 1, true);
				break;
			case R.id.duanlianButton:
				if (flag[7]){
					sendbytes = getSendDatas("b8080a000100008b11220d0a", 1, true);
					zidongfansenButton.setText("暂停锻炼");
				}else{
					sendbytes = getSendDatas("b8080a000000008b11220d0a", 1, true);
					zidongfansenButton.setText("开始锻炼");
				}
				flag[7] = !flag[7];
				break;
			}
			sendDatas(sendbytes);
		}
	};

	// 获取发送数据
	private byte[] getSendDatas(String tmp_str, int type, boolean dis_flag) {
		byte[] tmp_byte = null;
		byte[] write_msg_byte = null;

		switch (type) {
		case 0: // 字符串类型
			if (0 == tmp_str.length())
				return null;

			write_msg_byte = tmp_str.getBytes();
			break;

		case 1: // 十六进制类型
			if (0 == tmp_str.length())
				return null;

			tmp_byte = tmp_str.getBytes();
			write_msg_byte = new byte[tmp_byte.length / 2 + tmp_byte.length % 2];
			for (int i = 0; i < tmp_byte.length; i++) {
				if ((tmp_byte[i] <= '9') && (tmp_byte[i] >= '0')) {
					if (0 == i % 2)
						write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - '0') * 16) & 0xFF);
					else
						write_msg_byte[i / 2] |= (byte) ((tmp_byte[i] - '0') & 0xFF);
				} else {
					if (0 == i % 2)
						write_msg_byte[i / 2] = (byte) (((tmp_byte[i] - 'a' + 10) * 16) & 0xFF);
					else
						write_msg_byte[i / 2] |= (byte) ((tmp_byte[i] - 'a' + 10) & 0xFF);
				}
			}
			break;

		case 2: // 十进制类型
			if (0 == tmp_str.length())
				return null;

			int data_int = Integer.parseInt(tmp_str);
			int byte_size = 0;
			for (byte_size = 0; data_int != 0; byte_size++) { // 计算占用字节数
				data_int /= 256;
			}
			write_msg_byte = new byte[byte_size];

			data_int = Integer.parseInt(tmp_str);
			for (int i = 0; i < byte_size; i++) { // 转换
				write_msg_byte[i] = (byte) (0xFF & (data_int % 256));
				data_int /= 256;
			}

			break;
		}

		if (0 == tmp_str.length())
			return null;
		// 显示
		if (dis_flag) {
			Toast.makeText(MainActivity.this, "发送" + tmp_str + "成功", Toast.LENGTH_SHORT).show();
		}

		return write_msg_byte;
	}

	// 发送数据
	private boolean sendDatas(byte[] value) {
		if (value == null) {
			return false;
		}
		switch (talkmode) {
		case DATAMODE:
			mBle.sendData(value);
			break;
		case CMDMODE:
			mBle.sendCmd(value);
			break;
		}
		return true;
	}

	// 设置回调方法
	private MTBLEMBLE.CallBack blecallback = new MTBLEMBLE.CallBack() {

		@Override
		public void onReviceDatas(final BluetoothGattCharacteristic data_char) {
			disDatas(data_char);
		}

		@Override
		public void onReviceCMD(BluetoothGattCharacteristic data_char) {
			disDatas(data_char);
		}

		@Override
		public void onDisconnect() {
			handl.post(new Runnable() {

				@Override
				public void run() {
					if (!MainActivity.this.isDestroyed()) {
						Toast.makeText(getApplicationContext(), "断开连接，正在自动重连", Toast.LENGTH_SHORT).show();
						if (mBle.isConnected()) {
							return;
						} else {
							new connectThread().start();
						}
					}
				}
			});
		}
	};

	// 显示接收数据和命令
	private boolean disDatas(final BluetoothGattCharacteristic data_char) {
		handl.post(new Runnable() {
			@Override
			public void run() {
				switch (1) {
				case 0: // String
					Toast.makeText(MainActivity.this, data_char.getStringValue(0), Toast.LENGTH_SHORT).show();
					break;
				case 1: // 16进制
					Toast.makeText(MainActivity.this, Helpful.MYBytearrayToString(data_char.getValue()), Toast.LENGTH_SHORT).show();
					break;
				case 2: // 10进制
					int count = 0;
					byte[] tmp_byte = data_char.getValue();
					for (int i = 0; i < tmp_byte.length; i++) {
						count *= 256;
						count += (tmp_byte[tmp_byte.length - 1 - i] & 0xFF);
					}
					Toast.makeText(MainActivity.this, "" + count, Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}
		});
		return true;
	}

	// 建立连接线程
	private ProgressDialog pd;
	private class connectThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				// 创建一个ProgressDialog框, 类似于loading作用
				handl.post(new Runnable() {

					@Override
					public void run() {
						pd = ProgressDialog.show(MainActivity.this, "", "正在连接", true, false);
					}

				});

				// 开始连接
				if (!mBle.connect(mac, 10000, 2)) {
					mBle.disConnect();
					handl.post(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_LONG).show();
						}

					});
				}else{
					handl.post(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_LONG).show();
						}

					});
				}

			} catch (Exception e) {
				LogText.writeStr("MTBeacon1Set AsyDataThread->" + e.toString());
			}

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBle.disConnect();
	}
}
