package quizinterfaces;

import java.util.List;

import quiz.Answer;

public interface QuestionInterface {

	public String getQuestionText();

	public boolean isTrue();

	public AnswerInterface getRightAnswer();

	public List<? extends AnswerInterface> getAnswers();
}
