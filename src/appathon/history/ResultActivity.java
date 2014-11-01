package appathon.history;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import appathon.history.models.User;

public class ResultActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		Bundle bundle = this.getIntent().getExtras();
		ArrayList<User> users = null;
		
		//for debug -- Start
		users = new ArrayList<User>();
		users.add(new User());
		users.add(new User());
		users.add(new User());
		users.add(new User());
		users.get(0).setName("Chao Gao");
		users.get(1).setName("Meng Zhang");
		users.get(2).setName("Liangchuan Gu");
		users.get(3).setName("Yimai Fang");
		//for debug -- End
		
		if (bundle != null)
		{
			users = (ArrayList<User>) bundle.getSerializable("userMap");
		}

		if (users == null)
		{
			return;
		}

		// Sorting based on scores
		Collections.sort(users);
		ArrayAdapter<User> adapter = new ArrayAdapter<User>(this,
				android.R.layout.simple_list_item_1, users);
		ListView listView = (ListView) findViewById(R.id.rankingListView);
		listView.setAdapter(adapter);
	}
}
