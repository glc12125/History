package appathon.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

		ArrayList<User> users = MainActivity.manager.getUsers();

		// Sorting based on scores
		Collections.sort(users, Collections.reverseOrder());

		SimpleAdapter adapter = new SimpleAdapter(this,
				convertUsersToMap(users), R.layout.activity_result_list_item,
				new String[] { "avatar", "name", "score" }, new int[] {
						R.id.avatar, R.id.name, R.id.score });

		ListView listView = (ListView) findViewById(R.id.rankingListView);
		listView.setAdapter(adapter);
	}

	private List<Map<String, Object>> convertUsersToMap(ArrayList<User> users)
	{
		List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();

		for (User user : users)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("avatar", user.getAvatar());
			map.put("name", user.getName());
			map.put("score", user.getScore());
			userList.add(map);
		}

		return userList;
	}

	public void displayAllAnswers(View v)
	{
		Intent intent = new Intent();
		this.setResult(RESULT_OK, intent);
		this.finish();
	}
}
