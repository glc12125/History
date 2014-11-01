package appathon.history;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import appathon.history.models.User;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity
{


	GoogleMap worldMap;
	TextView questionView;
	TextView timerView;
	LocationGetter lg;
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		worldMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		questionView = (TextView) this.findViewById(R.id.question_view);
		timerView = (TextView) this.findViewById(R.id.timer_view);
		worldMap.setOnMapClickListener(new clickMapWhilePlayingListener());
		lg = new LocationGetter();
		context = this.getApplicationContext();
		questionView.setText("hehe");
	}

	public class clickMapWhilePlayingListener implements OnMapClickListener {

		@Override
		public void onMapClick(LatLng arg0) {
			// TODO Auto-generated method stub
			questionView.setText(lg.getCountryName(context, arg0.latitude, arg0.longitude));
		}
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
			timerView.setText("Completed."); 
		} 
		@SuppressLint("NewApi") 
		@TargetApi(Build.VERSION_CODES.GINGERBREAD) 
		@Override public void onTick(long millisUntilFinished) 
		{ 
			long millis = millisUntilFinished; String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))); 
			System.out.println(hms); 
			timerView.setText(hms); 
		} 
	}
}