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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;

/**
 * The PickerActivity enhances the Friend or Place Picker by adding a title and
 * a Done button. The selection results are saved in the ScrumptiousApplication
 * instance.
 */
public class PickerActivity extends FragmentActivity
{
	private FriendPickerFragment friendPickerFragment;

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
		HashMap<String, URI> facebook_friends_username_imageuri = new HashMap<String, URI>();

		List<GraphUser> selectedUsers = friendPickerFragment.getSelection();

		for (GraphUser graphUser : selectedUsers)
		{
			JSONObject jsonUser = graphUser.getInnerJSONObject();

			URI user_avatar_uri = null;

			try
			{
				user_avatar_uri = new URI(jsonUser.getJSONObject("picture")
						.getJSONObject("data").getString("url"));
			} catch (URISyntaxException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			facebook_friends_username_imageuri.put(graphUser.getName(),
					user_avatar_uri);
		}

		Intent intent = new Intent(this, MainActivity.class);

		intent.putExtra("facebook_friends_username_imageuri",
				facebook_friends_username_imageuri);
		
		startActivity(intent);
		finish();
	}

	private void showAlert(String title, String message)
	{
		new AlertDialog.Builder(this).setTitle(title).setMessage(message)
				.setPositiveButton("ok", null).show();
	}
}
