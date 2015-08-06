package com.mt.help;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class LogText {

	private final static String file_path = Environment
			.getExternalStorageDirectory().getAbsolutePath();

	public static void writeStr(String str) {
		File file = new File(file_path,"MTBeacon.txt");
		SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy年MM月dd日    HH:mm:ss  ");
		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
		String    now_time    =    formatter.format(curDate); 

		try {
			FileOutputStream fOut = new FileOutputStream(file, true);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(now_time+"    "+str+"\r\n");
			osw.flush();
			osw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
