package Service;

import Model.Account;

import DAO.AccountDAO;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account createAccount(Account account) {
        if(account.getPassword().length() < 4 || account.getUsername().isBlank()) { // validation for the password and the username
            return null;
        }

        return (this.accountDAO.getUsernameCount(account.getUsername()) == 0) ? this.accountDAO.registerAccount(account) : null;
    }

    public Account accountLogin(Account account) {
        return this.accountDAO.accountLogin(account.getUsername(), account.getPassword());
    }
}