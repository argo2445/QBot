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
		            List<List<InlineKeyboardButton>> bList = new ArrayList<>();
		            List<InlineKeyboardButton> aList = new ArrayList<>();
					for(int z=0;z<quizInterface.fetchQuestion(update.getMessage().getChatId()).getAnswers().size();z++) {
						if(z%2 ==0) {
							aList = new ArrayList<>();
						}
						aList.add(new InlineKeyboardButton().setText(quizInterface
		            			.fetchQuestion(update.getMessage().getChatId()).getAnswers().get(z).getAnswerText())
		            			.setCallbackData("answer"+z));
		             	if(z%2 ==0) {
		             		bList.add(aList);
		                }
					}					
		            markupInline.setKeyboard(bList);
		            message.setReplyMarkup(markupInline);
		            
		            
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
