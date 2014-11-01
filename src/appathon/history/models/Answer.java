package appathon.history.models;

import java.util.ArrayList;

public class Answer
{
	public String answer;
	public boolean isGeographic = false;
	public double longitude, latitude;

	public Answer(String answer)
	{
		super();
		this.answer = answer;
	}

}
