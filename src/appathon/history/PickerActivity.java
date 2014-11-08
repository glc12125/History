/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package appathon.history;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PlacePickerFragment;

/**
 * The PickerActivity enhances the Friend or Place Picker by adding a title and
 * a Done button. The selection results are saved in the ScrumptiousApplication
 * instance.
 */
public class PickerActivity extends FragmentActivity
{
	public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
	public static final Uri PLACE_PICKER = Uri.parse("picker://place");

	private static final int SEARCH_RADIUS_METERS = 1000;
	private static final int SEARCH_RESULT_LIMIT = 50;
	private static final String SEARCH_TEXT = "Restaurant";
	private static final int LOCATION_CHANGE_THRESHOLD = 50; // meters

	private static final Location SAN_FRANCISCO_LOCATION = new Location("")
	{
		{
			setLatitude(37.7750);
			setLongitude(-122.4183);
		}
	};

	private FriendPickerFragment friendPickerFragment;
	private PlacePickerFragment placePickerFragment;
	private LocationListener locationListener;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pickers);

		Bundle args = getIntent().getExtras();
		FragmentManager manager = getSupportFragmentManager();
		Fragment fragmentToShow = null;

		if (savedInstanceState == null)
		{
			friendPickerFragment = new FriendPickerFragment(args);
			friendPickerFragment
					.setFriendPickerType(FriendPickerFragment.FriendPickerType.TAGGABLE_FRIENDS);
		} else
		{
			friendPickerFragment = (FriendPickerFragment) manager
					.findFragmentById(R.id.picker_fragment);
			;
		}

		friendPickerFragment
				.setOnErrorListener(new PickerFragment.OnErrorListener()
				{
					@Override
					public void onError(PickerFragment<?> fragment,
							FacebookException error)
					{
						PickerActivity.this.onError(error);
					}
				});

		friendPickerFragment
				.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener()
				{
					@Override
					public void onDoneButtonClicked(PickerFragment<?> fragment)
					{
						finishActivity();
					}
				});

		fragmentToShow = friendPickerFragment;

		manager.beginTransaction()
				.replace(R.id.picker_fragment, fragmentToShow).commit();
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		try
		{
			friendPickerFragment.loadData(false);
		} catch (Exception ex)
		{
			onError(ex);
		}
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		if (locationListener != null)
		{
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(locationListener);
			locationListener = null;
		}
	}

	private void onError(Exception error)
	{
		String text = "exception" + error.getMessage();
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		toast.show();
	}

	private void onError(String error, final boolean finishActivity)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("error_dialog_title")
				.setMessage(error)
				.setPositiveButton("error_dialog_button_text",
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i)
							{
								if (finishActivity)
								{
									finishActivity();
								}
							}
						});
		builder.show();
	}

	private void finishActivity()
	{
		// ScrumptiousApplication app = (ScrumptiousApplication)
		// getApplication();
		// if (FRIEND_PICKER.equals(getIntent().getData()))
		// {
		// if (friendPickerFragment != null)
		// {
		// app.setSelectedUsers(friendPickerFragment.getSelection());
		// }
		// } else if (PLACE_PICKER.equals(getIntent().getData()))
		// {
		// if (placePickerFragment != null)
		// {
		// app.setSelectedPlace(placePickerFragment.getSelection());
		// }
		// }
		// setResult(RESULT_OK, null);
		// finish();
	}
}
