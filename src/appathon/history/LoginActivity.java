package appathon.history;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

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
			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data)
		{
			Log.d("HelloFacebook", "Success!");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//for facebook
		uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
		// hide action bar
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();

		setContentView(R.layout.activity_login);

		// set title view
		ImageView appTitleImageView = (ImageView) findViewById(R.id.app_title);
		appTitleImageView.setImageResource(R.drawable.app_title);

		// hide zoom control
		GoogleMap mapForLogin = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.mapForLogin)).getMap();
		mapForLogin.getUiSettings().setZoomControlsEnabled(false);
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
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception)
	{
		startActivity(new Intent(this, MainActivity.class));
	}
}
