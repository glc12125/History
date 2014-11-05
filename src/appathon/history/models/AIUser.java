package appathon.history.models;

import java.util.Random;

import appathon.history.models.qa.Question;

public class AIUser extends User {

	private double correctPercentage; // To determine the probability AI can correctly answer one question, 0.0 - 1.0
	private double second_mean; // Decide the average time one AI responses 
	private double second_std; // Decide the variance of responding time
	private double second_this_turn; // Seconds this AI responds question this turn
	
	public AIUser(int id, String name, boolean isAI, int avatar, int small_avatar) {
		super(id, name, isAI, avatar, small_avatar);
	}
	
	public AIUser(int id, String name, int avatar, int small_avatar, 
			double correctPercentage, double second_mean, double second_std) {
		super(id, name, true, avatar, small_avatar);
		this.correctPercentage = correctPercentage;
		this.second_mean = second_mean;
		this.second_std = second_std;
		this.second_this_turn = sampleSecond();
	}

	public double sampleSecond() {
		Random r = new Random();
		return Math.abs(r.nextGaussian() * second_mean + second_std);
	}
	
	public double getSecondThisTurn() {
		return second_this_turn;
	}
	
	public String sendResult(Question question, String selectedAnswer)
	// Make an answer to one question
	{
		return null;
	}
	
}
