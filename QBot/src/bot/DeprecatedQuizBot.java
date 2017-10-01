//package bot;
//
//import java.util.ArrayList;
//import java.util.List;
//import static java.lang.Math.*;
//import java.util.StringTokenizer;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.telegram.telegrambots.api.methods.send.SendMessage;
//import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
//import org.telegram.telegrambots.api.objects.Update;
//import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
//import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.exceptions.TelegramApiException;
//
//import quizinterfaces.*;
//
//public class DeprecatedQuizBot extends TelegramLongPollingBot {
//	
//	private SendMessage message;
//	private int numberOfQuestions;
//	private boolean gameIsRunning = false;
//	private boolean registrationOpen = false;
//	private QuizInterface quizInterface;
//	private InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
//    private List<List<InlineKeyboardButton>> bList = new ArrayList<>();
//    private List<InlineKeyboardButton> aList = new ArrayList<>();
//	
//    public DeprecatedQuizBot(QuizInterface quizInterface) {
//		this.quizInterface=quizInterface;
//	}
//    
//
//	@Override
//    public void onUpdateReceived(Update update){
//		
//		try {
//			execute(new SendMessage(1L, "test"));
//		} catch (TelegramApiException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		if (update.hasMessage() && update.getMessage().hasText()){
//			if(update.getMessage().getText().equals("/open")){
//				if(gameIsRunning == false){
//					gameIsRunning = true;
//					message = new SendMessage()
//							.setChatId(update.getMessage().getChatId())
//							.setText("Weitere Personen anmelden durch: /join");
//					registrationOpen = true;
//					quizInterface.createGame(update.getMessage().getChatId());
//					quizInterface.addPlayer(update.getMessage().getFrom().getId(), update.getMessage().getChatId());
//				}
//				else {
//					message = new SendMessage()
//						.setChatId(update.getMessage().getChatId())
//						.setText("Ein Spiel läuft bereits.");
//				}
//			}
//			else if(update.getMessage().getText().equals("/join")){
//				if(registrationOpen == true){ 
//					message = new SendMessage()
//							.setChatId(update.getMessage().getChatId())
//							.setText(update.getMessage().getFrom().getFirstName() + " ist dem Spiel beigetreten. \n Weitere Personen anmelden durch: /join");
//					quizInterface.addPlayer(update.getMessage().getFrom().getId(), update.getMessage().getChatId());
//				}
//				else {
//					message = new SendMessage()
//							.setChatId(update.getMessage().getChatId())
//							.setText("Keine Registration offen.");
//				}
//			}
//			else if(update.getMessage().getText().startsWith("/start")){
//				if(registrationOpen == true && gameIsRunning == true){
//					registrationOpen = false;
//					StringTokenizer st = new StringTokenizer(update.getMessage().getText());
//					String num = "10";
//					if(st.hasMoreTokens()) {
//						num = st.nextToken();
//					}
//					numberOfQuestions=10;
//					try {
//						numberOfQuestions=Integer.parseInt(num);
//						message = new SendMessage()
//								.setChatId(update.getMessage().getChatId())
//								.setText("Stelle "+numberOfQuestions+" Fragen");
//					}catch (NumberFormatException e ){
//						message = new SendMessage()
//								.setChatId(update.getMessage().getChatId())
//								.setText("Keine gültige Eingabe. /n Stelle "+numberOfQuestions+" Fragen");
//					}
//					try {
//						sendMessage(message);
//					} catch (TelegramApiException e) {
//						e.printStackTrace();
//					}
//					quizInterface.startGame(update.getMessage().getChatId(), numberOfQuestions);
//					showQuestions(update);  
//		            
//				}
//				else{
//					message = new SendMessage()
//							.setChatId(update.getMessage().getChatId())
//							.setText("Kein Spielstart möglich.");
//				}
//			}
//			else if(update.getMessage().getText().equals("/stop")){
//				if(gameIsRunning == true){
//					gameIsRunning = false;
//					//Spiel beenden
//					quizInterface.endGame(update.getMessage().getChatId());
//					message = new SendMessage()
//							.setChatId(update.getMessage().getChatId())
//							.setText("Spiel beendet.");
//				}
//				else{
//					message = new SendMessage()
//							.setChatId(update.getMessage().getChatId())
//							.setText("Kein Spiel momentan am laufen.");
//				}
//			}
//			else if(update.getMessage().getText().startsWith("/")){
//				message = new SendMessage()
//					.setChatId(update.getMessage().getChatId())
//					.setText("Es tut mir Leid diese Nachricht habe ich nicht verstanden.");
//			}
//			
//			
//			
//			try {
//				sendMessage(message);
//			} catch (TelegramApiException e) {
//				e.printStackTrace();
//			}
//		}
//		else if(update.hasCallbackQuery())
//		{
//			String call_data = update.getCallbackQuery().getData();
//           // long message_id = update.getCallbackQuery().getMessage().getMessageId();
//            long chat_id = update.getCallbackQuery().getMessage().getChatId();
//            int playerId = update.getCallbackQuery().getFrom().getId();
//            AnswerInterface answer = quizInterface.fetchQuestion(chat_id).getAnswers().get(Integer.parseInt(call_data));
//            quizInterface.enterAnswer(chat_id, playerId, answer);
//            
//            
//            /*if (call_data.equals("update_msg_text")) {
//                String answer = "Updated message text";
//                EditMessageText new_message = new EditMessageText()
//                        .setChatId(chat_id)
//                        .setMessageId(toIntExact(message_id))
//                        .setText(answer);
//                try {
//                    editMessageText(new_message); 
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }*/
//         }
//	}
//
//
//	@SuppressWarnings("deprecation")
//	private void showQuestions(Update update) {
//		
//		message = new SendMessage()
//				.setChatId(update.getMessage().getChatId())
//				.setText(quizInterface.fetchQuestion(update.getMessage().getChatId()).getQuestionText());
//		for(int z=0;z<quizInterface.fetchQuestion(update.getMessage().getChatId()).getAnswers().size();z++) {
//			if(z%2 ==0) {
//				aList = new ArrayList<>();
//			}
//			aList.add(new InlineKeyboardButton().setText(quizInterface
//					.fetchQuestion(update.getMessage().getChatId()).getAnswers().get(z).getAnswerText())
//					.setCallbackData(z+""));
//		 	if(z%2 ==0) {
//		 		bList.add(aList);
//		    }
//		}					
//		markupInline.setKeyboard(bList);
//		message.setReplyMarkup(markupInline);
//		try {
//			sendMessage(message);
//		} catch (TelegramApiException e) {
//			e.printStackTrace();
//		}
//		numberOfQuestions = numberOfQuestions - 1;
//		if(numberOfQuestions != 0) {
//			TimerTask tt=new TimerTask() {
//				@Override
//				public void run() {
//					showQuestions(update); //sollte nicht klappen... kein neues update bekommen
//				}
//			};
//			long delay = 2000;
//			new Timer().schedule(tt, delay);			
//			}
//		}
//	    
//
//    @Override
//    public String getBotUsername() {
//        return "QuizBot";
//    }
//
//    @Override
//    public String getBotToken() {
//        return "479671819:AAGfKq2wfUjWwcOXjeSbTSj6Pd2C1-DucbY";
//    }
//    
//}
