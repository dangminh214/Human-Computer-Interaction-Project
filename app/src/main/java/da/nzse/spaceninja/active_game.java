package da.nzse.spaceninja;


import static da.nzse.spaceninja.settings.mediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import da.nzse.engine.GameView;
import da.nzse.engine.GameViewEventListener;


public class active_game extends AppCompatActivity {

    GameView mView;
    ImageButton mDialogButton;
    Button home, settings, retry, resume;
    TextView mScore;
    int speedStat;
    int score;

    //MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_game);

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.pendulum_cut);
        }

        mediaPlayer.start();
        mediaPlayer.setLooping(true);


        SharedPreferences sharedPref = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        speedStat = sharedPref.getInt("speed", 1);

        mView = (GameView) findViewById(R.id.glsvGame);
        mScore = (TextView) findViewById(R.id.stvGameHighscore);

        if(speedStat == 0){
            speedStat = 300;
        } else if(speedStat == 1){
            speedStat = 375;
        } else if(speedStat == 2){
            speedStat = 450;
        }
        mView.setmGameSpeed(speedStat);

        mView.setListener(new GameViewEventListener() {
            @Override
            public void onGameOver(GameView gameView) {
                toendedGame(gameView);
            }

            @Override
            public void onScoreChange(long currentScore) {
                score = Math.toIntExact(Long.valueOf(currentScore));
                mScore.setText("Score: " + Long.valueOf(currentScore).toString());
            }
        });
//        mView.setZOrderOnTop(false);

        mDialogButton = findViewById(R.id.imageButton);
        Dialog dialog = new Dialog(active_game.this);

        mDialogButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                mView.pause(true);

                dialog.setContentView(R.layout.activity_paused_game);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.setCancelable(false);

                home = dialog.findViewById(R.id.buttonSetting2);
                settings = dialog.findViewById(R.id.buttonSetting);
                retry = dialog.findViewById(R.id.buttonRetry);
                resume = dialog.findViewById(R.id.buttonRetry2);

                home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        toMainMenu();
                    }
                });

                settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        toSettings();
                    }
                });

                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        retry();
                    }
                });

                resume.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mView.pause(false);
                        dialog.dismiss();
                        resume();
                    }
                });
                /* WHY would you do this. this ends the game thread
                try {
                    mView.mThread.setRunning(false);
                    mView.mThread.join();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }*/
                dialog.show();
            }
        });
    }

    public void toendedGame(View view){
        mediaPlayer.stop();
        mediaPlayer = null;
        Intent intent = new Intent(this, ended_game.class);
        intent.putExtra("score", score);
        startActivity(intent);
    }

    public void toSettings(){
        Intent intent = new Intent(this, settings.class);
        intent.putExtra(Intent.EXTRA_TEXT, "settings");
        startActivity(intent);
    }

    public void toMainMenu(){
        mediaPlayer.stop();
        mediaPlayer = null;
        Intent intent = new Intent(this, main_menu.class);
        startActivity(intent);
    }

    public void retry(){
        mediaPlayer.stop();
        mediaPlayer = null;
        try {
            mView.mThread.setRunning(false);
            mView.mThread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        Intent intent = new Intent(this, main_menu.class);
        intent.putExtra(Intent.EXTRA_TEXT, "retry");
        startActivity(intent);
    }

    public void resume(){

    }
}