package appathon.history;

import java.util.ArrayList;
import java.util.HashMap;
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
import appathon.history.models.GameManager;
import appathon.history.models.Question;
import appathon.history.models.User;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity
{
	GoogleMap worldMap;
	TextView questionView;
	TextView timerView;
	LocationGetter lg;
	Context context;
	GameManager manager;

	HashMap<String, Marker> marker_map; // Store countries and marker pair, it is used for Game
	HashMap<Marker, HashMap<String, String>> marker_text_map = new HashMap<Marker, HashMap<String, String>>(); //Store the question/answer pair for each country

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		worldMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		Bundle bundle = this.getIntent().getExtras();
		lg = new LocationGetter();
		context = this.getApplicationContext();
		//		if (bundle == null)
		//		{
		//			worldMap.setOnMapClickListener(new clickMapWhilePlayingListener());
		//		}

		questionView = (TextView) this.findViewById(R.id.question_view);
		timerView = (TextView) this.findViewById(R.id.timer_view);
		marker_map = new HashMap<String, Marker>();
		marker_text_map = new HashMap<Marker, HashMap<String, String>>(); //Store the question/answer pair for each country
		manager = new GameManager();
		CounterClass counter = new CounterClass(10000, 1000);
		counter.start();
	}

	public class clickMapWhilePlayingListener implements OnMapClickListener
	{

		@Override
		public void onMapClick(LatLng arg0)
		{
			// TODO Auto-generated method stub
			questionView.setText(lg.getCountryName(context, arg0.latitude,
					arg0.longitude));
			drawMarker(arg0, R.drawable.avatar_gao_chao_small);
		}
	}


	public void moveCameraToCountry(String country) {
		LatLng ll = lg.getLocationFromAddress(context, country);
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(ll)      // Sets the center of the map to country
		.zoom(17)                   // Sets the zoom
		.build();                   // Creates a CameraPosition from the builder
		worldMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	public void displayAllAnswers(ArrayList<Question> q_list) {
		if(marker_map == null) {
			marker_map = new HashMap<String, Marker>();
		}
		if(marker_text_map == null) {
			marker_text_map = new HashMap<Marker, HashMap<String, String>>();
		}
		for(Question q : q_list) {
			if(q.country != null) {
				LatLng ll = lg.getLocationFromAddress(context, q.country.answer);
				if(!marker_map.containsKey(q.country.answer)) {
					Marker marker = drawMarker(ll);
					marker_map.put(q.country.answer, marker);
					HashMap<String, String> textMap = new HashMap<String, String>();
					textMap.put(q.question, q.country.answer);
					marker_text_map.put(marker, textMap);
				} else {
					Marker marker = marker_map.get(q.country.answer);
					marker_text_map.get(marker).put(q.question, q.country.answer);
				}
			}	
		}
		for (Marker marker: marker_text_map.keySet()) {
			marker.setSnippet(generateQASnipper(marker_text_map.get(marker)));
		}
	}

	private String generateQASnipper(HashMap<String, String> map) {
		StringBuffer sb = new StringBuffer();
		for(String question: map.keySet()) {
			sb.append("Q:");
			sb.append(question);
			sb.append("\n");
			sb.append("A:");
			sb.append(map.get(question));
			sb.append("\n");
		}
		return sb.toString();
	}


	public void removeMarker(String countryName) {
		if(marker_map.containsKey(countryName)) {
			Marker marker = marker_map.remove(countryName);
			marker.remove();
		}
	}

	public Marker drawMarker(String countryName) {
		LatLng ll = lg.getLocationFromAddress(context, countryName);
		return drawMarker(ll);
	}

	public Marker drawMarker(LatLng ll, int avatar_id) {
		return worldMap.addMarker(new MarkerOptions()
		.position(ll)
		.icon(BitmapDescriptorFactory.fromResource(avatar_id)));
	}

	public Marker drawMarker(LatLng ll){
		return worldMap.addMarker(new MarkerOptions()
		.position(ll)
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	}

	public Marker drawMarker(LatLng ll, User user) {
		return worldMap.addMarker(new MarkerOptions()
		.position(ll)
		.icon(BitmapDescriptorFactory.fromResource(user.getSmallAvatar())));
	}

	public void drawMarkers(String[] countryNames) {
		for(String cn: countryNames) {
			LatLng ll = lg.getLocationFromAddress(context, cn);
			drawMarker(ll);
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

		@Override
		public void onFinish()
		{
			timerView.setText("Completed.");
		}

		@SuppressLint("NewApi")
		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		@Override
		public void onTick(long millisUntilFinished)
		{
			long millis = millisUntilFinished;
			String hms = String.format(
					"%02d:%02d:%02d",
					TimeUnit.MILLISECONDS.toHours(millis),
					TimeUnit.MILLISECONDS.toMinutes(millis)
					- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
							.toHours(millis)),
							TimeUnit.MILLISECONDS.toSeconds(millis)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes(millis)));
			System.out.println(hms);
			timerView.setText(hms);

			if(manager.checkAnswers()){
				Question q = manager.NextQuestion();
				this.cancel();
				this.start();
			}
		}
	}
}