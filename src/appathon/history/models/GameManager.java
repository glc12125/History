package appathon.history.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.http.conn.ManagedClientConnection;

import android.R.bool;
import android.content.Context;
import appathon.history.MainActivity;
import appathon.history.R;

public class GameManager
{
	private QuestionGenerator questionGenerator;
	private ArrayList<Question> questions;
	private ArrayList<User> users;
	private HashMap<Country, ArrayList<Question>> countryToQuestionsMap;
	private HashMap<Country, User> countryToUser;
	private int currentQuestionIndex;
	private Context context;

	public boolean isFinished;

	public GameManager(Context context)
	{
		super();
		this.context = context;
		isFinished = false;

		currentQuestionIndex = 0;

		users = new ArrayList<User>();
		users.add(new User("Meng ZHang", true, R.drawable.avatar_meng_zhang,
				R.drawable.avatar_meng_zhang_small));
		users.add(new User("Chao Gao", false, R.drawable.avatar_chao_gao,
				R.drawable.avatar_gao_chao_small));
		users.add(new User("Liangchuan Gu", true,
				R.drawable.avatar_liangchuan_gu,
				R.drawable.avatar_liang_chuan_small));
		users.add(new User("Yimai Fang", true, R.drawable.avatar_yimai_fang,
				R.drawable.avatar_yi_mai_small));

		questionGenerator = new QuestionGenerator(this.context);
		questions = questionGenerator.getQuestions(100);

		countryToQuestionsMap = new HashMap<Country, ArrayList<Question>>();
		for (Question question : questions)
		{
			Country country = new Country(question.country.answer);
			if (countryToQuestionsMap.containsKey(country))
			{
				countryToQuestionsMap.get(country).add(question);
			} else
			{
				ArrayList<Question> newList = new ArrayList<Question>();
				newList.add(question);
				countryToQuestionsMap.put(country, newList);
			}
		}

		countryToUser = new HashMap<Country, User>();
	}

	public QuestionGenerator getQuestionGenerator()
	{
		return questionGenerator;
	}

	public void setQuestionGenerator(QuestionGenerator questionGenerator)
	{
		this.questionGenerator = questionGenerator;
	}

	public ArrayList<Question> getQuestions()
	{
		return questions;
	}

	public void setQuestions(ArrayList<Question> questions)
	{
		this.questions = questions;
	}

	public ArrayList<User> getUsers()
	{
		return users;
	}

	public void setUsers(ArrayList<User> users)
	{
		this.users = users;
	}

	public HashMap<Country, ArrayList<Question>> getCountryToQuestionsMap()
	{
		return countryToQuestionsMap;
	}

	public void setCountryToQuestionsMap(
			HashMap<Country, ArrayList<Question>> countryToQuestionsMap)
	{
		this.countryToQuestionsMap = countryToQuestionsMap;
	}

	public HashMap<Country, User> getCountryToUser()
	{
		return countryToUser;
	}

	public void setCountryToUser(HashMap<Country, User> countryToUser)
	{
		this.countryToUser = countryToUser;
	}

	public int getCurrentQuestionIndex()
	{
		return currentQuestionIndex;
	}

	public boolean checkAnswers()
	{
		int count = 0;
		Question currentQuestion = questions.get(this.currentQuestionIndex);

		for (int i = 0; i < users.size(); i++)
		{
			User user = users.get(i);

			if (user.isQuestionSubmitted())
			{
				count++;
				String selectedAnswer = null;

				if (user.isAI())
				{
					selectedAnswer = currentQuestion.options.get(i).answer;
				} else
				{
					selectedAnswer = user.getSelectedAnswer();
				}

				if (selectedAnswer.equals(currentQuestion.correctAnswer))
				{
					updatecountryToUser(currentQuestion.country.answer,
							users.get(i));
					return true;
				}
			}

		}

		if (count == users.size())
		{
			return true;
		} else
		{
			return false;
		}

	}

	private void updatecountryToUser(String countryName, User user)
	{
		for (Country country : countryToQuestionsMap.keySet())
		{
			if (country.getName().equals(countryName))
			{
				if (countryToUser.containsKey(country))
				{
					country.setDefense(country.getDefense() - 1);
					if (country.getDefense() == 0)
					{
						MainActivity.removeMarker(country.getName());
						country.setDefense(1);
						countryToUser.put(country, user);
						MainActivity.drawMarker(MainActivity.lg
								.getLocationFromAddress(context,
										country.getName()), user);
					}
				} else
				{
					country.setDefense(1);
					countryToUser.put(country, user);
					MainActivity.drawMarker(
							MainActivity.lg.getLocationFromAddress(context,
									country.getName()), user);
				}
				break;
			}
		}

	}

	public void RestartUsers()
	{
		for (int i = 0; i < users.size(); ++i)
		{
			users.get(i).restart();
		}
	}

	public Question NextQuestion()
	{
		currentQuestionIndex++;
		if (currentQuestionIndex >= questions.size())
		{
			currentQuestionIndex = 0;
		}

		RestartUsers();
		return questions.get(currentQuestionIndex);
	}

	public boolean updateUser(String userName, String selectedAnswer)
	{

		for (User user : users)
		{
			if (user.getName().equals(userName))
			{
				user.setSelectedAnswer(selectedAnswer);
				return true;
			}
		}

		return false;
	}

	public void calculateUserScores()
	{
		isFinished = true;

		Iterator entries = countryToUser.entrySet().iterator();
		while (entries.hasNext())
		{
			Entry thisEntry = (Entry) entries.next();
			Country country = (Country) thisEntry.getKey();
			User user = (User) thisEntry.getValue();
			user.setScore(user.getScore() + country.getDefense());
		}
	}

}
