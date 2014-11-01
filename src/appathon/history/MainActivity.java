package appathon.history;

import java.util.ArrayList;
import java.util.HashMap;

import appathon.history.models.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void showResult(ArrayList<User> userMap){
        Intent intent = new Intent();
        // Pass information to the next screen
        intent.setClass( getApplicationContext(), ResultActivity.class );
        Bundle bundle = new Bundle();
        bundle.putSerializable("userMap",userMap);
        
        intent.putExtras( bundle );

        // Redirect to next screen
        startActivity( intent );
    }
}