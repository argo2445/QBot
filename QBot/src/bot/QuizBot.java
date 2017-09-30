package bot;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.*;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import quizinterfaces.*;

public class QuizBot extends TelegramLongPollingBot {
	
	private SendMessage message;
	private int numberOfQuestions;
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
					numberOfQuestions=10;
					if(num !=null) {
					numberOfQuestions=Integer.parseInt(num);
					}
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText("Stelle "+numberOfQuestions+" Fragen");
					try {
						sendMessage(message);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
					quizInterface.startGame(update.getMessage().getChatId(), numberOfQuestions);
					message = new SendMessage()
							.setChatId(update.getMessage().getChatId())
							.setText(quizInterface.fetchQuestion(update.getMessage().getChatId()).getQuestionText());
					
					InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
		            List<InlineKeyboardButton> rowInline = new ArrayList<>();
		            
		            //for(String answer : quizInterface.fetchQuestion(update.getMessage().getChatId()).)
		            
		            /*List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
		            rowInline.add(new InlineKeyboardButton().setText("Update message text").setCallbackData("update_msg_text"));
		            rowInline.add(new InlineKeyboardButton().setText("Update message text2").setCallbackData("update_msg_text2"));
		           
		            rowInline2.add(new InlineKeyboardButton().setText("Update message text3").setCallbackData("update_msg_text3"));
		            rowInline2.add(new InlineKeyboardButton().setText("Update message text4").setCallbackData("update_msg_text4"));
		            // Set the keyboard to the markup
		            rowsInline.add(rowInline);
		            rowsInline.add(rowInline2);
		            // Add it to the message
		            markupInline.setKeyboard(rowsInline);
		            message.setReplyMarkup(markupInline);*/
		            
		            
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
		else if(update.hasCallbackQuery()){
			String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            if (call_data.equals("update_msg_text")) {
                String answer = "Updated message text";
                EditMessageText new_message = new EditMessageText()
                        .setChatId(chat_id)
                        .setMessageId(toIntExact(message_id))
                        .setText(answer);
                try {
                    editMessageText(new_message); 
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
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
