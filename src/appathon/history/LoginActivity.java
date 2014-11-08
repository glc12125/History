package appathon.history;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import appathon.history.PickerActivity;
import appathon.history.R;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class LoginActivity extends FragmentActivity
{
	private UiLifecycleHelper uiHelper;

	private GraphUser user;

	private Session.StatusCallback callback = new Session.StatusCallback()
	{
		@Override
		public void call(Session session, SessionState state,
				Exception exception)
		{
			onSessionStateChange(session, state, exception);
		}
	};

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback()
	{
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data)
		{
			Toast.makeText(getApplicationContext(),
					String.format("Error: %s", error.toString()),
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data)
		{
			Toast.makeText(getApplicationContext(), "Success!",
					Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// for facebook
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// set title view
		ImageView appTitleImageView = (ImageView) findViewById(R.id.app_title);
		appTitleImageView.setImageResource(R.drawable.app_title);

		// hide zoom control
		GoogleMap mapForLogin = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.mapForLogin)).getMap();
		mapForLogin.getUiSettings().setZoomControlsEnabled(false);

		// for facebook
		showHashKey(this);

		LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton.setReadPermissions(Arrays.asList("public_profile",
				"user_friends"));
		loginButton
				.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback()
				{
					@Override
					public void onUserInfoFetched(GraphUser graphUser)
					{
						LoginActivity.this.user = graphUser;
						saveUserInfo();
					}
				});

	}

	public static void showHashKey(Context context)
	{
		try
		{
			PackageInfo info = context.getPackageManager().getPackageInfo(
					"appathon.history", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures)
			{
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.i("KeyHash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e)
		{
		} catch (NoSuchAlgorithmException e)
		{
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);

		startActivity(new Intent(this, PickerActivity.class));
		finish();
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

	private void saveUserInfo()
	{
		// JSONObject jsonUser = this.user.getInnerJSONObject();
		//
		// String user_avatar_uri_string = null;
		// try
		// {
		// user_avatar_uri_string = jsonUser.getJSONObject("picture")
		// .getJSONObject("data").getString("url");
		// } catch (JSONException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		SharedPreferences facebook_userinfo = getSharedPreferences(
				"facebook_userinfo", 0);
		SharedPreferences.Editor facebook_userinfo_editor = facebook_userinfo
				.edit();

		// facebook_userinfo_editor.putString("name", this.user.getName());
		// facebook_userinfo_editor.putString("user_avatar_uri_string",
		// user_avatar_uri_string);
		facebook_userinfo_editor.putString("name", "Chao");
		facebook_userinfo_editor
				.putString(
						"user_avatar_uri_string",
						"https://scontent-b-lhr.xx.fbcdn.net/hphotos-xpa1/v/t1.0-9/1378093_649030461803545_1085505525_n.jpg?oh=68ab2ee46369245fa5c17e6844bdbde5&oe=54DD4C1D");
		facebook_userinfo_editor.commit();
	}
}
