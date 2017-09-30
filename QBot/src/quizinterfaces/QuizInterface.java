package quizinterfaces;

import java.util.List;

public interface QuizInterface {
	
	public int createGame();
	
	public void addPlayer(String name, int gameID);
	
	public void startGame(int gameID, int rounds);
	
	public QuestionInterface fetchQuestion(int gameID);
	
	public boolean enterAnswer(int gameID, String name, AnswerInterface answer);
	
	public List<String> retrieveScore(int gameID);
	
	public void endGame(int gameID);
}
