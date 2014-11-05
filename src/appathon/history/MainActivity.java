package appathon.history;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import appathon.history.models.Country;
import appathon.history.models.GameManager;
import appathon.history.models.User;
import appathon.history.models.qa.Answer;
import appathon.history.models.qa.Question;
import appathon.util.LocationGetter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity
{
	private MsgHandler mHandler = null;
	private int millisRoundTime = 5000;
	public static final String userName = "Yimai Fang";
	GoogleMap worldMap;
	public LocationGetter lg;
	Context context;
	public GameManager manager;
	PopupWindow mPopupWindowForQuestion;
	PopupWindow mPopupWindowForRanking;
	CounterClass counter;
	HashMap<Country, Marker> marker_map; 
	// Store countries and marker pair, it is used for Game
	HashMap<Marker, HashMap<String, String>> marker_text_map = new HashMap<Marker, HashMap<String, String>>(); // Store
	// The question/answer pair for each marker
	MediaPlayer backgroundMusicPlayer; 


	public class MsgHandler extends Handler
	{
		WeakReference<MainActivity> mActivity;
		public static final int MSG_TYPE_SHOW_QUESTION = 1;
		public static final int MSG_TYPE_DRAW_MARKER = 2;
		public static final int MSG_TYPE_REMOVE_MARKER = 3;

		MsgHandler(MainActivity aActivity)
		{
			mActivity = new WeakReference<MainActivity>(aActivity);
		}

		@Override
		public void handleMessage(Message msg)
		{
			MainActivity mAct = mActivity.get();
			Bundle b = null;
			String countryName = null;

			switch (msg.what)
			{
			case MsgHandler.MSG_TYPE_SHOW_QUESTION:
				b = msg.getData();
				Question question = (Question)b.getSerializable("question");
				//mAct.showQuestion(manager.getCurrentQuestion());
				mAct.showQuestion(question);
				break;
			case MsgHandler.MSG_TYPE_DRAW_MARKER:
				b = msg.getData();
				int avatar = b.getInt("avatar");
				countryName = b.getString("countryName");
				drawMarker(lg.getLocationFromAddress(context, countryName),
						avatar);
				break;
			case MsgHandler.MSG_TYPE_REMOVE_MARKER:
				b = msg.getData();
				countryName = b.getString("countryName");
				Country c = new Country(countryName);
				removeMarker(c);
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();

		setContentView(R.layout.activity_main); // be sure you call this AFTER
		// requestFeature
		backgroundMusicPlayer = MediaPlayer.create(this, R.raw.background_audio);
		backgroundMusicPlayer.setLooping(true); // Set looping
		backgroundMusicPlayer.setVolume(100, 100);
		backgroundMusicPlayer.start();
		
		worldMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		Bundle bundle = this.getIntent().getExtras();
		lg = new LocationGetter();
		context = this.getApplicationContext();
		// Store the country to marker map
		marker_map = new HashMap<Country, Marker>();
		// Store the question/answer pair for each country
		marker_text_map = new HashMap<Marker, HashMap<String, String>>(); 
		// Set popup for question
		View popupViewForQuestion = getLayoutInflater().inflate(
				R.layout.activity_main_popup_question, null);
		mPopupWindowForQuestion = new PopupWindow(popupViewForQuestion,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWindowForQuestion.setTouchable(true);
		mPopupWindowForQuestion.setOutsideTouchable(false);

		mHandler = new MsgHandler(this);
		manager = new GameManager(context, mHandler);
		// pup up 500ms later
		mHandler.sendEmptyMessageDelayed(MsgHandler.MSG_TYPE_SHOW_QUESTION, 500);
		// set popup for ranking
		View popupViewForRanking = getLayoutInflater().inflate(
				R.layout.activity_main_popup_ranking, null);
		mPopupWindowForRanking = new PopupWindow(popupViewForRanking,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWindowForRanking.setTouchable(true);
		mPopupWindowForRanking.setOutsideTouchable(false);

		counter = new CounterClass(millisRoundTime, 1000);
		counter.start();
	}

	public void moveCameraToCountry(Country country, float zoomLevel)
	{
		LatLng ll = lg.getLocationFromAddress(context, country.getName(), 7.5);
		// Creates a CameraPosition from the builder
		CameraPosition cameraPosition = new CameraPosition.Builder().target(ll).zoom(zoomLevel).build(); 
		// Move camera to specific position
		worldMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	public void displayAllAnswers(ArrayList<Question> q_list)
	{
		if (marker_map == null)
		{
			marker_map = new HashMap<Country, Marker>();
		}
		if (marker_text_map == null)
		{
			marker_text_map = new HashMap<Marker, HashMap<String, String>>();
		}
		for (Question q : q_list)
		{
			if (q.correspondingCountry != null)
			{
				Country country = q.correspondingCountry;
				LatLng ll = lg.getLocationFromAddress(context, country.getName());
				if (!marker_map.containsKey(country))
				{
					Marker marker = drawMarker(ll);
					marker.setTitle(country.getName());
					marker_map.put(country, marker);
					HashMap<String, String> textMap = new HashMap<String, String>();
					textMap.put(q.question, country.getName());
					marker_text_map.put(marker, textMap);
				} else
				{
					Marker marker = marker_map.get(country);
					marker_text_map.get(marker).put(q.question, country.getName());
				}
			}
		}
		for (Marker marker : marker_text_map.keySet())
		{
			marker.setSnippet(generateQASnipper(marker_text_map.get(marker)));
		}

		worldMap.setOnMarkerClickListener(new OnMarkerClickListener()
		{
			@Override
			public boolean onMarkerClick(Marker arg0)
			{
				arg0.showInfoWindow();
				return true;
			}
		});
	}

	private String generateQASnipper(HashMap<String, String> map)
	{
		StringBuffer sb = new StringBuffer();
		for (String question : map.keySet())
		{
			sb.append("Q:");
			sb.append(question);
			sb.append("\n");
			sb.append("A:");
			sb.append(map.get(question));
			sb.append("\n");
		}
		return sb.toString();
	}

	public void removeMarker(Country country)
	{
		if (marker_map.containsKey(country))
		{
			Marker marker = marker_map.remove(country);
			marker.remove();
		}
	}

	public Marker drawMarker(Country country)
	{
		LatLng ll = lg.getLocationFromAddress(context, country.getName());
		return drawMarker(ll);
	}

	public Marker drawMarker(LatLng ll, int avatar_id)
	{
		if (worldMap == null)
		{
			Log.e("aaaaaaaaaa", "world map is null");
		}
		MarkerOptions marker = new MarkerOptions().position(ll).icon(
				BitmapDescriptorFactory.fromResource(avatar_id));

		if (marker == null)
		{
			Log.e("aaaaaaaaaa", "marker is null");
		}

		Marker temp_marker = worldMap.addMarker(marker);
		CameraPosition cameraPosition = new CameraPosition.Builder().target(ll)
				.build();
		worldMap.moveCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));

		return temp_marker;
	}

	public Marker drawMarker(LatLng ll)
	{
		return worldMap.addMarker(new MarkerOptions().position(ll).icon(
				BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	}

	public void showRanking(View v)
	{
		counter.cancel();
		backgroundMusicPlayer.stop();
		mPopupWindowForQuestion.dismiss();
		v.setVisibility(View.GONE);

		manager.rankUsers();
		ArrayList<User> users = manager.getUsers();
		SimpleAdapter adapter = new SimpleAdapter(this,
				convertUsersToMap(users),
				R.layout.activity_main_popup_ranking_list_item, new String[] {
			"ranking", "avatar", "name", "score" }, new int[] {
			R.id.ranking, R.id.avatar, R.id.name, R.id.score });

		mPopupWindowForRanking.showAtLocation(findViewById(R.id.map),
				Gravity.TOP, 0, 0);

		ListView rankingListView = ((ListView) mPopupWindowForRanking
				.getContentView().findViewById(R.id.rankingListView));

		rankingListView.setAdapter(adapter);
	}

	private List<Map<String, Object>> convertUsersToMap(ArrayList<User> users)
	{
		List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < users.size(); i++)
		{
			User user = users.get(i);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ranking", user.getRanking());
			map.put("avatar", user.getSmallAvatar());
			map.put("name", user.getName());
			map.put("score", user.getScore());
			userList.add(map);
		}

		return userList;
	}

	public void displayAllAnswers(View v)
	{
		mPopupWindowForQuestion.dismiss();
		mPopupWindowForRanking.dismiss();
		displayAllAnswers(manager.getQuestions());
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
			manager.checkAnswers(millisRoundTime);
			manager.getToNextRound();
			showQuestion(manager.getCurrentQuestion());
			this.cancel();
			this.start();
		}

		@SuppressLint("NewApi")
		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		@Override
		public void onTick(long millisUntilFinished)
		{
//			long millis = millisUntilFinished;
//			String hms = String.format(
//					"%02d:%02d:%02d",
//					TimeUnit.MILLISECONDS.toHours(millis),
//					TimeUnit.MILLISECONDS.toMinutes(millis)
//					- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
//							.toHours(millis)),
//							TimeUnit.MILLISECONDS.toSeconds(millis)
//							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
//									.toMinutes(millis)));
//			System.out.println(hms);
			long millisPassed = millisRoundTime - millisUntilFinished;
			if (manager.checkAnswers(millisPassed))
			{
				manager.getToNextRound();
				showQuestion(manager.getCurrentQuestion());
				this.cancel();
				this.start();
			}
		}
	}

	/**
	 * Move camera to specific area
	 * show question on mPopupWindowForQuestion
	 * Add event listener for mPopupWindowForQuestion
	 * @param question
	 */
	private void showQuestion(final Question question)
	{
		if (question.kind == "ChooseCountry")
			moveCameraToCountry(question.correspondingCountry,
					1.5f);
		else
			moveCameraToCountry(question.correspondingCountry,
					3.5f);

		// try
		// {
		// Thread.sleep(1000);
		// } catch (InterruptedException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		if (mPopupWindowForQuestion.isShowing())
		{
			mPopupWindowForQuestion.dismiss();
		}

		mPopupWindowForQuestion.showAtLocation(findViewById(R.id.map),
				Gravity.TOP, 0, 0);

		TextView questionTextView = ((TextView) mPopupWindowForQuestion
				.getContentView().findViewById(R.id.questionTextView));
		questionTextView.setText(question.question);

		ListView answerListView = ((ListView) mPopupWindowForQuestion
				.getContentView().findViewById(R.id.answerListView));

		answerListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				ListView answerListView = (ListView) parent;

				HashMap<String, String> map = (HashMap<String, String>) answerListView
						.getItemAtPosition(position);
				String yourAnswer = map.get("option_string");

				manager.updateUser(userName, yourAnswer);

				mPopupWindowForQuestion.dismiss();
			}
		});

		SimpleAdapter adapter = new SimpleAdapter(this,
				convertOptionsToMap(question.options),
				R.layout.activity_main_popup_question_list_item,
				new String[] { "option_string" },
				new int[] { R.id.option_string });

		answerListView.setAdapter(adapter);
	}

	private List<Map<String, Object>> convertOptionsToMap(
			ArrayList<Answer> options)
			{
		List<Map<String, Object>> optionList = new ArrayList<Map<String, Object>>();

		for (Answer option : options)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("option_string", option.answer);
			optionList.add(map);
		}

		return optionList;
			}
}
