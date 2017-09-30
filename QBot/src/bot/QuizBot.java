package bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import quizinterfaces.*;

public class QuizBot extends TelegramLongPollingBot {
	
	private SendMessage message;
	private boolean gameIsRunning = false;
	private boolean registrationOpen = false;
	private QuizInterface quizInterface;
	
	@SuppressWarnings("deprecation")
	@Override
    public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()){
			if(update.getMessage().getText().equals("/open")){
				if(gameIsRunning == false){
					gameIsRunning = true;
					//person die er�ffnet hat anmelden
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText("Weitere Personen anmelden durch: /join");
					registrationOpen = true;
					quizInterface.createGame(update.getMessage().getChatId());
					quizInterface.addPlayer(update.getMessage().getFrom().getId(), update.getMessage().getChatId());
				}
				else {
					message = new SendMessage()
						.setChatId(update.getMessage().getChatId())
						.setText("Ein Spiel l�uft bereits.");
				}
			}
			else if(update.getMessage().getText().equals("/join")){
				if(registrationOpen == true){
					//person wird angemeldet 
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText(update.getMessage().getFrom().getFirstName() + " ist dem Spiel beigetreten. \n Weitere Personen anmelden durch: /join");
					quizInterface.addPlayer(update.getMessage().getFrom().getId(), update.getMessage().getChatId());
				}
				else {
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText("Keine Registration offen.");
				}
			}
			else if(update.getMessage().getText().startsWith("/start")){
				if(registrationOpen == true && gameIsRunning == true){
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText("Spiel startet jetzt.");
					registrationOpen = false;
					//start game
					String string = update.getMessage().getText();
					String[] parts = string.split(" ");
					String num = parts[1]; //Number
					int numberOfQuestions=10;
					if(num !=null) {
					numberOfQuestions=Integer.parseInt(num);
					}
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText("Stelle"+numberOfQuestions+" Fragen");
					quizInterface.startGame(update.getMessage().getChatId(), numberOfQuestions);
				}
				else{
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText("Kein Spielstart möglich.");
				}
			}
			else if(update.getMessage().getText().equals("/stop")){
				if(gameIsRunning == true){
					gameIsRunning = false;
					//Spiel beenden
					quizInterface.endGame(update.getMessage().getChatId());
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText("Spiel beendet.");
				}
				else{
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText("Kein Spiel momentan am laufen.");
				}
			}
			else if(update.getMessage().getText().startsWith("/")){
				message = new SendMessage()
					.setChatId(update.getMessage().getChatId())
					.setText("Es tut mir Leid diese Nachricht habe ich nicht verstanden.");
			}
			try {
				sendMessage(message);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
    }

    @Override
    public String getBotUsername() {
        return "QuizBot";
    }

    @Override
    public String getBotToken() {
        return "479671819:AAGfKq2wfUjWwcOXjeSbTSj6Pd2C1-DucbY";
    }
    
}
