package Service;

import Model.Message;

import java.util.List;

import DAO.MessageDAO;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public Message postMessage(Message message) {
        if(message.getMessage_text().length() > 255 || message.getMessage_text().isBlank()) {
            return null;
        }

        return (this.messageDAO.getPosterIdCount(message.getPosted_by()) > 0) ? this.messageDAO.insertMessage(message) : null;
    }

    public List<Message> getAllMessages() {
        return this.messageDAO.getAllMessages();
    }

    public List<Message> getMessagesFromSender(int sender_id) {
        return this.messageDAO.getMessagesBySenderId(sender_id);
    }

    public Message getMessageByIdentificationNumber(int id) {
        return this.messageDAO.getMessageById(id);
    }

    public Message updateMessage(int id, String newMessage) {
        if(newMessage.length() > 255 || newMessage.isBlank()) {
            return null;
        }

        return (this.messageDAO.getMessageIdCount(id) > 0) ? this.messageDAO.updateMessage(id, newMessage) : null;
    }

    public Message deleteMessage(int id) {
        return (this.messageDAO.getMessageIdCount(id) > 0) ? this.messageDAO.deleteMessage(id) : null;
    }
}