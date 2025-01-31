package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.Account;
import Util.ConnectionUtil;

/***
 * class for managing access to the Account table in the database
 */
public class AccountDAO {

    /***
     * Finds the number of usernames in the database
     * @param username the username as a String
     * @return the number of matching usernames
     */
    public int getUsernameCount(String username) {
        // connect to the database
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT COUNT(username) FROM account WHERE username = ?";

        try {
            //Check if name is unique

            //pass the SQL statement into PreparedStatement for SQL Injection protection
            PreparedStatement cleanValidationStatement = connection.prepareStatement(sql);

            //set the empty placeholder value
            cleanValidationStatement.setString(1, username);

            //get the resulting rows from the query execution
            ResultSet usernames = cleanValidationStatement.executeQuery();

            //go to the first row
            usernames.next();

            int finalResult = usernames.getInt(1);

            connection.close();

            return finalResult;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return 0;
    }

    /***
     * Logs into an existing account. If no account is found with the username and password, then returns null. Uses prepared statements to protect from SQL injection.
     * @param username the username of the account as a String
     * @param password the password of the account as a String
     * @return The Logged in account
     */
    public Account accountLogin(String username, String password) {
        // connect to the database
        Connection connection = ConnectionUtil.getConnection();
        String sql = "SELECT account_id, username, password FROM account WHERE username = ? AND password = ?";

        try {
            //pass the SQL statement into PreparedStatement for SQL Injection protection
            PreparedStatement cleanValidationStatement = connection.prepareStatement(sql);

            //set the empty placeholder values
            cleanValidationStatement.setString(1, username);
            cleanValidationStatement.setString(2, password);

            //get the resulting rows from the query execution
            ResultSet resultRow = cleanValidationStatement.executeQuery();

            //go to the first row
            resultRow.next();

            // save the row as an Account object
            Account loggedInAccount = new Account(resultRow.getInt("account_id"), resultRow.getString("username"), resultRow.getString("password"));

            connection.close();

            return loggedInAccount;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    /***
     * Registers a new Account to the database
     * @param account the new account as an Account object
     * @return the registered account as an Account object
     */
    public Account registerAccount(Account account) {
        // connect to the database
        Connection connection = ConnectionUtil.getConnection();
        String sql = "INSERT INTO account (username, password) VALUES (?, ?)";

        try {
            //pass the SQL statement into PreparedStatement for SQL Injection protection
            PreparedStatement cleanStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            //set the empty placeholder values
            cleanStatement.setString(1, account.getUsername());
            cleanStatement.setString(2, account.getPassword());

            //get the resulting rows from the query execution
            cleanStatement.executeUpdate();
            ResultSet resultRow = cleanStatement.getGeneratedKeys();

            //go to the first row
            resultRow.next();

            Account newAccount = new Account(resultRow.getInt(1), account.getUsername(), account.getPassword());

            connection.close();

            return newAccount;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return null;
    }
}