package appathon.history.models;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;
import appathon.history.MainActivity;
import appathon.history.R;
import appathon.qa.Question;
import appathon.qa.QuestionGenerator;

public class GameManager
{
	private QuestionGenerator questionGenerator;
	private ArrayList<Question> questions;
	private ArrayList<User> users;
	private HashMap<Country, ArrayList<Question>> countryToQuestionMap;
	private HashMap<Country, User> countryToUserMap;
	private int currentQuestionIndex;
	private Context context;

	public boolean isFinished;

	public GameManager(Context context)
	{
		super();
		this.context = context;
		isFinished = false;

		users = initailizeUsers();
		
		questionGenerator = new QuestionGenerator(this.context);
		questions = questionGenerator.getQuestions(100);
		currentQuestionIndex = 0;
		
		countryToQuestionMap = initailizeCountryToQuestionMap();
		countryToUserMap = new HashMap<Country, User>();
	}

	public QuestionGenerator getQuestionGenerator()
	{
		return questionGenerator;
	}

	public ArrayList<Question> getQuestions()
	{
		return questions;
	}

	public ArrayList<User> getUsers()
	{
		return users;
	}

	public HashMap<Country, ArrayList<Question>> getCountryToQuestionsMap()
	{
		return countryToQuestionMap;
	}

	public HashMap<Country, User> getCountryToUser()
	{
		return countryToUserMap;
	}

	public int getCurrentQuestionIndex()
	{
		return currentQuestionIndex;
	}
	/**
	 * check the answers submitted by users are correct or not
	 * @return whether we should pass to the next question
	 */
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
				String selectedAnswer;

				if (user.isAI())
				{
					selectedAnswer = currentQuestion.options.get(i).answer;
				} else
				{
					selectedAnswer = user.getSelectedAnswer();
				}

				if (selectedAnswer.equals(currentQuestion.correctAnswer))
				{
					if (user.getName().equals(MainActivity.userName))
					{
						playSoundCorrect();
					} else
					{
						playSoundWrong();
					}
					updatecountryToUser(currentQuestion.correspondingCountry.getName(), user);
					return true;
				}
			}
		}
		return count == users.size(); // If all users have answer this question, then we pass it to next round
	}

	private void updatecountryToUser(String countryName, User user)
	{
		Country target_country = new Country(countryName);
		if(countryToQuestionMap.containsKey(target_country)) {
			Country country = target_country;
			if (countryToUserMap.containsKey(country))
			{
				country.setDefense(country.getDefense() - 1);
				if (country.getDefense() == 0)
				{
					MainActivity.removeMarker(country.getName());
					country.setDefense(1);
					countryToUserMap.put(country, user);
					MainActivity.drawMarker(MainActivity.lg
							.getLocationFromAddress(context,
									country.getName()), user
							.getSmallAvatar());
				}
			} else
			{
				country.setDefense(1);
				countryToUserMap.put(country, user);
				MainActivity.drawMarker(
						MainActivity.lg.getLocationFromAddress(context,
								country.getName()), user.getSmallAvatar());
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
		Question currentQuestion = questions.get(this.currentQuestionIndex);
		for (User user : users)
		{
			if (user.getName().equals(userName))
			{
				user.setSelectedAnswer(selectedAnswer);
				user.setQuestionSubmitted(true);

				if (currentQuestion.correctAnswer.equals(selectedAnswer))
				{
					playSoundCorrect();
					Toast.makeText(context, "Correct!!", Toast.LENGTH_SHORT)
							.show();
				} else
				{
					playSoundWrong();
					Toast.makeText(context, "Wrong!!", Toast.LENGTH_SHORT)
							.show();
				}
				return true;
			}
		}

		return false;
	}

	private void playSoundCorrect()
	{
		MediaPlayer mediaPlayer = MediaPlayer.create(context,
				R.raw.sound_correct);
		mediaPlayer.start(); // no need to call prepare(); create() does that
		// for you

	}

	private void playSoundWrong()
	{
		MediaPlayer mediaPlayer = MediaPlayer
				.create(context, R.raw.sound_wrong);
		mediaPlayer.start(); // no need to call prepare(); create() does that
								// for you
	}

	public void calculateUserScores()
	{
		isFinished = true;

		for (Country country : countryToQuestionMap.keySet())
		{
			if (countryToUserMap.containsKey(country))
			{
				User user = countryToUserMap.get(country);
				user.setScore(user.getScore() + country.getDefense());
			}

		}

	}
	
	private ArrayList<User> initailizeUsers() {
		ArrayList<User> users = new ArrayList<User>();
		users.add(new User("Meng Zhang", true, R.drawable.avatar_meng_zhang,
				R.drawable.avatar_meng_zhang_small));
		users.add(new User("Chao Gao", true, R.drawable.avatar_chao_gao,
				R.drawable.avatar_chao_gao_small));
		users.add(new User("Liangchuan Gu", true,
				R.drawable.avatar_liangchuan_gu,
				R.drawable.avatar_liang_chuan_small));
		users.add(new User("Yimai Fang", false, R.drawable.avatar_yimai_fang,
				R.drawable.avatar_yi_mai_small));
		return users;
	}
	
	private HashMap<Country, ArrayList<Question>> initailizeCountryToQuestionMap() {
		if(questions == null) {
			throw new NullPointerException();
		}
		
		HashMap<Country, ArrayList<Question>> countryToQuestionMap = new HashMap<Country, ArrayList<Question>>();
		for (Question question : questions)
		{
			Country country = question.correspondingCountry;
			if (countryToQuestionMap.containsKey(country))
			{
				countryToQuestionMap.get(country).add(question);
			} else
			{
				ArrayList<Question> newList = new ArrayList<Question>();
				newList.add(question);
				countryToQuestionMap.put(country, newList);
			}
		}
		return countryToQuestionMap;
	}

}
