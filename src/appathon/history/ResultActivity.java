package appathon.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

		if (bundle != null)
		{
			users = (ArrayList<User>) bundle.getSerializable("userMap");
		}

		if (users == null)
		{
			// for debug -- Start
			users = generateFakeUsers();
			// for debug -- End
		}

		// Sorting based on scores
		Collections.sort(users);

		SimpleAdapter adapter = new SimpleAdapter(this, getData(users),
				R.layout.activity_result_list_item, new String[] { "avatar",
						"name", "score" }, new int[] { R.id.avatar, R.id.name,
						R.id.score });

		ListView listView = (ListView) findViewById(R.id.rankingListView);
		listView.setAdapter(adapter);
	}

	private ArrayList<User> generateFakeUsers()
	{
		ArrayList<User> users = null;

		users = new ArrayList<User>();
		users.add(new User());
		users.add(new User());
		users.add(new User());
		users.add(new User());
		users.get(0).setName("Chao Gao");
		users.get(1).setName("Meng Zhang");
		users.get(2).setName("Liangchuan Gu");
		users.get(3).setName("Yimai Fang");

		return users;
	}

	private List<Map<String, Object>> getData(ArrayList<User> users)
	{
		List<Map<String, Object>> usersList = new ArrayList<Map<String, Object>>();

		for (User user : users)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("avatar", user.getAvatar());
			map.put("name", user.getName());
			map.put("score", user.getScore());
			usersList.add(map);
		}

		return usersList;
	}
}
