package quizinterfaces;

import java.util.List;

public interface QuestionInterface {

	public String getQuestionText();

	public boolean isTrue();

	public AnswerInterface getRightAnswer();

	public List<? extends AnswerInterface> getAnswers();

	public int getDataBaseId();

}
