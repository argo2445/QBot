package bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class QuizBot extends TelegramLongPollingBot {
	
	SendMessage message;
	boolean gameIsRunning = false;
	boolean registrationOpen = false;
	
	@SuppressWarnings("deprecation")
	@Override
    public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()){
			if(update.getMessage().getText().equals("/open")){
				if(gameIsRunning == false){
					gameIsRunning = true;
					//person die eröffnet hat anmelden
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText("Weitere Personen anmelden durch: /join");
					registrationOpen = true;
				}
				else {
					message = new SendMessage()
						.setChatId(update.getMessage().getChatId())
						.setText("Ein Spiel läuft bereits.");
				}
			}
			else if(update.getMessage().getText().equals("/join")){
				if(registrationOpen == true){
					//person wird angemeldet 
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText(update.getMessage().getFrom().getFirstName() + " ist dem Spiel beigetreten. \n Weitere Personen anmelden durch: /join");
				}
				else {
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText("Keine Registration offen.");
				}
			}
			else if(update.getMessage().getText().equals("/start")){
				if(registrationOpen == true && gameIsRunning == true){
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText("Spiel startet jetzt.");
					registrationOpen = false;
					//start game
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
