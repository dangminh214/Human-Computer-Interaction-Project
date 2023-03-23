package da.nzse.spaceninja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ended_game extends AppCompatActivity {

    int score = 0;
    String highscore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ended_game);
        SharedPreferences sharedPref = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        highscore = sharedPref.getString("highscore", "0");

        Intent intent = getIntent();
        score = intent.getIntExtra("score", 0);
        String text = Integer.toString(score);
        String scoreDisplay = "Score: " + text;
        TextView tvScore = (TextView)findViewById(R.id.textViewScore);
        tvScore.setText(scoreDisplay);
        updateHighscore();
        editor.putString("highscore", highscore);
        editor.commit();

        String highscoreDisplay = "Highscore: " + highscore;
        TextView tvHighscore = (TextView)findViewById(R.id.textViewHighScore);    //Highscore Display
        tvHighscore.setText(highscoreDisplay);
    }

    public void updateHighscore(){
        int highscoreValue = Integer.parseInt(highscore);
        if(score > highscoreValue){
            highscore = Integer.toString(score);
        }
    }

    public void toMainMenu(View view){
        Intent intent = new Intent(this, main_menu.class);
        startActivity(intent);
    }

    public void toActiveGame(View view){
        Intent intent = new Intent(this, active_game.class);
        startActivity(intent);
    }
}