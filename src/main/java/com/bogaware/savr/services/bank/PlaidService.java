package com.bogaware.savr.services.bank;

import com.bogaware.savr.configurations.bank.PlaidConfiguration;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.*;
import com.plaid.client.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Service
public class PlaidService {

    private PlaidConfiguration plaidConfiguration;
    private PlaidClient plaidClient;

    @Autowired
    public PlaidService(PlaidConfiguration plaidConfiguration) {
        this.plaidConfiguration = plaidConfiguration;
        this.plaidClient = PlaidClient.newBuilder()
                .clientIdAndSecret(plaidConfiguration.getClientId(), plaidConfiguration.getSecret())
                .developmentBaseUrl()
                .build();
    }

    public String getLinkToken(String userId) {
        try {
            Response<LinkTokenCreateResponse> response = plaidClient.service()
                    .linkTokenCreate(new LinkTokenCreateRequest(new LinkTokenCreateRequest.User(userId), "Frugal", Arrays.asList("auth", "transactions"), Arrays.asList("US"), "en")).execute();
            if (response.isSuccessful()) {
                return response.body().getLinkToken();
            }
            else {
                System.out.println(response.errorBody().string());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAccessToken(String publicToken) {
        try {
            Response<ItemPublicTokenExchangeResponse> response = plaidClient.service()
                    .itemPublicTokenExchange(new ItemPublicTokenExchangeRequest(publicToken)).execute();
            if (response.isSuccessful()) {
                return response.body().getAccessToken();
            }
            else {
                System.out.println(response.errorBody().string());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AccountsGetResponse getAccounts(String accessToken) {
        try {
            Response<AccountsGetResponse> response = plaidClient.service()
                    .accountsGet(
                            new AccountsGetRequest(accessToken)
                    ).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            else {
                throw new Exception(response.errorBody().string());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AccountsBalanceGetResponse getAccountsByIds(String accessToken, List<String> accountIds) {
        try {
            Response<AccountsBalanceGetResponse> response = plaidClient.service()
                    .accountsBalanceGet(
                            new AccountsBalanceGetRequest(accessToken).withAccountIds(accountIds)
                    ).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            else {
                throw new Exception(response.errorBody().string());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public TransactionsGetResponse getTransactions(String accessToken, Calendar startDate, Calendar endDate) {
        try {
            Response<TransactionsGetResponse> response = plaidClient.service()
                    .transactionsGet(new TransactionsGetRequest(accessToken,
                            startDate.getTime(),
                            endDate.getTime()).withCount(250)).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            else {
                throw new Exception(response.errorBody().string());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
