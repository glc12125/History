package appathon.history.models;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import appathon.history.models.qa.Question;

/*
 * GameUpdateRunnable is a member variable of GameManager. And it gets kicked off when
 * startCountDown() is called.
 * */

public class GameUpdateTimer extends MyCountDownTimer {

    // Tells the Runnable to pause for a certain number of milliseconds
    private static final long SLEEP_TIME_MILLISECONDS = 3000;
    
	private static int MILLIS_ROUNDTIME = 5000;
	
	private static int MILLIS_INTERVAL = 1000;
    
    // Sets the log tag
    private static final String LOG_TAG = "GameUpdateRunnable";
    
    // Defines a field that contains the calling object of type PhotoTask.
    final GameUpdateRunnableMethods gameUpdateTask;
    
    /**
    *
    * An interface that defines methods that gameUpdateTask implements. An instance of
    * gameUpdateTask passes itself to an GameUpdateRunnable instance through the
    * GameUpdateRunnable constructor, after which the two instances can access each other's
    * variables.
    */
    interface GameUpdateRunnableMethods {
        
        void showQuestion(Question question);
        
		boolean checkAnswers(long millisRoundTime);
		
		void getToNextRound();
		
		Question getCurrentQuestion();
		
		boolean gameEnd();
		
		void updateProgressBar(long millisPassed);
        
		void gameOver();
    }

    /**
     * This constructor creates an instance of PhotoDownloadRunnable and stores in it a reference
     * to the PhotoTask instance that instantiated it.
     *
     * @param gameUpdateTask The GameUpdateTask, which implements GameUpdateRunnableMethods
     */
    GameUpdateTimer(GameUpdateRunnableMethods gameUpdateTask) {
		super(MILLIS_ROUNDTIME, MILLIS_INTERVAL);
        this.gameUpdateTask = gameUpdateTask;
    }
    
	private void pauseForNextQuestion(){
		boolean gameEnd = gameUpdateTask.gameEnd();
		if(gameEnd){
			gameUpdateTask.gameOver();
			return;
		}
		
		Timer t = new Timer();
		t.schedule(new TimerTask() {			

			public void run() {
				setupNewQuestion();
				start();
			}
			
		}, SLEEP_TIME_MILLISECONDS);
	}
	
	private void setupNewQuestion()
	{
		gameUpdateTask.getToNextRound();
		gameUpdateTask.showQuestion(gameUpdateTask.getCurrentQuestion());
	}

	@Override
	public void onTick(long millisUntilFinished) {
		long millisPassed = MILLIS_ROUNDTIME - millisUntilFinished;
		gameUpdateTask.updateProgressBar(millisPassed);
		if (gameUpdateTask.checkAnswers(millisPassed))
		{
			this.cancel();
			pauseForNextQuestion();
		}
		
	}

	@Override
	public void onFinish() {
		pauseForNextQuestion();	
	}
}
