package appathon.history;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import appathon.history.models.User;

public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         
        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null){
        	ArrayList<User> userMap = (ArrayList<User>)bundle.getSerializable("userMap");
        }
    }
	
}
