package appathon.history;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.LoginButton;
import com.facebook.widget.PickerFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class LoginActivity extends FragmentActivity
{
	public static final int REQUEST_CODE_PICKER = 1;

	private UiLifecycleHelper uiHelper;

	private List<GraphUser> selectedUsers;

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
		loginButton.setReadPermissions("user_friends");
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
		
		if (requestCode != REQUEST_CODE_PICKER)
		{
			uiHelper.onActivityResult(requestCode, resultCode, data,
					dialogCallback);

			startActivityForResult(new Intent(this, PickerActivity.class),
					REQUEST_CODE_PICKER);
		} else
		{

		}
	}

	private void onFriendPickerDone(FriendPickerFragment fragment)
	{
		FragmentManager fm = getSupportFragmentManager();
		fm.popBackStack();

		String results = "";

		List<GraphUser> selection = fragment.getSelection();

		if (selection != null && selection.size() > 0)
		{
			ArrayList<String> names = new ArrayList<String>();
			for (GraphUser user : selection)
			{
				names.add(user.getName());
			}
			results = TextUtils.join(", ", names);
		} else
		{
			results = "no_friends_selected";
		}

		showAlert("you_picked", results);

		startActivity(new Intent(this, MainActivity.class));
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
