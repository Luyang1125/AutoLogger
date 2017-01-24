package com.winlab.selfdrivingloggingtool.SteeringWheelAngle;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;


public class MyRunnable implements Runnable {
	
	private volatile float[] values;
	private volatile String tag;
	private volatile String timestamp;
	private volatile String dataPacket;
	
	//public MyRunnable(float[] values, String tag, String recvTime){
	public MyRunnable(String dataPacket){
		/*this.values = values;
		this.tag = tag;
		this.timestamp = recvTime;*/
		this.dataPacket = dataPacket;
		
	}
		public void run(){
			
			
			/*String[] array = new String[values.length];
			for (int i=0; i<values.length; i++) {
				array[i] = String.format("%.8f" ,values[i]);
			}*/
			//write(Global.file, tag, array );
			write(Global.file, dataPacket );
		}
		
		// private void write(FileOutputStream file, String tag, String[] values) {
		 private void write(BufferedOutputStream file, String data) {
				if (file == null) {
					Log.e("WRITING", "File does not exist");
					return;
				}

				

				try {
					file.write(data.getBytes());
					//file.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
	}