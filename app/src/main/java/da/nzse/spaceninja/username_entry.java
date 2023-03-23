package da.nzse.spaceninja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class username_entry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_entry);
    }

    public void backToSettings(View view){
        Intent intent = getIntent();
        String textReceived = "";
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            textReceived = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        if (textReceived.equals("mainMenu")) {
            Intent intentt = new Intent(this, main_menu.class);
            startActivity(intentt);
        } else {
            Intent intentt = new Intent(this, settings.class);
            startActivity(intentt);
        }
    }

    public void confirm(View view){
        EditText e = (EditText)findViewById(R.id.editTextTextPersonName);
        String input = e.getText().toString();
        String toastText = "Change failed due to missing input";

        if(!input.isEmpty()){
            SharedPreferences sharedPref = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("username", input);
            editor.commit();
            toastText = "Username has been changed";
        }

        //Toast
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, toastText, duration);
        toast.show();
    }
}