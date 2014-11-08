package appathon.history.models;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import appathon.exception.NumOfCountriesException;
import appathon.history.R;
import appathon.history.models.qa.Question;

public class User implements Comparable<User>
{
	protected static ArrayList<Integer> availableIcons = new ArrayList<Integer>(
			Arrays.asList(R.drawable.zuck, R.drawable.ma, R.drawable.jobs,
					R.drawable.cameron, R.drawable.obama, R.drawable.merkel,
					R.drawable.kat));
	protected static ArrayList<String> availableNames = new ArrayList<String>(
			Arrays.asList("M��rk Z��ckerberg", "J��ck M��", "St��ve J��bs",
					"D��vid C��meron", "Bar��ck ��bama", "Ang��la M��rkel",
					"Kam Kat"));
	protected static Random randomGenerator = new Random();

	private String name;
	private boolean isAI;
	private int id;
	private int score;
	private int ranking;
	private int avatar;
	private boolean questionSubmitted;
	private int small_avatar;
	private boolean isChecked;
	private ArrayList<Country> controlledCountries;
	protected long reactiveMillis;
	protected String selectedAnswer;
	private Bitmap bitmap;

	public User(int id, String name, boolean isAI)
	{
		this.id = id;

		this.name = name;

		if (name.contains("Meng"))
		{
			this.small_avatar = R.drawable.avatar_meng_zhang;
			this.avatar = R.drawable.avatar_meng_zhang;
		} else if (name.contains("Liangchuan"))

		{
			this.small_avatar = R.drawable.liangchuan;
			this.avatar = R.drawable.liangchuan;
		} else if (name.contains("Yimai"))

		{
			this.small_avatar = R.drawable.yimai;
			this.avatar = R.drawable.yimai;
		} else if (name.contains("Chao"))
		{
			this.small_avatar = R.drawable.chao;
			this.avatar = R.drawable.chao;
		} else
		{
			this.small_avatar = R.drawable.meng;
			this.avatar = R.drawable.meng;
		}

		this.isAI = isAI;
		this.score = 0;
		this.questionSubmitted = false;
		this.controlledCountries = new ArrayList<Country>();
		this.reactiveMillis = -1;
		this.isChecked = false;
	}

	private Bitmap loadBitmap(URI uri)
	{
		URL url = null;
		try
		{
			url = uri.toURL();
		} catch (MalformedURLException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Bitmap bm = null;
		InputStream is = null;
		BufferedInputStream bis = null;
		try
		{
			URLConnection conn = url.openConnection();
			conn.connect();
			is = conn.getInputStream();
			bis = new BufferedInputStream(is, 8192);
			bm = BitmapFactory.decodeStream(bis);
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (bis != null)
			{
				try
				{
					bis.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (is != null)
			{
				try
				{
					is.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		if (bm == null)
		{
			Log.e("null", "null");
		} else
		{
			Log.e("not null", "not null");
		}

		return bm;
	}

	public long getReactiveMillis()
	{
		return this.reactiveMillis;
	}

	public void setReactiveMillis(long ms)
	{
		this.reactiveMillis = ms;
	}

	public void gainCountry(Country country)
	{
		this.controlledCountries.add(country);
	}

	public void loseCountry(Country country)
	{
		this.controlledCountries.remove(country);
	}

	public int getNumOfCountries()
	{
		return this.controlledCountries.size();
	}

	public ArrayList<Country> getCountries()
	{
		return this.controlledCountries;
	}

	public String getName()
	{
		return name;
	}

	public Bitmap getBitmap()
	{
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}

	public int getSmallAvatar()
	{
		return this.small_avatar;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isAI()
	{
		return isAI;
	}

	public void setAI(boolean isAI)
	{
		this.isAI = isAI;
	}

	public boolean isChecked()
	{
		return isChecked;
	}

	public void checked()
	{
		isChecked = true;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public int getRanking()
	{
		return ranking;
	}

	public void setRanking(int ranking)
	{
		this.ranking = ranking;
	}

	public int getAvatar()
	{
		return avatar;
	}

	public int getId()
	{
		return id;
	}

	public boolean isQuestionSubmitted()
	{
		return questionSubmitted;
	}

	public void setQuestionSubmitted(boolean questionSubmitted)
	{
		this.questionSubmitted = questionSubmitted;
	}

	public String getSelectedAnswer()
	{
		return this.selectedAnswer;
	}

	public void setSelectedAnswer(String selectedAnswer)
	{
		this.selectedAnswer = selectedAnswer;
	}

	public String sendResult(Question question, String selectedAnswer)
	// Make an answer to one question
	{
		if (isAI)
		{
			// randomly return answer
			return null;
		} else
		{
			return selectedAnswer;
		}
	}

	public void restartQuestionSubmittedStatus()
	{
		isChecked = false;
		questionSubmitted = false;
	}

	@Override
	public int compareTo(User aUser)
	{
		if (aUser instanceof User)
		{
			return (score - aUser.score);
		}

		throw new ClassCastException("Cannot compare User with "
				+ aUser.getClass().getName());
	}

	@Override
	public String toString()
	{
		return name + "\ttotal scores:" + score;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
