package quizinterfaces;

public interface QuestionInterface {

	public String getQuestionText();

	public boolean isTrue();
	
	public AnswerInterface getRightAnswer();
}
