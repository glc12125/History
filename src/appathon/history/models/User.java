package appathon.history.models;

import java.util.ArrayList;

import appathon.history.R;
import appathon.qa.Question;

public class User implements Comparable<User>
{
	private String name;
	private boolean isAI;
	private int score;
	private int avatar;
	private boolean questionSubmitted;
	private int small_avatar;
	private String selectedAnswer;

	public User(String name, boolean isAI, int avatar, int small_avatar)
	{
		this.name = name;
		this.isAI = isAI;
		this.score = 0;
		this.avatar = avatar;
		this.questionSubmitted = false;
		this.small_avatar = small_avatar;
	}

	public String getName()
	{
		return name;
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

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public int getAvatar()
	{
		return avatar;
	}

	public void setAvatar(int avatar)
	{
		this.avatar = avatar;
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
		return selectedAnswer;
	}

	public void setSelectedAnswer(String selectedAnswer)
	{
		this.selectedAnswer = selectedAnswer;
	}

	public String sendResult(Question question, String selectedAnswer)
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

	public void restart()
	{
		if (isAI)
		{
			questionSubmitted = true;
		} else
		{
			questionSubmitted = false;
		}
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

	public static ArrayList<User> generateFakeUsers() {
		ArrayList<User> user_list = new ArrayList<User>();
		user_list.add(new User("Meng Zhang", true, R.drawable.avatar_meng_zhang,
				R.drawable.avatar_meng_zhang_small));
		user_list.add(new User("Chao Gao", true, R.drawable.avatar_chao_gao,
				R.drawable.avatar_chao_gao_small));
		user_list.add(new User("Liangchuan Gu", true,
				R.drawable.avatar_liangchuan_gu,
				R.drawable.avatar_liang_chuan_small));
		user_list.add(new User("Yimai Fang", false, R.drawable.avatar_yimai_fang,
				R.drawable.avatar_yi_mai_small));
		
		user_list.get(0).setScore(53);
		user_list.get(1).setScore(14);
		user_list.get(2).setScore(22);
		user_list.get(3).setScore(31);
		
		return user_list;
	}
}
