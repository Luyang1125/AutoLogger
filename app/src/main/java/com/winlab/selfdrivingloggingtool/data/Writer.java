package com.winlab.selfdrivingloggingtool.data;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;


public class Writer implements Runnable {


	private String dataPacket;
	private BufferedOutputStream bos;

	public Writer(BufferedOutputStream bos_in,String dataPacket_in){
		this.bos = bos_in;
		this.dataPacket = dataPacket_in;
	}

	@Override
	public void run() {
		write(bos, dataPacket );
	}

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