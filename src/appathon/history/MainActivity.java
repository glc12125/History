package appathon.history;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import appathon.history.models.*;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void showResult(ArrayList<User> userMap)
	{
		Intent intent = new Intent();
		// Pass information to the next screen
		intent.setClass(getApplicationContext(), ResultActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("userMap", userMap);

		intent.putExtras(bundle);

		// Redirect to next screen
		startActivity(intent);
	}
	
	public class CounterClass extends CountDownTimer 
	{ 
		public CounterClass(long millisInFuture, long countDownInterval) 
		{ 
			super(millisInFuture, countDownInterval); 
		} 
		
		@Override public void onFinish() 
		{ 
			textViewTime.setText("Completed."); 
		} 
		@SuppressLint("NewApi") 
		@TargetApi(Build.VERSION_CODES.GINGERBREAD) 
		@Override public void onTick(long millisUntilFinished) 
		{ 
			long millis = millisUntilFinished; String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))); 
			System.out.println(hms); 
			textViewTime.setText(hms); 
		} 
	}
}