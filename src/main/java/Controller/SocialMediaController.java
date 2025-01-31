package Controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    MessageService messageService;
    AccountService accountService;

    public SocialMediaController() {
        this.messageService = new MessageService();
        this.accountService = new AccountService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::createAccountHandler);
        app.post("/login", this::accountLoginHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesFromSenderHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);

        return app;
    }

    /**
     * Uses the service class to create a new account. Specifically, maps the request body to the Account class and passes it in
     * to the createAccount method. If the createAccount method returns null, sets status to 400 (client error)
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void createAccountHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        Account addedAccount = accountService.createAccount(account);

        if(addedAccount == null) {
            context.status(400);
        } else {
            context.json(mapper.writeValueAsString(addedAccount));
        }
    }

    /**
     * Logs in using the account information from the request body. Specifically, maps the request body to the Account class, then
     * passes it into the accountLogin method. If the account cannot be logged in, sets the status to 401 (Unauthorized)
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void accountLoginHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        Account addedAccount = accountService.accountLogin(account);
        
        if(addedAccount == null) {
            context.status(401);
        } else {
            context.json(mapper.writeValueAsString(addedAccount));
        }
    }


    /**
     * A handler for posting a new message to the client. The handler maps the request body to a Message and passes it
     * to the postMessage method to save it in the database. If a message cannot be posted, sets status to 400 (client error)
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void postMessageHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);
        Message addedMessage = messageService.postMessage(message);

        if(addedMessage == null) {
            context.status(400);
        } else {
            context.json(mapper.writeValueAsString(addedMessage));
        }
    }

    /**
     * A handler for getting all of the messages from the server. Uses the getAllMessages method to grab the list of Message objects
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void getAllMessagesHandler(Context context) {
        List<Message> messages = messageService.getAllMessages();
        context.json(messages);
        context.status(200);
    }

    /**
     * A handler for getting all messages from a single sender. Passes in the account_id from the request body as an argument
     * to the getMessagesFromSender method. If there's an incorrect number formatting from the request, status is set to 400 (client error)
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void getMessagesFromSenderHandler(Context context) {
        try {
            List<Message> messages = messageService.getMessagesFromSender(Integer.parseInt(context.pathParam("account_id")));
            context.json(messages);
            context.status(200);

        } catch(NumberFormatException e) {
            System.out.println(e.getMessage());
            context.status(400);
        }
    }

    /**
     * A handler for getting a single message by the message id. If a message is found with the id, includes it in the message context 
     * and sets status to 200
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void getMessageByIdHandler(Context context) {
        try {
            Message message = messageService.getMessageByIdentificationNumber(Integer.parseInt(context.pathParam("message_id")));
            
            if(message != null)
                context.json(message);

            context.status(200);
        } catch(NumberFormatException e) {
            System.out.println(e.getMessage());
            context.status(400);
        }
    }

    /**
     * A handler for updating a message based on it's id. Because the message body contains a JSON string to access the updated
     * message text, a JsonNode is needed to extract the specific message from the Json string output. If there is no message with
     * the specified id, sets the context to 400 (client error)
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void updateMessageHandler(Context context) throws JsonProcessingException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(context.body());
            Message message = messageService.updateMessage(Integer.parseInt(context.pathParam("message_id")), node.get("message_text").asText());

            if(message == null) {
                context.status(400);
            } else {
                context.json(message);
            }
        } catch(NumberFormatException e) {
            System.out.println(e.getMessage());
            context.status(400);
        }
    }

    /**
     * A handler for deleting a message from the server using it's id. If a message cannot be found, sets the status to 400 (client error)
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void deleteMessageHandler(Context context) {
        try {
            Message message = messageService.deleteMessage(Integer.parseInt(context.pathParam("message_id")));
        
            if(message != null)
                context.json(message);

            context.status(200);
        } catch(NumberFormatException e) {
            System.out.println(e.getMessage());
            context.status(400);
        }
    }
}