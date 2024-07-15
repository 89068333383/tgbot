package lessons.java;

import commands.AppBotCommand;
import commands.BotCommonCommands;
import functions.FilterOperations;
import functions.ImageOperation;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;

import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.ImageUtils;
import utils.PhotoMessageUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    HashMap<String, Message> messages = new HashMap<>();

    Class[] commandClasses = new Class[]{BotCommonCommands.class};
    @Override
    public String getBotUsername() {
        return "ku_mozg_bot";
    }

    @Override
    public String getBotToken() {
        return "7063375562:AAEL0c8mbXw19-eOg36aYo_tMiYXyhUQ1vg";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        try {
            SendMessage responseTextMessage = runCommonComand(message);
            if (responseTextMessage != null) {
                execute(responseTextMessage);
                return;
            }
            responseTextMessage = runPhotoMessage(message);
            if (responseTextMessage != null) {
                execute(responseTextMessage);
                return;
            }
            Object responseMediaMessage = runPhotoFilter(message);
            if (responseMediaMessage instanceof SendMediaGroup) {
                SendMediaGroup testResponseMediaMessage = (SendMediaGroup) responseMediaMessage;
                execute(testResponseMediaMessage);

            }else if (responseMediaMessage instanceof SendMessage){
                execute((SendMessage) responseMediaMessage);
            }
            return;
        } catch (InvocationTargetException | IllegalAccessException | TelegramApiException e) {
            e.printStackTrace();
        }

    }

//    private String runComand(String text) throws InvocationTargetException, IllegalAccessException {
//        for (int i = 0; i < commandClasses.length; i++) {
//            BotCommonCommands commands = new BotCommonCommands();
//            Method[] classsMethods = commands.getClass().getDeclaredMethods();
//            for (Method method : classsMethods) {
//                if (method.isAnnotationPresent(AppBotCommand.class)) {
//                    AppBotCommand command = method.getAnnotation(AppBotCommand.class);
//                    if (command.name().equals(text)){
//                        method.setAccessible(true);
//                        return (String) method.invoke(commands);
//                    }
//                }
//            }
//        }
//        BotCommonCommands commands = new BotCommonCommands();
//
//        return null;
//    }
    private SendMessage runCommonComand(Message message) throws InvocationTargetException, IllegalAccessException {

        String text = message.getText();
        BotCommonCommands commands = new BotCommonCommands();
        Method[] classsMethods = commands.getClass().getDeclaredMethods();
        for (Method method : classsMethods) {
            if (method.isAnnotationPresent(AppBotCommand.class)) {
                AppBotCommand command = method.getAnnotation(AppBotCommand.class);
                if (command.name().equals(text)) {
                    method.setAccessible(true);
                    String responseText = (String) method.invoke(commands);
                    if (responseText != null) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(message.getChatId().toString());
                        sendMessage.setText(responseText);
                        return sendMessage;
                    }
                }
            }
        }
        return null;

    }

    private SendMessage runPhotoMessage(Message message){
        List<File> files = getFilesByMessage(message);
        if (files.isEmpty()) return null;
        String chatId = message.getChatId().toString();
        messages.put(message.getChatId().toString(), message);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> allKeyboardRows = new ArrayList<>(getKeybadRow(FilterOperations.class));

        replyKeyboardMarkup.setKeyboard(allKeyboardRows);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите фильтр");
        return  sendMessage;

    }
    private List<File> getFilesByMessage(Message message){
        List<PhotoSize> photoSizes = message.getPhoto();
        if (photoSizes == null) return new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();
        for (PhotoSize photoSize : photoSizes) {
            final String fileId = photoSize.getFileId();
            try {
                files.add(sendApiMethod(new GetFile(fileId)));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return files;
    }

    private Object runPhotoFilter (Message newMessage){
        final String text = newMessage.getText();
        ImageOperation operation = ImageUtils.getOperation(text);
        String chatId = newMessage.getChatId().toString();
        if(operation == null) return null;
        Message photoMessage = messages.get(chatId);
        if (photoMessage !=null) {
            List<File> files = getFilesByMessage(photoMessage);
            try {
                List<String> paths = PhotoMessageUtils.savePhotos(files, getBotToken());
                return preparePhotoMessage(paths, operation, chatId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Отправьте фото или картинку, чтобы воспользоваться фильтром");
            return sendMessage;
        }

        return null;
    }

    private SendMediaGroup preparePhotoMessage(List<String> localPath, ImageOperation operation, String chatId) throws Exception {
        SendMediaGroup mediaGroup = new SendMediaGroup();
        ArrayList<InputMedia> medias = new ArrayList<>();
        for (String path : localPath) {
            InputMedia inputMedia = new InputMediaPhoto();
            PhotoMessageUtils.processingImage(path, operation );
            inputMedia.setMedia(new java.io.File(path), "path");
            medias.add(inputMedia);
        }
        mediaGroup.setMedias(medias);
        mediaGroup.setChatId(chatId);
        return mediaGroup;
    }
//    private ReplyKeyboardMarkup getKeyBoard(){
//        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//        ArrayList<KeyboardRow> allKeyboardRows = new ArrayList<>();
//        allKeyboardRows.addAll(getKeybadRow(BotCommonCommands.class));
//        allKeyboardRows.addAll(getKeybadRow(FilterOperations.class));
//        replyKeyboardMarkup.setKeyboard(allKeyboardRows);
//        replyKeyboardMarkup.setOneTimeKeyboard(true);
//        return replyKeyboardMarkup;
//    }

    private ArrayList<KeyboardRow> getKeybadRow(Class someClass) {
        Method[] classsMethods = someClass.getDeclaredMethods();
        ArrayList<AppBotCommand> commands = new ArrayList<>();
        for (Method method : classsMethods) {
            if (method.isAnnotationPresent(AppBotCommand.class)) {
                commands.add(method.getAnnotation(AppBotCommand.class));
            }
        }
        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        int columnCount = 3;
        int rowCount = commands.size() / columnCount + ((commands.size() % columnCount != 0) ? 1 : 0);
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            KeyboardRow row = new KeyboardRow();
            for (int columnIndex = 0; columnIndex < 3; columnIndex++) {
                int index = rowIndex * columnCount + columnIndex;
                if (index >= commands.size()) continue;
                AppBotCommand command = commands.get(rowIndex * columnCount + columnIndex);
                KeyboardButton keyboardButton = new KeyboardButton(command.name());
                row.add(keyboardButton);
            }
            keyboardRows.add(row);
        }
        return keyboardRows;
    }
}
