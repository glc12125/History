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

public class GameUpdateRunnable implements Runnable {

    // Tells the Runnable to pause for a certain number of milliseconds
    private static final long SLEEP_TIME_MILLISECONDS = 1000;
    
	private static int MILLIS_ROUNDTIME = 5000;
    
    // Sets the log tag
    private static final String LOG_TAG = "GameUpdateRunnable";
    
    // Defines a field that contains the calling object of type PhotoTask.
    final GameUpdateRunnableMethods gameUpdateTask;
    
    private CounterClass counter;
    
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
    GameUpdateRunnable(GameUpdateRunnableMethods gameUpdateTask) {
        this.gameUpdateTask = gameUpdateTask;
        this.counter = new CounterClass(MILLIS_ROUNDTIME, 1000);
    }
    
    /*
     * Defines this object's task, which is a set of instructions designed to be run on a Thread.
     */
    @Override
    public void run() {
        this.counter.start();
    }
    
    public void stop() {
    	this.counter.cancel();
    }
    
	public class CounterClass extends CountDownTimer
	{

		public CounterClass(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish()
		{
			pauseForNextQuestion();
			
			this.start();
		}
		
		private void pauseForNextQuestion(){
			Timer t = new Timer();
			t.schedule(new TimerTask() {			

				public void run() {
					boolean gameEnd = gameUpdateTask.gameEnd();
					if(gameEnd){
						gameUpdateTask.gameOver();
					}
					else{
						setupNewQuestion();
					}
				}
				
			}, 0, SLEEP_TIME_MILLISECONDS);
		}
		
		private void setupNewQuestion()
		{
			gameUpdateTask.getToNextRound();
			gameUpdateTask.showQuestion(gameUpdateTask.getCurrentQuestion());
		}

		@SuppressLint("NewApi")
		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		@Override
		public void onTick(long millisUntilFinished)
		{
			// call method in gamemanager to update progress bar
			// should call "bar.setProgress(value)" in this method
			
			long millisPassed = MILLIS_ROUNDTIME - millisUntilFinished;
			gameUpdateTask.updateProgressBar(millisPassed);
			if (gameUpdateTask.checkAnswers(millisPassed))
			{
				pauseForNextQuestion();
				this.start();
			}
		}
	}
}
