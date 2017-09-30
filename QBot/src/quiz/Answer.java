package quiz;

import quizinterfaces.AnswerInterface;

public class Answer implements AnswerInterface {

	private int databaseId;
	private String text;
	private Question question;

	public Answer(Question question, int databaseId, String text) {
		this.databaseId = databaseId;
		this.text = text;
		this.question = question;
	}

	/**
	 * @return the question
	 */
	public Question getQuestion() {
		return this.question;
	}

	public int getDatabaseId() {
		return databaseId;
	}

	@Override
	public String getAnswerText() {
		return text;
	}

}
