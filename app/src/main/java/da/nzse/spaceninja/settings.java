package da.nzse.spaceninja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;





public class settings extends AppCompatActivity {
    public int soundEffectVolume; //0...100
    public int musicVolume; // 0...100
    public int speedStat; // 0, 1 or 2
    public static MediaPlayer mediaPlayer;

    AudioManager audioManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        //mediaPlayer = MediaPlayer.create(this, R.raw.pendulum_cut);

        SharedPreferences sharedPref = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        soundEffectVolume = sharedPref.getInt("soundeffect", 50);
        musicVolume = sharedPref.getInt("music", 50);
        speedStat = sharedPref.getInt("speed", 1);

        SeekBar soundEffect = (SeekBar) findViewById(R.id.sbSettingsSfxL);
        soundEffect.setProgress(soundEffectVolume);

        SeekBar music = (SeekBar) findViewById(R.id.sbSettingsMusicL);
        music.setMax(maxVolume);
        music.setProgress(currentVolume);
        music.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar speed = (SeekBar) findViewById(R.id.sbSettingsSpeedL);
        speed.setProgress(speedStat);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        saveSettings();
    }

    @Override
    protected void onPause(){
        super.onPause();
        saveSettings();
    }

    public void saveSettings(){
        SharedPreferences sharedPref = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        SeekBar soundEffect = (SeekBar)findViewById(R.id.sbSettingsSfxL);
        soundEffectVolume = soundEffect.getProgress();
        editor.putInt("soundeffect", soundEffectVolume);

        SeekBar music = (SeekBar)findViewById(R.id.sbSettingsMusicL);
        musicVolume = music.getProgress();
        editor.putInt("music", musicVolume);

        SeekBar speed = (SeekBar)findViewById(R.id.sbSettingsSpeedL);
        speedStat = speed.getProgress();
        editor.putInt("speed", speedStat);

        editor.commit();
    }

    public void back(View view) {
        Intent intent = getIntent();
        String textReceived = "";
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            textReceived = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        if (textReceived.equals("settings")) {
            Intent intentt = new Intent(this, active_game.class);
            startActivity(intentt);
        } else {
            Intent intentt = new Intent(this, main_menu.class);
            startActivity(intentt);
        }
    }

    public void toUsernameEntry(View view) {
        Intent intent = new Intent(this, username_entry.class);
        startActivity(intent);
    }
}