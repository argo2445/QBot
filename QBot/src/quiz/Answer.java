package quiz;

import quizinterfaces.AnswerInterface;

public class Answer implements AnswerInterface {

	private int databaseId;
	private String text;

	public Answer(int databaseId, String text) {
		this.databaseId=databaseId;
		this.text=text;
	}
	
	public int getDatabaseId() {
		return databaseId;
	}
	
	@Override
	public String getAnswerText() {
		return text;
	}

}
