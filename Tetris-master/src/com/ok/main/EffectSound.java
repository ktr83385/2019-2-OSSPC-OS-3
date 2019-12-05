package com.ok.main;
import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class EffectSound {
    public void eff_game_die() {
        File eff_game_die;
        AudioInputStream stream;
        AudioFormat format;
        DataLine.Info info;

        eff_game_die = new File("../Sound/game_die.wav");

        Clip clip;

        try {
            stream = AudioSystem.getAudioInputStream(eff_game_die);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip)AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        } catch (Exception e) {
            System.out.println("err : " + e);
        }
    }
}