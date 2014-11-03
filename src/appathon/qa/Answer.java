package appathon.qa;

public class Answer
{
	public String answer;
	public boolean isGeographic = false;
	public double longitude, latitude;
	
	public Answer(String answer, boolean isGeographic){
		super();
		this.answer = answer;
		this.isGeographic = isGeographic;
	}

	@Override
	public String toString() {
		return answer;
	}
}
