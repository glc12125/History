package appathon.history;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import appathon.animation.MarkerAnimationCountDownTimer;
import appathon.history.models.Country;
import appathon.history.models.GameManager;
import appathon.history.models.User;
import appathon.history.models.qa.Answer;
import appathon.history.models.qa.Question;
import appathon.util.LocationGetter;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
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
	private UiLifecycleHelper uiHelper;

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
		public static final int MSG_TYPE_SHOW_RANKING = 4;
		public static final int MSG_TYPE_SAVE_SCREEN = 5;
		public static final int MSG_TYPE_SHARE_RANKING = 6;

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
				if (question == null)
				{
					question = manager.getCurrentQuestion();
				}
				mAct.showQuestion(question);
				break;
			case MsgHandler.MSG_TYPE_DRAW_MARKER:
				b = msg.getData();
				int defense = b.getInt("defense");
				int userId = b.getInt("userId");
				int avatar = b.getInt("avatar");
				countryName = b.getString("countryName");
				Country country = new Country(countryName);
				drawMarker(country, userId, defense, avatar);
				blinkMarker(marker_map.get(country));
				break;
			case MsgHandler.MSG_TYPE_REMOVE_MARKER:
				b = msg.getData();
				countryName = b.getString("countryName");
				Country c = new Country(countryName);
				removeMarker(c);
				break;
			case MsgHandler.MSG_TYPE_SHOW_RANKING:
				showRanking();
				break;
			case MsgHandler.MSG_TYPE_SAVE_SCREEN:
				saveScreen();
				break;
			case MsgHandler.MSG_TYPE_SHARE_RANKING:
				shareRankingToFacebook();
				break;
			default:
				break;
			}
		}
	}

	private Session.StatusCallback callback = new Session.StatusCallback()
	{
		@Override
		public void call(Session session, SessionState state,
				Exception exception)
		{
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// for facebook
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		// get facebook friends
		@SuppressWarnings("unchecked")
		HashMap<String, URI> facebook_friends_username_imageuri = new HashMap<String, URI>(
				(Map<String, URI>) getIntent().getExtras().get(
						"facebook_friends_username_imageuri"));

		// get facebook user
		SharedPreferences facebook_userinfo = getSharedPreferences(
				"facebook_userinfo", 0);

		String userName = facebook_userinfo.getString("name", null);
		URI user_avatar_uri = null;
		try
		{
			user_avatar_uri = new URI(facebook_userinfo.getString(
					"user_avatar_uri_string", null));
		} catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setContentView(R.layout.activity_main);

		context = this.getApplicationContext();

		backgroundMusicPlayer = MediaPlayer
				.create(this, R.raw.background_audio);
		// backgroundMusicPlayer.setLooping(true); // Set looping
		// backgroundMusicPlayer.setVolume(100, 100);
		// backgroundMusicPlayer.start();

		worldMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		// hide zoom
		worldMap.getUiSettings().setZoomControlsEnabled(false);

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
		manager = new GameManager(context, mHandler, userName, user_avatar_uri,
				facebook_friends_username_imageuri);
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

	public Marker drawMarker(Country country, int userId, int defense,
			int avatar)
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
							.fromBitmap(generateCustomizedMarkerBitmap(userId,
									avatar, defense)));
			temp_marker = marker_map.get(country);
		} else
		{
			MarkerOptions marker = new MarkerOptions().position(ll).icon(
					BitmapDescriptorFactory
							.fromBitmap(generateCustomizedMarkerBitmap(userId,
									avatar, defense)));
			temp_marker = worldMap.addMarker(marker);
			marker_map.put(country, temp_marker);
		}

		return temp_marker;
	}

	private Bitmap generateCustomizedMarkerBitmap(int userId, int avatar,
			int defense)
	{
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bmp = Bitmap.createBitmap(110, 130, conf);
		Canvas canvas = new Canvas(bmp);

		Paint paint = new Paint();
		paint.setAntiAlias(true);

		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), avatar),
				0, 30, paint);

		switch (userId)
		{
		case 1:
			paint.setARGB(128, 255, 255, 51);
			break;
		case 2:
			paint.setARGB(128, 0, 255, 0);
			break;
		case 3:
			paint.setARGB(128, 255, 0, 0);
			break;
		case 4:
			paint.setARGB(128, 0, 0, 255);
			break;

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

	public void showRanking()
	{
		manager.stopCountDown();
		backgroundMusicPlayer.stop();
		mPopupWindowForQuestion.dismiss();

		manager.rankUsers();
		ArrayList<User> users = manager.getUsers();
		SimpleAdapter adapter = new SimpleAdapter(this,
				convertUsersToMap(users),
				R.layout.activity_main_popup_ranking_list_item, new String[] {
						"ranking", "avatar", "name", "score", "flag1", "flag2",
						"flag3", "flag4", "flag5", "flag6", "flag7", "flag8",
						"flag9", "flag10", "flag11", "flag12", "flag13",
						"flag14" }, new int[] { R.id.ranking, R.id.avatar,
						R.id.name, R.id.score, R.id.flag1, R.id.flag2,
						R.id.flag3, R.id.flag4, R.id.flag5, R.id.flag6,
						R.id.flag7, R.id.flag8, R.id.flag9, R.id.flag10,
						R.id.flag11, R.id.flag12, R.id.flag13, R.id.flag14 });

		mPopupWindowForRanking.showAtLocation(findViewById(R.id.map),
				Gravity.TOP, 0, 0);

		TextView rankingTextView = ((TextView) mPopupWindowForRanking
				.getContentView().findViewById(R.id.rankingText));

		int rankNo = manager.getRankNoOfPlayer();
		rankingTextView.setText("You're No." + rankNo + " among your friends!");

		ListView rankingListView = ((ListView) mPopupWindowForRanking
				.getContentView().findViewById(R.id.rankingListView));

		rankingListView.setAdapter(adapter);

		// hide zoom
		worldMap.getUiSettings().setZoomControlsEnabled(false);

		mHandler.sendEmptyMessageDelayed(MsgHandler.MSG_TYPE_SAVE_SCREEN, 3000);
	}

	public void test(View v)
	{
		mHandler.sendEmptyMessageDelayed(MsgHandler.MSG_TYPE_SHARE_RANKING, 0);
	}

	private void shareRankingToFacebook()
	{
		String filePath = Environment.getExternalStorageDirectory()
				+ File.separator + "img.png";
		ArrayList<File> photoFiles = new ArrayList<File>();
		photoFiles.add(new File(filePath));

		if (FacebookDialog.canPresentShareDialog(this,
				FacebookDialog.ShareDialogFeature.PHOTOS))
		{
			FacebookDialog shareDialog = new FacebookDialog.PhotoShareDialogBuilder(
					this).addPhotoFiles(photoFiles).build();
			uiHelper.trackPendingDialogCall(shareDialog.present());
		}
	}

	private void saveScreen()
	{
		Process sh;
		try
		{
			sh = Runtime.getRuntime().exec("su", null, null);

			OutputStream os = sh.getOutputStream();
			os.write(("/system/bin/screencap -p " + "/sdcard/img.png")
					.getBytes("ASCII"));
			os.flush();

			os.close();
			
			sh.waitFor();

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
                + File.separator + "/sdcard/img.png", options);
		Bitmap resizedbitmap1=Bitmap.createBitmap(bmp, 0,0, bmp.getWidth(), bmp.getHeight() * 2 / 3);
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		resizedbitmap1.compress(Bitmap.CompressFormat.PNG, 80, bytes);
		File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + "/sdcard/img.png");
		FileOutputStream fo = null;
		try {
			f.createNewFile();
			fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
			fo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
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
			int numCountries = user.getNumOfCountries();
			for (int cp = 0; cp < 14; cp++)
			{
				if (cp < numCountries)
					map.put("flag" + (cp + 1), user.getCountries().get(cp)
							.getFlag());
				else
					map.put("flag" + (cp + 1), null);
			}
			userList.add(map);
		}

		return userList;
	}

	public void displayAllAnswers(View v)
	{
		// show zoom
		worldMap.getUiSettings().setZoomControlsEnabled(true);

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

				manager.updateUser(yourAnswer);

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

	private void blinkMarker(Marker m)
	{
		MarkerAnimationCountDownTimer macdt = new MarkerAnimationCountDownTimer(
				1000, 100, m, 4);
		macdt.start();
	}

	private void showAlert(String title, String message)
	{
		new AlertDialog.Builder(this).setTitle(title).setMessage(message)
				.setPositiveButton("ok", null).show();
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception)
	{
	}
}
