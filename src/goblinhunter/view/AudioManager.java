package goblinhunter.view;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;

public class AudioManager {

    private static AudioManager instance;
    private Clip clip;

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }
    private AudioManager() {}

    public void loadAndPlay(String resourcePath) {
        try {
            var is = ResourceManager.getResourceStream(resourcePath);
            if (is == null) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            clip = AudioSystem.getClip();
            clip.open(ais);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("AudioManager: cannot load " + resourcePath);
            e.printStackTrace();
        }
    }

    public void setMuted(boolean muted) {
        if (clip == null) return;
        try {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(muted ? gain.getMinimum() : 0f);
        } catch (IllegalArgumentException e) {
            // MASTER_GAIN not supported by current audio system
        }
    }
}
