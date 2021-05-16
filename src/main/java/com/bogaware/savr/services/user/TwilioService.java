package com.bogaware.savr.services.user;

import com.bogaware.savr.configurations.user.TwilioConfiguration;
import com.bogaware.savr.models.bank.PlaidTransaction;
import com.bogaware.savr.models.user.TwilioMessage;
import com.bogaware.savr.repositories.user.TwilioMessageRepository;
import com.google.gson.JsonObject;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TwilioService {

    private TwilioConfiguration twilioConfiguration;
    private TwilioMessageRepository twilioMessageRepository;
    private CommandService commandService;

    @Autowired
    public TwilioService(TwilioConfiguration twilioConfiguration,
                         TwilioMessageRepository twilioMessageRepository,
                         CommandService commandService) {
        this.twilioConfiguration = twilioConfiguration;
        Twilio.init(twilioConfiguration.getAccoundSid(), twilioConfiguration.getAuthToken());
        this.twilioMessageRepository = twilioMessageRepository;
        this.commandService = commandService;
    }

    public TwilioMessage twilioMessageBuilder(String toPhoneNumber, String message) {
        return new TwilioMessage(java.util.UUID.randomUUID().toString().toUpperCase(),
                toPhoneNumber,
                twilioConfiguration.getPhoneNumber(),
                false,
                message,
                new java.sql.Timestamp(new java.util.Date().getTime()));
    }

    public void sendMessage(TwilioMessage twilioMessage) {
        Message message = Message.creator(new PhoneNumber(twilioMessage.getToPhoneNumber()),
                new PhoneNumber(twilioConfiguration.getPhoneNumber()),
                twilioMessage.getBody()).create();
        saveTwilioMessage(twilioMessage);
    }

    public void sendMessageNoLog(TwilioMessage twilioMessage) {
        Message message = Message.creator(new PhoneNumber(twilioMessage.getToPhoneNumber()),
                new PhoneNumber(twilioConfiguration.getPhoneNumber()),
                twilioMessage.getBody()).create();
    }

    public void handleReceivedMessage(String receivedMessageParameters) throws UnsupportedEncodingException {
        TwilioMessage twilioMessage = parseReceivedMessage(receivedMessageParameters);
        saveTwilioMessage(twilioMessage);
        if (twilioMessage.getBody().equalsIgnoreCase("Savr Stop")) {
            sendMessage(new TwilioMessage(java.util.UUID.randomUUID().toString().toUpperCase(),
                    twilioMessage.getFromPhoneNumber(),
                    twilioMessage.getToPhoneNumber(),
                    false,
                    "Savr Stopped",
                    new java.sql.Timestamp(new java.util.Date().getTime())));
            System.exit(1);
        }
        else {
            String commandResult = commandService.runCommandFromTwilioMessage(twilioMessage);
            sendMessage(new TwilioMessage(java.util.UUID.randomUUID().toString().toUpperCase(),
                    twilioMessage.getFromPhoneNumber(),
                    twilioMessage.getToPhoneNumber(),
                    false,
                    commandResult,
                    new java.sql.Timestamp(new java.util.Date().getTime())));
        }
    }

    public TwilioMessage parseReceivedMessage(String receivedMessageParameters) throws UnsupportedEncodingException {
        /*String receivedMessageParameters = "ToCountry=US&" +
                "ToState=MO&" +
                "SmsMessageSid=SM82cb2c0de08ca4357cad0b8fadb37879" +
                "&NumMedia=0&" +
                "ToCity=LILBOURN" +
                "&FromZip=85242" +
                "&SmsSid=SM82cb2c0de08ca4357cad0b8fadb37879" +
                "&FromState=AZ" +
                "&SmsStatus=received" +
                "&FromCity=QUEEN+CREEK" +
                "&Body=Test1" +
                "&FromCountry=US" +
                "&To=%2B15732276562" +
                "&ToZip=63873" +
                "&NumSegments=1" +
                "&MessageSid=SM82cb2c0de08ca4357cad0b8fadb37879" +
                "&AccountSid=AC9e514a290db6f306f4b481f5b750d6e5" +
                "&From=%2B14808885436" +
                "&ApiVersion=2010-04-01";*/
        String urlDecodedReceivedMessageParameters = URLDecoder.decode(receivedMessageParameters, "UTF-8");
        String[] parts = urlDecodedReceivedMessageParameters.split("&");
        JsonObject json = new JsonObject();
        for(String part: parts){
            String[] keyVal = part.split("="); // The equal separates key and values
            json.addProperty(keyVal[0], keyVal[1]);
        }
        return new TwilioMessage(java.util.UUID.randomUUID().toString().toUpperCase(),
                json.get("To").getAsString(),
                json.get("From").getAsString(),
                true,
                json.get("Body").getAsString(),
                new java.sql.Timestamp(new java.util.Date().getTime()));
    }

    public void sendNewTransactionsUpdate(String phoneNumber, List<PlaidTransaction> plaidTransactions) {
        boolean reachedTransactionLimit = plaidTransactions.size() > twilioConfiguration.getTransactionsToMessageLimit();
        plaidTransactions = plaidTransactions.stream().limit(twilioConfiguration.getTransactionsToMessageLimit())
                .collect(Collectors.toList());
        String resultMessage = "New Transactions\n";
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        for (PlaidTransaction plaidTransaction: plaidTransactions) {
            resultMessage += currencyFormatter.format(plaidTransaction.getAmount()) +
                    " at " +
                    truncateAndEllipsis(plaidTransaction.getMerchantName() != null ?
                            plaidTransaction.getMerchantName() : plaidTransaction.getName(),
                            twilioConfiguration.getTransactionNameContentLimit()) +
                    "\n";
        }
        if (reachedTransactionLimit) {
            resultMessage += "...";
        }
        TwilioMessage twilioMessage = new TwilioMessage(java.util.UUID.randomUUID().toString().toUpperCase(),
                phoneNumber,
                twilioConfiguration.getPhoneNumber(),
                false,
                resultMessage,
                new java.sql.Timestamp(new java.util.Date().getTime()));
        sendMessage(twilioMessage);
    }

    public void saveTwilioMessage(TwilioMessage twilioMessage) {
        twilioMessageRepository.save(twilioMessage);
    }

    public String truncateAndEllipsis(String str, int maxLength) {
        if (str.length() < maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 1) + "...";
    }
}
