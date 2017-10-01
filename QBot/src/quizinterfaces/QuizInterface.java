package quizinterfaces;

import java.util.List;

public interface QuizInterface {
	
	/**
	 * Creates a game that is associated with the give Chat
	 * @param chatID
	 */
	public void createGame(long chatID);
	
	/**
	 * Adds Player to a game
	 * @param name Playername
	 * @param chatID Id of the Chat where the Game is played
	 */
	public void addPlayer(int playerId, long chatID, String userName);
	
	/**
	 * Starts the game 
	 * @param chatID Id of the Chat where the game is played
	 * @param rounds Number of questions of the game
	 */
	public void startGame(long chatID, int rounds);
	
	/**
	 * @param chatID Id of the Chat where the game is played
	 * @return Current Question
	 */
	public QuestionInterface fetchQuestion(long chatID);
	
	
	/**
	 * Enters the given answer of the Player
	 * @param chatID Id of the Chat where the game is played
	 * @param playerId Identifier of the Player
	 * @param answer given Answer
	 * @return true if Answer was right
	 */
	public boolean enterAnswer(long chatID, int playerId, int answerDbId);
	
	/**
	 * Gets the current Score
	 * @param chatID Id of the Chat where the game is played
	 * @return The PlayerId:Score per Player as List
	 */
	public List<String> retrieveScore(long chatID);
	
	/**
	 * Ends the given Game
	 * @param chatID Id of the Chat where the game is played
	 */
	public void endGame(long chatID);
}
