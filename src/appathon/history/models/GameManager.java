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
import appathon.history.models.GameUpdateRunnable.GameUpdateRunnableMethods;
import appathon.history.models.qa.Question;
import appathon.history.models.qa.QuestionGenerator;

public class GameManager implements GameUpdateRunnableMethods
{
    /*
     * Field containing the Thread this task is running on.
     */
    private Thread currentThread;
    private GameUpdateRunnable gameUpdateRunnable;

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
		gameUpdateRunnable = new GameUpdateRunnable(this);
		restartUsers();
	}

	public GameUpdateRunnable getGameUpdateRunnable() {
		return gameUpdateRunnable;
	}
	
	public void startCountDown(){
		if(gameUpdateRunnable !=null){
			Thread t = new Thread(gameUpdateRunnable);
			currentThread = t;
	        t.start();
		}
	}

	public Thread getCurrentThread() {
		return currentThread;
	}
	
	/**
	 * check the answers submitted by users are correct or not
	 * 
	 * @return whether we should pass to the next question
	 */
	@Override
	public boolean checkAnswers(long millisPassed)
	{
		int count = 0;
		Question currentQuestion = getCurrentQuestion();

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
				if (selectedAnswer != null && selectedAnswer.equals(currentQuestion.correctAnswer))
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
						drawMarker(currentQuestion.correspondingCountry, user.getSmallAvatar());
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
		} else if(cgi.getUser().equals(user)){
			cgi.setDefense(cgi.getDefense() + 1);
		} else {
			cgi.setUser(user);
		}
	}

	/**
	 * Method to notify MainActivity to draw marker for specific country and avatar
	 * @param countryName
	 * @param avatar
	 */
	private void drawMarker(Country country, int avatar)
	{
		Message msg = new Message();
		msg.what = MsgHandler.MSG_TYPE_DRAW_MARKER;
		Bundle b = new Bundle();
		b.putString("countryName", country.getName());
		b.putInt("avatar", avatar);
		msg.setData(b);
		mHandler.sendMessage(msg);
	}

	/**
	 * Method to notify MainActivity to remove marker for specific country
	 * @param countryName
	 */
	private void removeMarker(Country country)
	{
		Message msg = new Message();
		msg.what = MsgHandler.MSG_TYPE_REMOVE_MARKER;
		Bundle b = new Bundle();
		b.putString("countryName", country.getName());
		msg.setData(b);
		mHandler.sendMessage(msg);
	}

	/**
	 * Get to next round.
	 * Initialize next question and users' status
	 * @return
	 */
	public void getToNextRound() {
		getNextQuestion();
		restartUsers();
	}
	
	/**
	 * Change the question submitted status for all users to False
	 */
	private void restartUsers()
	{
		for (int i = 0; i < users.size(); ++i)
		{
			User user = users.get(i);
			user.restartQuestionSubmittedStatus();
			if(user.isAI()) {
				AIUser ai_user = (AIUser) user;
				ai_user.sampleReaction(this.getCurrentQuestion());
			} else {
				user.setSelectedAnswer(null);
			}
		}
	}

	/**
	 * Get next question.
	 * @return next question to display for game
	 */
	public Question getNextQuestion()
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
	@Override
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

	/**
	 *  Ranking the users based on the score
	 */
	public void rankUsers()
	{
		isFinished = true;

		for (CountryGameInfo cgi: countryGameInfoMap.values())
		{
			if(cgi.getUser() != null) {
				cgi.getUser().setScore(cgi.getDefense());
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
			throw new NullPointerException("Quesitions are null");
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
	
	public ArrayList<Question> getQuestions()
	{
		return questions;
	}

	public ArrayList<User> getUsers()
	{
		return users;
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

	@Override
	public void showQuestion(Question question) {
		Message msg = new Message();
		msg.what = MsgHandler.MSG_TYPE_SHOW_QUESTION;
		Bundle b = new Bundle();
		b.putSerializable("question", question);
		msg.setData(b);
		mHandler.sendMessage(msg);
		
	}

}
