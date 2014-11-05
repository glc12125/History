package appathon.history.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;
import appathon.history.MainActivity.MsgHandler;
import appathon.history.R;
import appathon.history.models.qa.Question;
import appathon.history.models.qa.QuestionGenerator;

public class GameManager
{
	private QuestionGenerator questionGenerator;
	private ArrayList<Question> questions;
	private ArrayList<User> users;
	private HashMap<Country, CountryGameInfo> countryGameInfoMap;
	private int currentQuestionIndex;
	private Context context;
	private MsgHandler mHandler = null;
	public boolean isFinished;
	
	public GameManager(Context context, MsgHandler msgH)
	{
		super();
		this.context = context;
		this.mHandler = msgH;
		isFinished = false;

		initailizeUsers();

		questionGenerator = new QuestionGenerator(this.context);
		questions = questionGenerator.getQuestions(100);
		currentQuestionIndex = 0;
		initailizeCountryToQuestionMap();

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

	public int getCurrentQuestionIndex()
	{
		return currentQuestionIndex;
	}
	
	
	/**
	 * check the answers submitted by users are correct or not
	 * 
	 * @return whether we should pass to the next question
	 */
	public boolean checkAnswers(long millisPassed)
	{
		int count = 0;
		Question currentQuestion = questions.get(this.currentQuestionIndex);

		for (int i = 0; i < users.size(); i++)
		{
			User user = users.get(i);
			
			if(user.isChecked()) {
				count++;
				continue;
			}
			
			if((user.isAI() && user.getReactiveMillis() < millisPassed) || 
			(!user.isAI() && user.isQuestionSubmitted())) {
				user.checked();
				count++;
				String selectedAnswer = user.getSelectedAnswer();
				if (selectedAnswer.equals(currentQuestion.correctAnswer))
				{
					
					try {
						changeCountryGameInfo(currentQuestion.correspondingCountry, user);
						if (!user.isAI()) {
							Toast.makeText(context, "Correct!!", Toast.LENGTH_SHORT).show();
							playSoundCorrect();
						} else {
							Toast.makeText(context, "Country is controled by " + user.getName(), Toast.LENGTH_SHORT).show();
							playSoundWrong();
						}
						return true;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if(!user.isAI()) {
					playSoundWrong();
					Toast.makeText(context, "Wrong!!", Toast.LENGTH_SHORT).show();
				}
			}
		}
		return count == users.size(); // If all users have answer this question,
										// then we pass it to next round
	}
	
	
	/**
	 * Update GameInfoCountry for specific country
	 * @param countryName
	 * @param user
	 */
	private void changeCountryGameInfo(Country target_country, User user) throws Exception{
		if (!countryGameInfoMap.containsKey(target_country)) 
			throw new Exception("Country " + target_country.getName() + " does not exist in countryGameInfoMap");
		CountryGameInfo cgi = countryGameInfoMap.get(target_country);
		if(cgi.getUser() == null) {
			cgi.setDefense(1);
			cgi.setUser(user);
			drawMarker(target_country.getName(), user.getSmallAvatar());
		} else if(cgi.getUser().equals(user)){
			cgi.setDefense(cgi.getDefense() + 1);
		} else {
			cgi.setUser(user);
			drawMarker(target_country.getName(), user.getSmallAvatar());
		}
	}

	/**
	 * Method to notify MainActivity to draw marker for specific country and avatar
	 * @param countryName
	 * @param avatar
	 */
	private void drawMarker(String countryName, int avatar)
	{
		Message msg = new Message();
		msg.what = MsgHandler.MSG_TYPE_DRAW_MARKER;
		Bundle b = new Bundle();
		b.putString("countryName", countryName);
		b.putInt("avatar", avatar);
		msg.setData(b);
		mHandler.sendMessage(msg);
	}

	/**
	 * Method to notify MainActivity to remove marker for specific country
	 * @param countryName
	 */
	private void removeMarker(String countryName)
	{
		Message msg = new Message();
		msg.what = MsgHandler.MSG_TYPE_REMOVE_MARKER;
		Bundle b = new Bundle();
		b.putString("countryName", countryName);
		msg.setData(b);
		mHandler.sendMessage(msg);
	}

	/**
	 * Get to next round.
	 * Initailze game and user
	 * @return
	 */
	public Question getToNextRound() {
		Question q = getNextQuestion();
		restartUsers(q);
		return q;
	}
	
	
	/**
	 * Change the question submitted status for all users to False
	 */
	private void restartUsers(Question q)
	{
		for (int i = 0; i < users.size(); ++i)
		{
			User user = users.get(i);
			user.restartQuestionSubmittedStatus();
			if(user.isAI()) {
				AIUser ai_user = (AIUser) user;
				ai_user.sampleReaction(q);
			} else {
				user.setSelectedAnswer(null);
			}
		}
	}

	/**
	 * Get next question.
	 * @return next question to display for game
	 */
	private Question getNextQuestion()
	{
		currentQuestionIndex++;
		if (currentQuestionIndex >= questions.size())
		{
			currentQuestionIndex = 0;
		}
		return questions.get(currentQuestionIndex);
	}

	/**
	 * Get current question
	 * @return
	 */
	public Question getCurrentQuestion() {
		return questions.get(currentQuestionIndex);
	}
	
	/**
	 * Update selectedAnswer for user whose name is userName
	 * @param userName
	 * @param selectedAnswer
	 */
	public void updateUser(String userName, String selectedAnswer)
	{
		for (User user : users)
		{
			if (user.getName().equals(userName))
			{
				user.setSelectedAnswer(selectedAnswer);
				user.setQuestionSubmitted(true);
			}
		}
	}

	private void playSoundCorrect()
	{
		MediaPlayer mediaPlayer = MediaPlayer.create(context,
				R.raw.sound_correct);
		mediaPlayer.start();
	}

	private void playSoundWrong()
	{
		MediaPlayer mediaPlayer = MediaPlayer
				.create(context, R.raw.sound_wrong);
		mediaPlayer.start(); 
	}

	/**
	 *  Ranking the users based on the number of countries they have occupied
	 */
	public void rankUsers()
	{
		isFinished = true;

		for (CountryGameInfo cgi: countryGameInfoMap.values())
		{
			if(cgi.getUser() != null) {
				cgi.getUser().incrementNumOfCountriesByOne();
			}
		}

		// Sorting based on scores
		Collections.sort(users, Collections.reverseOrder());

		for (int i = 0; i < users.size(); i++)
		{
			User user = users.get(i);
			user.setRanking(i + 1);
		}
	}

	private void initailizeUsers()
	{
		this.users = new ArrayList<User>();
		users.add(new AIUser(1, "Meng Zhang", true, R.drawable.avatar_meng_zhang,
				R.drawable.avatar_meng_zhang_small));
		users.add(new AIUser(2, "Chao Gao", true, R.drawable.avatar_chao_gao,
				R.drawable.avatar_chao_gao_small));
		users.add(new AIUser(3, "Liangchuan Gu", true,
				R.drawable.avatar_liangchuan_gu,
				R.drawable.avatar_liang_chuan_small));
		users.add(new User(4, "Yimai Fang", false, R.drawable.avatar_yimai_fang,
				R.drawable.avatar_yi_mai_small));
	}

	private void initailizeCountryToQuestionMap()
	{
		this.countryGameInfoMap = new HashMap<Country, CountryGameInfo>();
		if (questions == null)
		{
			throw new NullPointerException();
		}

		for (Question question : questions)
		{
			Country country = question.correspondingCountry;
			if (countryGameInfoMap.containsKey(country))
			{
				countryGameInfoMap.get(country).addQuestion(question);
			} else
			{
				CountryGameInfo cgi = new CountryGameInfo(country, question);
				countryGameInfoMap.put(country, cgi);
			}
	
		}
	}

}
