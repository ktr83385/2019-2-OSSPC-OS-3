package com.ok.main;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class Main {
	
	public static int SCREEN_WIDTH = 0;
	public static int SCREEN_HEIGHT = 0;
	public static boolean mute=true;
	
	
	public static boolean getMute() {
		return mute;
	}


	public static void setMute(boolean mute) {
		Main.mute = mute;
	}


	public static void main(String[] args) {
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		SCREEN_WIDTH = d.width;
		SCREEN_HEIGHT = d.height;
		
		new TMain();
		
		BGM sound_bgm = new BGM();
		while(getMute()) {
			try {
				sound_bgm.abc();
				Thread.sleep(192000); // replay
			} catch(Exception e) {

			}
		}
	}
	

}