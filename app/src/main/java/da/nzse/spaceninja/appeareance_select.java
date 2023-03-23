package da.nzse.spaceninja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class appeareance_select extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appeareance_select);
    }

    public void backToMainMenu(View view){
        Intent intent = new Intent(this, main_menu.class);
        startActivity(intent);
    }

    //TODO
    public void changeSkin(View view){

    }
}