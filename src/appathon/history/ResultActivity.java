package appathon.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import appathon.history.models.Answer;
import appathon.history.models.Question;
import appathon.history.models.QuestionGenerator;
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

	public void popupTest(View v)
	{
		final Question question = generateFakeQuestion();

		View popupView = getLayoutInflater().inflate(
				R.layout.activity_main_popup_question, null);

		final PopupWindow mPopupWindow = new PopupWindow(popupView,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(false);

		TextView questionTextView = ((TextView) mPopupWindow.getContentView()
				.findViewById(R.id.questionTextView));
		questionTextView.setText(question.question);

		ListView answerListView = ((ListView) mPopupWindow.getContentView()
				.findViewById(R.id.answerListView));

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

				if (question.correctAnswer.equals(yourAnswer))
				{
					Toast.makeText(getApplicationContext(), "Correct answer!!",
							Toast.LENGTH_SHORT).show();
				} else
				{
					Toast.makeText(getApplicationContext(), "Wrong answer!!",
							Toast.LENGTH_SHORT).show();
				}

				mPopupWindow.dismiss();
			}
		});

		SimpleAdapter adapter = new SimpleAdapter(this,
				convertOptionsToMap(question.options),
				R.layout.activity_main_popup_question_list_item,
				new String[] { "option_string" },
				new int[] { R.id.option_string });

		answerListView.setAdapter(adapter);
		mPopupWindow.showAtLocation(findViewById(R.id.activity_result),
				Gravity.CENTER, 0, 0);
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

	private Question generateFakeQuestion()
	{
		QuestionGenerator qg = new QuestionGenerator(this.getApplicationContext());
		return qg.getQuestions(1).get(0);
	}

	private ArrayList<User> generateFakeUsers()
	{
		ArrayList<User> users = null;

		users = new ArrayList<User>();
		users.add(new User());
		users.add(new User());
		users.add(new User());
		users.add(new User());
		users.get(0).setAvatar(R.drawable.avatar_chao_gao);
		users.get(0).setName("Chao Gao");
		users.get(0).setScore(53);
		users.get(1).setAvatar(R.drawable.avatar_meng_zhang);
		users.get(1).setName("Meng Zhang");
		users.get(1).setScore(14);
		users.get(2).setAvatar(R.drawable.avatar_liangchuan_gu);
		users.get(2).setName("Liangchuan Gu");
		users.get(2).setScore(22);
		users.get(3).setAvatar(R.drawable.avatar_yimai_fang);
		users.get(3).setName("Yimai Fang");
		users.get(3).setScore(31);

		return users;
	}
}
