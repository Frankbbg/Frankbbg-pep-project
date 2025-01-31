package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Message;
import Util.ConnectionUtil;

/***
 * class for managing access to the Message table in the database
 */
public class MessageDAO {
    /***
     * Gets the number of senders(accounts) for a specified account_id (AKA posted_by)
     * @param posted_by the account id of the message sender
     * @return an integer representing the number of matching senders
     */
    public int getPosterIdCount(int posted_by) {
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT COUNT(account_id) FROM account WHERE account_id = ?";

        try {
            //pass the SQL statement into PreparedStatement for SQL Injection protection
            PreparedStatement cleanStatement = connection.prepareStatement(sql);

            //set the empty placeholder value
            cleanStatement.setInt(1, posted_by);

            //get the resulting rows from the query execution
            ResultSet result = cleanStatement.executeQuery();

            //go to the first row
            result.next();

            int finalResult = result.getInt(1);

            connection.close();

            return finalResult;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return 0;
    }

    /***
     * Gets the number of message ids using a message id as an identifier
     * @param message_id the message identifcation number
     * @return the amount of messages with a specified id
     */
    public int getMessageIdCount(int message_id) {
        Connection connection = ConnectionUtil.getConnection();
        String validationSql = "SELECT COUNT(message_id) FROM message WHERE message_id = ?";

        try {
            //pass the SQL statement into PreparedStatement for SQL Injection protecti
            PreparedStatement cleanValidationStatement = connection.prepareStatement(validationSql);

            //set the empty placeholder values
            cleanValidationStatement.setInt(1, message_id);

            //get the resulting rows from the query execution
            ResultSet validationResult = cleanValidationStatement.executeQuery();

            //go to the first row
            validationResult.next();

            int finalResult = validationResult.getInt(1);

            connection.close();

            return finalResult;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return 0;
    }

    /***
     * Inserts a message into the database using prepared statements for SQL injection protection.
     * @param message The message to insert
     * @return The inserted message as a Message object
     */
    public Message insertMessage(Message message) {
        Connection connection = ConnectionUtil.getConnection();
        String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";

        try {
            //pass the SQL statement into PreparedStatement for SQL Injection protection
            PreparedStatement cleanStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            //set the empty placeholder values
            cleanStatement.setInt(1, message.getPosted_by());
            cleanStatement.setString(2, message.getMessage_text());
            cleanStatement.setLong(3, message.getTime_posted_epoch());

            //get the resulting rows from the query execution
            cleanStatement.executeUpdate();
            ResultSet resultRow = cleanStatement.getGeneratedKeys();

            //go to the first row
            resultRow.next();

            Message insertedMessage = new Message(resultRow.getInt(1), message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());

            connection.close();

            return insertedMessage;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /***
     * Gets all messages from the database
     * @return List<Message> list of messages
     */
    public List<Message> getAllMessages() {
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message";
        List<Message> allMessages = new ArrayList<Message>();

        try {
            //pass the SQL statement into PreparedStatement for SQL Injection protection
            PreparedStatement cleanStatement = connection.prepareStatement(sql);

            //get the resulting rows from the query execution
            ResultSet resultRows = cleanStatement.executeQuery();

            while(resultRows.next()) { // iterate over the rows
                allMessages.add(new Message(resultRows.getInt("message_id"), resultRows.getInt("posted_by"), resultRows.getString("message_text"), resultRows.getLong("time_posted_epoch")));
            }

            connection.close();

            return allMessages;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /***
     * Gets all the messages that a specified sender has sent. Uses prepared statements to project from SQL injection
     * @param sender_id the account_id of the sender
     * @return A List<Message> list of all messages that the sender has sent
     */
    public List<Message> getMessagesBySenderId(int sender_id) {
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE posted_by = ?";
        List<Message> messageList = new ArrayList<>();
                    
        try {
            //pass the SQL statement into PreparedStatement for SQL Injection protection
            PreparedStatement cleanStatement = connection.prepareStatement(sql);

            //set the empty placeholder values
            cleanStatement.setInt(1, sender_id);

            //get the resulting rows from the query execution
            ResultSet resultRows = cleanStatement.executeQuery();

            while(resultRows.next()) { // iterate over the rows
                messageList.add(new Message(resultRows.getInt("message_id"), resultRows.getInt("posted_by"), resultRows.getString("message_text"), resultRows.getLong("time_posted_epoch")));
            }

            connection.close();

            return messageList;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /***
     * Gets a specific message based on its identification number. Uses prepared statements to protect against SQL injection
     * @param id the identification number of the message
     * @return The message as a Message object
     */
    public Message getMessageById(int id) {
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE message_id = ?";

        try {
            PreparedStatement cleanStatement = connection.prepareStatement(sql);

            cleanStatement.setInt(1, id);

            ResultSet resultRow = cleanStatement.executeQuery();

            resultRow.next();

            Message message = new Message(resultRow.getInt("message_id"), resultRow.getInt("posted_by"), resultRow.getString("message_text"), resultRow.getLong("time_posted_epoch"));

            connection.close();

            return message;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /***
     * Updates a message in the database using its id and a new message string. Uses prepared statements to protect from SQL injection
     * @param id the identification number of the message
     * @param newMessage The message text to update the message with
     * @return the newly updated message as a Message object
     */
    public Message updateMessage(int id, String newMessage) {
        Connection connection = ConnectionUtil.getConnection();
        String sqlUpdate = "UPDATE message SET message_text = ? WHERE message_id = ?";
        String sqlQuery = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE message_id = ?";

        try {
            PreparedStatement cleanUpdateStatement = connection.prepareStatement(sqlUpdate);

            cleanUpdateStatement.setString(1, newMessage);
            cleanUpdateStatement.setInt(2, id);

            cleanUpdateStatement.executeUpdate();

            PreparedStatement cleanQueryStatement = connection.prepareStatement(sqlQuery);

            cleanQueryStatement.setInt(1, id);

            ResultSet resultRow = cleanQueryStatement.executeQuery();

            resultRow.next();

            Message updatedMessage = new Message(resultRow.getInt("message_id"), resultRow.getInt("posted_by"), resultRow.getString("message_text"), resultRow.getLong("time_posted_epoch"));

            connection.close();

            return updatedMessage;
            
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /***
     * deletes a message from the database, then returns it. Uses prepared statements to protect from SQL injection
     * @param id the identification number of the message to be deleted
     * @return The deleted message
     */
    public Message deleteMessage(int id) {
        Connection connection = ConnectionUtil.getConnection();
        String sqlQuery = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE message_id = ?";
        String sqlDelete = "DELETE FROM message WHERE message_id = ?";

        try {
            
            PreparedStatement cleanSelectStatement = connection.prepareStatement(sqlQuery);
            PreparedStatement cleanDeleteStatement = connection.prepareStatement(sqlDelete);

            cleanSelectStatement.setInt(1, id);
            cleanDeleteStatement.setInt(1, id);

            ResultSet deletedRow = cleanSelectStatement.executeQuery();

            deletedRow.next();

            Message deletedMessage = new Message(deletedRow.getInt("message_id"), deletedRow.getInt("posted_by"), deletedRow.getString("message_text"), deletedRow.getLong("time_posted_epoch"));

            cleanDeleteStatement.executeUpdate();

            connection.close();

            return deletedMessage;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}