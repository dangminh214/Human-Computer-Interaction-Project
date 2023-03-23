package da.nzse.spaceninja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class main_menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String textReceived = "";
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            textReceived = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        if (textReceived.equals("retry")) {
            Intent intentt = new Intent(this, active_game.class);
            startActivity(intentt);
        }
        SharedPreferences sharedPref = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "Default");
        String highscore = sharedPref.getString("highscore", "0");
        if(username.equals("Default")){
            Intent intenttt = new Intent(this, username_entry.class);
            intenttt.putExtra(Intent.EXTRA_TEXT, "mainMenu");
            startActivity(intenttt);
        }

        setContentView(R.layout.activity_main_menu);
        //Username Display
        TextView tvUsername = (TextView)findViewById(R.id.tvMenuUsernameL);
        tvUsername.setText(username);

        //Highscore Display
        String highscoreDisplay = "Highscore: " + highscore;
        TextView tvHighscore = (TextView)findViewById(R.id.tvMenuHighscoreL);
        tvHighscore.setText(highscoreDisplay);
    }

    public void toSettings(View view){
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }

    public void toAppeareanceSelect(View view){
        Intent intent = new Intent(this, appeareance_select.class);
        startActivity(intent);
    }

    public void toActiveGame(View view){
        Intent intent = new Intent(this, active_game.class);
        startActivity(intent);
    }
}