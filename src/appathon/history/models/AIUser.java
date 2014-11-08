package appathon.history.models;

import java.util.Random;

import appathon.history.models.qa.Question;

public class AIUser extends User {

	private double correctPercentage; // To determine the probability AI can correctly answer one question, 0.0 - 1.0
	private long millis_mean; // Decide the average time one AI responses 
	private double millis_std; // Decide the variance of responding time

	
	public AIUser(int id) {
		this(id, 0.85, 30000, 1000);
	}
	
	public AIUser(int id, double correctPercentage, long millis_mean, double millis_std) {
		super(id, "", true);
		this.correctPercentage = correctPercentage;
		this.millis_mean = millis_mean;
		this.millis_std = millis_std;
		this.reactiveMillis = sampleMillis();
	}

	/**
	 * Determine the answer given by AI and how quick it replies
	 * @param s
	 */
	public void sampleReaction(Question q) {
		sampleMillis();
		Random r = new Random();
		double d = r.nextDouble();
		if(d < this.correctPercentage) {
			this.setSelectedAnswer(q.correctAnswer);
		}
		else this.setSelectedAnswer("###WRONG ANSWER");
	}
	
	/**
	 * Generate how many milliseconds can AI answer question
	 * @return
	 */
	private long sampleMillis() {
		Random r = new Random();
		this.reactiveMillis = Math.abs((long)(r.nextGaussian() * millis_std) + millis_mean);
		return this.reactiveMillis;
	}
	
	
}
