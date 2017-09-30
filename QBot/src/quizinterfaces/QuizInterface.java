package quizinterfaces;

import java.util.List;

public interface QuizInterface {
	
	public void createGame(long chatID);
	
	public void addPlayer(String name, long chatID);
	
	public void startGame(long chatID, int rounds);
	
	public QuestionInterface fetchQuestion(long chatID);
	
	public boolean enterAnswer(long chatID, String name, AnswerInterface answer);
	
	public List<String> retrieveScore(long chatID);
	
	public void endGame(long chatID);
}
