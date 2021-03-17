package com.bogaware.savr.services;

import com.bogaware.savr.configurations.TwilioConfiguration;
import com.bogaware.savr.models.PlaidAccount;
import com.bogaware.savr.models.PlaidTransaction;
import com.bogaware.savr.models.TwilioMessage;
import com.bogaware.savr.models.User;
import com.bogaware.savr.repositories.PlaidAccountRepository;
import com.bogaware.savr.repositories.PlaidTransactionRepository;
import com.bogaware.savr.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.List;

@Service
public class CommandService {

    @Autowired
    TwilioConfiguration twilioConfiguration;

    private UserRepository userRepository;
    private PlaidAccountRepository plaidAccountRepository;
    private PlaidTransactionRepository plaidTransactionRepository;

    @Autowired
    public CommandService(UserRepository userRepository,
                          PlaidAccountRepository plaidAccountRepository,
                          PlaidTransactionRepository plaidTransactionRepository) {
        this.userRepository = userRepository;
        this.plaidAccountRepository = plaidAccountRepository;
        this.plaidTransactionRepository = plaidTransactionRepository;
    }

    public String runCommand(String command) {
        return executeCommand(command, null);
    }

    public String runCommandFromTwilioMessage(TwilioMessage twilioMessage) {
        User userContext = userRepository.findByPhoneNumber(twilioMessage.getFromPhoneNumber());
        return executeCommand(twilioMessage.getBody(), userContext);
    }

    public String executeCommand(String command, User user) {
        if (command.equalsIgnoreCase("Hey")) {
            return "Hello, I'm Savr!";
        }
        else if (command.equalsIgnoreCase("Bal")) {
            return getBalancesByUser(user);
        }
        else if (command.equalsIgnoreCase("Today")) {
            return getLatest5TransactionsByUser(user);
        }
        return null;
    }

    public String getBalancesByUser(User user) {
        if (user == null) {
            return "You are not in the system";
        }
        List<PlaidAccount> plaidAccounts = plaidAccountRepository.findAllByUserId(user.getId());
        String resultMessage = "Balances\n";
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        for (PlaidAccount plaidAccount: plaidAccounts) {
            resultMessage += currencyFormatter.format(plaidAccount.getCurrentBalance()) +
                    " - " + plaidAccount.getName() + "\n";
        }
        return resultMessage;
    }

    public String getLatest5TransactionsByUser(User user) {
        if (user == null) {
            return "You are not in the system";
        }
        List<PlaidTransaction> plaidTransactions = plaidTransactionRepository.findAllLatest5ByUserId(user.getId());
        String resultMessage = "Latest 5 Transactions\n";
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        for (PlaidTransaction plaidTransaction: plaidTransactions) {
            resultMessage += currencyFormatter.format(plaidTransaction.getAmount()) +
                    " at " +
                    truncateAndEllipsis(plaidTransaction.getMerchantName() != null ?
                                    plaidTransaction.getMerchantName() : plaidTransaction.getName(),
                            twilioConfiguration.getTransactionNameContentLimit()) +
                    "\n";
        }
        return resultMessage;
    }

    public String truncateAndEllipsis(String str, int maxLength) {
        if (str.length() < maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 1) + "...";
    }
}
