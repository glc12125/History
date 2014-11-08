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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
		pickFriends();
	}

	private void pickFriends()
	{
		final FriendPickerFragment fragment = new FriendPickerFragment();

		setFriendPickerListeners(fragment);

		showPickerFragment(fragment);
	}

	private void showPickerFragment(FriendPickerFragment fragment)
	{
		fragment.setOnErrorListener(new PickerFragment.OnErrorListener()
		{
			@Override
			public void onError(PickerFragment<?> pickerFragment,
					FacebookException error)
			{
				String text = "exception" + error.getMessage();
				Toast toast = Toast.makeText(LoginActivity.this, text,
						Toast.LENGTH_SHORT);
				toast.show();
			}
		});

		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction().replace(R.id.fragment_container, fragment)
				.addToBackStack(null).commit();
		fm.executePendingTransactions();

		fragment.loadData(true);
	}

	private void setFriendPickerListeners(final FriendPickerFragment fragment)
	{
		fragment.setOnDoneButtonClickedListener(new FriendPickerFragment.OnDoneButtonClickedListener()
		{
			@Override
			public void onDoneButtonClicked(PickerFragment<?> pickerFragment)
			{
				onFriendPickerDone(fragment);
			}
		});
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
