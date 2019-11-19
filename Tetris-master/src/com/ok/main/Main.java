package com.ok.main;
import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class Main {

	public static final int SCREEN_WIDTH = 1280;
	public static final int SCREEN_HEIGHT = 721;
	
	public static void main(String[] args) {
		new TMain();

		BGM sound_bgm = new BGM();
		while(true) {
			try {
				sound_bgm.abc();
				Thread.sleep(192000); // replay
			} catch(Exception e) {

			}
		}
	}

}