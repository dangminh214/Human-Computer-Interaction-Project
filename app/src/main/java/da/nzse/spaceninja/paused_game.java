package da.nzse.spaceninja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class paused_game extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paused_game);
    }

    public void toSettings(View view){
        Intent intent = new Intent(this, settings.class);
        intent.putExtra(Intent.EXTRA_TEXT, "settings");
        startActivity(intent);
    }

    public void toMainMenu(View view){
        Intent intent = new Intent(this, main_menu.class);
        startActivity(intent);
    }

    public void retry(View view){
        Intent intent = new Intent(this, active_game.class);
        startActivity(intent);
    }

    public void resume(View view){
        Intent intent = new Intent(this, active_game.class);
        startActivity(intent);
    }
}