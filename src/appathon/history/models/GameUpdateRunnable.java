package appathon.history.models;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.CountDownTimer;
import appathon.history.models.qa.Question;

public class GameUpdateRunnable implements Runnable {

    // Tells the Runnable to pause for a certain number of milliseconds
    private static final long SLEEP_TIME_MILLISECONDS = 250;
    
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
			gameUpdateTask.checkAnswers(MILLIS_ROUNDTIME);
			gameUpdateTask.getToNextRound();
			gameUpdateTask.showQuestion(gameUpdateTask.getCurrentQuestion());
			this.cancel();
			this.start();
		}

		@SuppressLint("NewApi")
		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		@Override
		public void onTick(long millisUntilFinished)
		{
//			long millis = millisUntilFinished;
//			String hms = String.format(
//					"%02d:%02d:%02d",
//					TimeUnit.MILLISECONDS.toHours(millis),
//					TimeUnit.MILLISECONDS.toMinutes(millis)
//					- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
//							.toHours(millis)),
//							TimeUnit.MILLISECONDS.toSeconds(millis)
//							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
//									.toMinutes(millis)));
//			System.out.println(hms);
			long millisPassed = MILLIS_ROUNDTIME - millisUntilFinished;
			if (gameUpdateTask.checkAnswers(millisPassed))
			{
				gameUpdateTask.getToNextRound();
				gameUpdateTask.showQuestion(gameUpdateTask.getCurrentQuestion());
				this.cancel();
				this.start();
			}
		}
	}
}
