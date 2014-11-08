package appathon.history;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
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
	public static final String userName = "Player-Chosen Name";
	GoogleMap worldMap;
	public LocationGetter lg;
	Context context;
	public GameManager manager;
	PopupWindow mPopupWindowForQuestion;
	PopupWindow mPopupWindowForRanking;
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
				Question question = manager.getCurrentQuestion();
				if(question == null){
					question = manager.getCurrentQuestion();
				}
				mAct.showQuestion(question);
				break;
			case MsgHandler.MSG_TYPE_DRAW_MARKER:
				b = msg.getData();
				int avatar = b.getInt("avatar");
				int defense = b.getInt("defense");
				countryName = b.getString("countryName");
				Country country = new Country(countryName);
				drawMarker(country, avatar, defense);
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

		context = this.getApplicationContext();

		backgroundMusicPlayer = MediaPlayer
				.create(this, R.raw.background_audio);
		// backgroundMusicPlayer.setLooping(true); // Set looping
		// backgroundMusicPlayer.setVolume(100, 100);
		// backgroundMusicPlayer.start();

		worldMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		// for customized info window
		worldMap.setInfoWindowAdapter(new InfoWindowAdapter()
		{
			@Override
			public View getInfoWindow(Marker arg0)
			{
				return null;
			}

			@Override
			public View getInfoContents(Marker marker)
			{
				View infoWindowView = getLayoutInflater().inflate(
						R.layout.activity_main_marker_info_window, null);
				TextView QATextView = ((TextView) infoWindowView
						.findViewById(R.id.QAText));
				QATextView.setText(marker.getSnippet());
				return infoWindowView;
			}
		});

		Bundle bundle = this.getIntent().getExtras();
		lg = LocationGetter.getLocationGetter(context);
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

		manager.startCountDown();
	}

	public void moveCameraToCountry(Country country, float zoomLevel)
	{
		LatLng ll = lg.getLocationFromAddress(country.getName(), 7.5);
		// Creates a CameraPosition from the builder
		CameraPosition cameraPosition = new CameraPosition.Builder().target(ll)
				.zoom(zoomLevel).build();
		// Move camera to specific position
		worldMap.moveCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
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
				LatLng ll = lg.getLocationFromAddress(country.getName());
				if (!marker_text_map.containsKey(country))
				{
					Marker marker = marker_map.get(country);
					if (marker == null)
					{
						marker = drawMarker(ll);
						marker_map.put(country, marker);
					}
					marker.setTitle(country.getName());
					HashMap<String, String> textMap = new HashMap<String, String>();
					textMap.put(q.question, q.correctAnswer);
					marker_text_map.put(marker, textMap);
				} else
				{
					Marker marker = marker_map.get(country);
					marker.setTitle(country.getName());
					marker_text_map.get(marker)
							.put(q.question, q.correctAnswer);
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
			sb.append("Q: ");
			sb.append(question);
			sb.append("\n");
			sb.append("A: ");
			sb.append(map.get(question));
			sb.append("\n\n");
		}
		return sb.toString();
	}

	private void removeMarker(Country country)
	{
		if (marker_map.containsKey(country))
		{
			Marker marker = marker_map.remove(country);
			marker.remove();
		}
	}

	private Marker drawMarker(Country country)
	{
		LatLng ll = lg.getLocationFromAddress(country.getName());
		return drawMarker(ll);
	}

	public Marker drawMarker(Country country, int avatar_id, int defense)
	{
		LatLng ll = country.getLatLng();
		if (worldMap == null)
		{
			Log.e("NullPointerException", "World Map is null");
		}

		Marker temp_marker;
		if (marker_map.containsKey(country))
		{
			marker_map.get(country).setIcon(
					BitmapDescriptorFactory
							.fromBitmap(generateCustomizedMarkerBitmap(
									avatar_id, defense)));
			temp_marker = marker_map.get(country);
		} else
		{
			MarkerOptions marker = new MarkerOptions().position(ll).icon(
					BitmapDescriptorFactory
							.fromBitmap(generateCustomizedMarkerBitmap(
									avatar_id, defense)));
			temp_marker = worldMap.addMarker(marker);
			marker_map.put(country, temp_marker);
		}

		return temp_marker;
	}

	private Bitmap generateCustomizedMarkerBitmap(int avatar_id, int defense)
	{
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bmp = Bitmap.createBitmap(110, 130, conf);
		Canvas canvas = new Canvas(bmp);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawBitmap(
				BitmapFactory.decodeResource(getResources(), avatar_id), 0, 30,
				paint);

		switch (avatar_id)
		{
		// case R.drawable.avatar_chao_gao_small:
		// paint.setARGB(128, 0, 0, 255);
		// break;
		// case R.drawable.avatar_liang_chuan_small:
		// paint.setARGB(128, 0, 255, 0);
		// break;
		// case R.drawable.avatar_meng_zhang_small:
		// paint.setARGB(128, 255, 0, 0);
		// break;
		// case R.drawable.avatar_yi_mai_small:
		// paint.setARGB(128, 255, 255, 51);
		// break;

		default:
			paint.setARGB(128, 0, 0, 255);
			break;
		}

		paint.setStyle(Paint.Style.FILL);

		canvas.drawCircle(80, 30, 20, paint);

		Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);
		paint.setTypeface(tf);
		paint.setTextSize(40);
		paint.setColor(Color.WHITE);
		canvas.drawText("" + defense, 70, 45, paint);

		return bmp;
	}

	public Marker drawMarker(LatLng ll)
	{
		return worldMap.addMarker(new MarkerOptions().position(ll).icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	}

	public void showRanking(View v)
	{
		manager.stopCountDown();
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

	/**
	 * Move camera to specific area show question on mPopupWindowForQuestion Add
	 * event listener for mPopupWindowForQuestion
	 * 
	 * @param question
	 */
	private void showQuestion(final Question question)
	{
		if (question.kind == "ChooseCountry")
			moveCameraToCountry(question.correspondingCountry, 1.5f);
		else
			moveCameraToCountry(question.correspondingCountry, 3.5f);

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
