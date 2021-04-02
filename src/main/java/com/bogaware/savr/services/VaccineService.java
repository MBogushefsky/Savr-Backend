package com.bogaware.savr.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Service
public class VaccineService {

    private String userToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMjZmYzM0Y2YtODY3NS00MDE5LWE3NjQtNTBjNjU0NDE0ZjlmIiwibmFtZUlEIjoiMjZmYzM0Y2YtODY3NS00MDE5LWE3NjQtNTBjNjU0NDE0ZjlmIiwibmFtZUlERm9ybWF0IjoidXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOm5hbWVpZC1mb3JtYXQ6cGVyc2lzdGVudCIsInVzZXIiOnsiaWQiOjE5NTIxNjYsImNyZWF0ZWREYXRlIjoiMjAyMS0wMy0yM1QwMDoyOTowNS4yODNaIiwidXBkYXRlZERhdGUiOiIyMDIxLTAzLTI0VDAyOjIxOjUzLjE2OVoiLCJ2ZXJzaW9uIjozLCJjcmVhdGVkQnkiOjAsInVwZGF0ZWRCeSI6MCwidGl0bGUiOm51bGwsImZpcnN0TmFtZSI6Ik1pdGNoZWxsIiwibWlkZGxlTmFtZSI6bnVsbCwibGFzdE5hbWUiOiJCb2d1c2hlZnNreSIsImVtYWlsIjoibWJvZ3VzaGVmc2t5QGdtYWlsLmNvbSIsImZpcmViYXNlVXNlcklkIjpudWxsLCJldGhuaWNpdHkiOiJOb24gSGlzcGFuaWMvTGF0aW5vIiwiYWFkVXNlcklkIjoiMjZmYzM0Y2YtODY3NS00MDE5LWE3NjQtNTBjNjU0NDE0ZjlmIiwiZG9iIjoiMTk5NS0wMS0yNSIsImdlbmRlciI6Ik1hbGUiLCJndWFyZGlhbk5hbWUiOm51bGwsInByb2ZpbGUiOiJQYXRpZW50IiwicmFjZSI6IldoaXRlIiwibGFuZ3VhZ2VQcmVmZXJlbmNlIjoiZW4tVVMiLCJsYW5ndWFnZXNTcG9rZW4iOm51bGwsInByaW1hcnlQaG9uZU51bWJlciI6Iig0ODApIDg4OC01NDM2Iiwib3RoZXJQaG9uZU51bWJlciI6bnVsbCwiQWRkcmVzczEiOm51bGwsIkFkZHJlc3MyIjoiMzI0MyBFIENlZGFyd29vZCBMbiIsInN0cmVldCI6bnVsbCwic3RhdGUiOiJBWiIsImNpdHkiOiJQaG9lbml4IiwiY291bnRyeSI6bnVsbCwiemlwY29kZSI6Ijg1MDQ4IiwibWFyaXRhbFN0YXR1cyI6IlNpbmdsZSIsImNvdW50eSI6Ik1hcmljb3BhIiwiZ3VhcmRpYW5GaXJzdE5hbWUiOiIiLCJndWFyZGlhbkxhc3ROYW1lIjoiIiwiaGF2ZUluc3VyYW5jZSI6ZmFsc2UsInNtc0NvbnNlbnQiOnRydWUsImlzRGlzYWJsZWQiOmZhbHNlLCJvY2N1cGF0aW9uIjoiT3RoZXIgb2NjdXBhdGlvbiIsIm91IjpudWxsfSwiaWF0IjoxNjE3MTY0MTc2LCJleHAiOjE2MTcxNjUwNzZ9.ecVCrH2Ijmw5aEbDrxGjG8vdFoygY4lmwh8cgk-vOVY";
    private boolean gettingNewToken = false;
    private boolean isTokenInvalid = true;
    private boolean isAvailableAppointment = false;
    private boolean loggedIn = false;
    private boolean selectedAppointment = false;

    private int attemptCounter = 0;

    private int callCounter = 0;
    private int monthNum = 4;
    private int dayOfMonth = 1;
    private String searchDate = "Fri%20Apr%2030%202021%2016:46:34%20GMT-0700";
    private int[] eventIds = new int[] { 838, 1147 };//, 368 };

    private HttpClient client = HttpClients.custom()
            .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                            .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                            .build()
                    )
            ).build();

    private HashSet<Integer> slotFullIds = new HashSet<>();

    ObjectMapper objectMapper = new ObjectMapper();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    WebDriver browser;
    WebDriverWait wait;

    @Autowired
    private RestTemplate restTemplate;

    public VaccineService() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    }

    //@Scheduled(fixedDelay = 1)
    public void getVaccineAppointment() throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, ParseException, InterruptedException {

        if (isTokenInvalid && !gettingNewToken) {
            loggedIn = false;
            gettingNewToken = true;
            String loginUrl = getLoginUrl();
            if (loginUrl != null) {
                System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
                browser = new ChromeDriver();
                browser.get(loginUrl);
                browser.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                WebElement usernameInput = browser.findElement(By.id("signInName"));
                usernameInput.sendKeys("mbogushefsky@gmail.com");
                WebElement passwordInput = browser.findElement(By.id("password"));
                passwordInput.sendKeys("Xboxlive880");
                WebElement signInButton = browser.findElement(By.id("next"));
                signInButton.click();
                WebElement body = browser.findElement(By.cssSelector("body"));
                //System.out.println("BODY: " + body.getAttribute("innerHTML"));
                JavascriptExecutor js = ((JavascriptExecutor) browser);
                wait = new WebDriverWait(browser, 20);
                //wait.until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
                WebElement we = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".home-action")));
                userToken = (String) js.executeScript(String.format("return window.localStorage.getItem('%s');", "TOKEN"));
                System.out.println("TOKEN: " + userToken);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".feature-buttons.resc .text")));
                List<WebElement> buttonsOnHome = browser.findElements(By.cssSelector(".feature-buttons.resc .text"));
                for (WebElement button : buttonsOnHome) {
                    System.out.println("INNER TEXT: " + button.getAttribute("innerText"));
                    if (button.getAttribute("innerText").contains("Reschedule Appointment")) {
                        button.click();
                    }
                }
                loggedIn = true;
                /*if (isAvailableAppointment) {
                    WebElement radioButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".mat-radio-button .mat-radio-label .mat-radio-container input")));
                    System.out.println(radioButton.getAttribute("value"));
                }*/
                isTokenInvalid = false;
            }
            gettingNewToken = false;
        }
        else {
            for (int id : eventIds) {
                //todayAnd5DaysOutGetVaccineAppointment(id);
                /*getEarliestVaccineAppointmentSlot();
                callCounter++;
                System.out.println("Checked (Call " + callCounter + ", ID " + id + ") (" + attemptCounter + " Failed on Slot Full)");*/
            }
        }
        if (attemptCounter > 10000) {
            System.exit(1);
        }
        if (!selectedAppointment) {
            getEarliestVaccineAppointmentSlot();
            callCounter++;
            System.out.println("Checked (Call " + callCounter + ") (" + attemptCounter + " Failed on Slot Full)");
        }
    }

    //@Scheduled(fixedDelay = 1000)
    private void checkForAppointments() throws InterruptedException {
        if (!isAvailableAppointment || !loggedIn || selectedAppointment) {
            return;
        }
        selectedAppointment = true;
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".fieldbox[formcontrolname='zipCode']")));
        WebElement inputZip = browser.findElement(By.cssSelector(".fieldbox[formcontrolname='zipCode']"));
        inputZip.clear();
        inputZip.sendKeys("85296");
        System.out.println("1");
        //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loader-md")));
        WebElement timeSlot = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".time-card .dropdownbox:not(.hidden)")));
        timeSlot.click();
        System.out.println("2");
        System.out.println("TIME SLOT: " + timeSlot.getAttribute("innerHTML"));
        WebElement bodyEle = browser.findElement(By.cssSelector("body"));
        System.out.println("Body: " + bodyEle.getAttribute("innerHTML"));
        WebElement timeSlotRadio = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".mob-table-hor table mat-radio-button")));
        timeSlotRadio.click();
        System.out.println("3");
        Thread.sleep(500);
        //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".mat-spinner")));
        System.out.println("4");
        JavascriptExecutor jse = (JavascriptExecutor)browser;
        Thread.sleep(1500);
        System.out.println("5");
        jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        WebElement bookAppointmentButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".button-wrapper .next-button")));
        bookAppointmentButton.click();
        System.out.println("6");
        //jse.executeScript("arguments[0].click();", bookAppointmentButton);
        WebElement confirmBookButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("mat-dialog-container button.proceed")));
        confirmBookButton.click();
    }

    public static ExpectedCondition<Boolean> absenceOfElementLocated(
            final By locator) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    driver.findElement(locator);
                    return false;
                } catch (NoSuchElementException e) {
                    return true;
                } catch (StaleElementReferenceException e) {
                    return true;
                }
            }

            @Override
            public String toString() {
                return "element to not being present: " + locator;
            }
        };
    }

    private String getLoginUrl() throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        trustEveryone();
        HttpClient client = HttpClients.custom()
                .disableRedirectHandling()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                                .build()
                        )
                ).build();
        HttpGet request = new HttpGet("https://api-server-podvaccine.azdhs.gov/login/");
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader
                (new InputStreamReader(
                        response.getEntity().getContent()));
        for (Header header : response.getAllHeaders()) {
            if (header.getName().equalsIgnoreCase("location")) {
                return header.getValue();
            }
        }
        return null;
    }

    private void getEarliestVaccineAppointmentSlot() throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, InterruptedException {
        long startTime = Calendar.getInstance().getTime().getTime();
        trustEveryone();
        HttpGet request = new HttpGet("https://api-server-podvaccine.azdhs.gov/vaccination-event/patient-view?distance=50&zipCode=85296&date=2021-04-01&groupIds=1&priorityString=Adults_16_older,Adults_18_older,Adults_20_older,Adults_25_older&user=1952166");
        request.setHeader("Authorization", userToken);
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader
                (new InputStreamReader(
                        response.getEntity().getContent()));

        String appendLine = "";
        String line = "";
        while ((line = rd.readLine()) != null) {
            appendLine += line;
        }
        JsonNode responseNode = objectMapper.readTree(appendLine);
        if (responseNode.has("statusCode") && responseNode.get("statusCode").asInt() == 401) {
            userToken = null;
            authorize();
            isTokenInvalid = true;
            return;
        }
        JsonNode appointment = responseNode.get("earliest");
        //System.out.println("Earliest Appointment (Call " + callCounter + " which is a " + (Calendar.getInstance().getTime().getTime() - startTime) +" millisecond operation) " + appointment.toString());

        if (appointment.toString().equalsIgnoreCase("null") || appointment.toString() == null || appointment == null) {
            return;
        }
        //System.out.println("VACCINE: " + appointment.toString());
        boolean isGoodEventId = false;
        for (int eventId : eventIds) {
            if (eventId == appointment.get("slot_eventId").asInt()) {
                isGoodEventId = true;
            }
        }
        if (!isGoodEventId) {
            return;
        }
        Calendar currentTime = Calendar.getInstance();
        long startInstant = Instant.parse("2021-03-25T00:00:00.000Z").toEpochMilli();
        //System.out.println(startInstant+ " : " + currentTime.getTimeInMillis() + " diff " + (startInstant - currentTime.getTimeInMillis()));
        String startTimeString = appointment.get("slot_startDate").asText();
        //System.out.println("Found on " + appointment.get("slot_startDate").asText());

        Calendar startTimeCalendar = Calendar.getInstance();
        System.out.println("Appointment on day found at " + startTimeCalendar.getTime().getHours() + ":" + startTimeCalendar.getTime().getMinutes());
        if (true || (startInstant - currentTime.getTimeInMillis()) >= 30*60*1000) {
            //System.out.println("Appointment on day at " + startTimeCalendar.getTime().getHours() + ":" + startTimeCalendar.getTime().getMinutes());
            HttpPost request2 = new HttpPost("https://api-server-podvaccine.azdhs.gov/appointment/booking");
            String json = "{\n" +
                    "  \"contactId\": 1952166,\n" +
                    "  \"slotId\": " + appointment.get("slot_id").asInt() + ",\n" +
                    "  \"insuranceId\": 1008849,\n" +
                    "  \"vaccineGroupIds\": \"1\",\n" +
                    "  \"priorityGroups\": \"Adults_16_older,Adults_18_older,Adults_20_older,Adults_25_older\",\n" +
                    "  \"isFollowUp\": false,\n" +
                    "  \"priorityAnswers\": [\n" +
                    "    {\n" +
                    "        \"isRequired\": true,\n" +
                    "        \"questionId\": 1,\n" +
                    "        \"question\": \"Do you live in a shelter or other congregate living settings with vulnerable populations?\",\n" +
                    "        \"type\": \"Radio\",\n" +
                    "        \"answer\": \"false\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"isRequired\": true,\n" +
                    "        \"questionId\": 2,\n" +
                    "        \"question\": \"Are you immunocompromised or do you have an underlying medical condition not limited to COPD, heart disease, diabetes, or chronic kidney disease?\",\n" +
                    "        \"type\": \"Radio\",\n" +
                    "        \"answer\": \"false\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            request2.setHeader("Authorization", userToken);
            StringEntity entity = new StringEntity(json);
            //System.out.println("JSON " + json);
            request2.setEntity(entity);
            HttpResponse response2 = client.execute(request2);
            BufferedReader rd2 = new BufferedReader
                    (new InputStreamReader(
                            response2.getEntity().getContent()));

            String appendLine2 = "";
            String line2 = "";
            while ((line2 = rd2.readLine()) != null) {
                appendLine2 += line2;
            }

            System.out.println("STATUS: " + response2.getStatusLine());
            System.out.println("Found: " + appendLine2);
            if (objectMapper.readTree(appendLine2).get("message").asText().equalsIgnoreCase("SLOT_FULL")) {
                attemptCounter++;
                if (isAvailableAppointment == false) {
                    isAvailableAppointment = true;
                    checkForAppointments();
                }
                System.out.println("Missed: " + (Calendar.getInstance().getTime().getTime() - startTime) +" millisecond operation) ");
                return;
            }
            else {
                System.exit(1);
            }
        }
    }

    private void getEarliestVaccineAppointmentSlotFromMonthly(int id) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ParseException {
        long startTime = Calendar.getInstance().getTime().getTime();
        trustEveryone();
        HttpGet request = new HttpGet("https://api-server-podvaccine.azdhs.gov/appointment-slot/monthly-available/" + id + "?searchDate=" + searchDate);
        request.setHeader("Authorization", userToken);
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader
                (new InputStreamReader(
                        response.getEntity().getContent()));

        String appendLine = "";
        String line = "";
        while ((line = rd.readLine()) != null) {
            appendLine += line;
        }
        JsonNode responseNode = objectMapper.readTree(appendLine);
        if (responseNode.has("statusCode") && responseNode.get("statusCode").asInt() == 401) {
            userToken = null;
            authorize();
            isTokenInvalid = true;
            return;
        }
        ArrayNode appointmentDates = (ArrayNode) responseNode.get("availableDates");
        //System.out.println("Earliest Appointment (Call " + callCounter + " which is a " + (Calendar.getInstance().getTime().getTime() - startTime) +" millisecond operation) " + appointmentDates.toString());

        if (appointmentDates.toString().equalsIgnoreCase("null") || appointmentDates.toString() == null || appointmentDates == null) {
            return;
        }
        Calendar currentTime = Calendar.getInstance();
        List<String> datesToCheck = new ArrayList<>();
        for (JsonNode date : appointmentDates) {
            System.out.println(date.asText());
            String stringdate = date.asText();
            Date dateObj = df.parse(stringdate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateObj);
            //System.out.println("Month " + cal.getTime().getMonth() + ":" + currentTime.getTime().getMonth());
            //System.out.println("Date " + cal.getTime().getDate() + ":" + currentTime.getTime().getDate());
            //if ((cal.getTime().getMonth() >= currentTime.getTime().getMonth()) && (cal.getTime().getDate() >= currentTime.getTime().getDate())) {
                datesToCheck.add(stringdate);
            //}
        }

        //System.out.println("VACCINE: " + appointmentDates.toString());
        for (String dateToCheck : datesToCheck) {
            System.out.println("DATE TO CHECK: " + dateToCheck);
            HttpGet request3 = new HttpGet("https://api-server-podvaccine.azdhs.gov/appointment-slot/event/" + id + "?date=" + dateToCheck);
            request3.setHeader("Authorization", userToken);
            HttpResponse response3 = client.execute(request3);
            BufferedReader rd3 = new BufferedReader
                    (new InputStreamReader(
                            response3.getEntity().getContent()));

            String appendLine3 = "";
            String line3 = "";
            while ((line3 = rd3.readLine()) != null) {
                appendLine3 += line3;
            }
            //System.out.println("RESULT: " + appendLine3);
            JsonNode responseNode3 = objectMapper.readTree(appendLine3);
            if (responseNode3.has("statusCode") && responseNode3.get("statusCode").asInt() == 401) {
                userToken = null;
                authorize();
                isTokenInvalid = true;
                return;
            }
            ArrayNode appointments = (ArrayNode) responseNode3;
            //System.out.println("APPOINTMENTS: " + appointments.toString());
            for (JsonNode appointment : appointments) {
                if (!slotFullIds.contains(appointment.get("id").asInt())) {
                    String startTimeString = appointment.get("startTime").asText();
                    //14:48:00
                    String[] startTimeSplit = startTimeString.split(":");
                    int startTimeHours = Integer.parseInt(startTimeSplit[0]);
                    int startTimeMinutes = Integer.parseInt(startTimeSplit[1]);
                    Calendar startTimeCalendar = Calendar.getInstance();
                    startTimeCalendar.set(Calendar.HOUR_OF_DAY, startTimeHours);
                    startTimeCalendar.set(Calendar.MINUTE, startTimeMinutes);
                    //System.out.println("AVAILABLE: " + appointment.get("id").asInt() + " slots " + appointment.get("availableAppointments").asInt());
                    //System.out.println(startTimeCalendar.getTimeInMillis() + " : " + currentTime.getTimeInMillis() + " diff " + (startTimeCalendar.getTimeInMillis() - currentTime.getTimeInMillis()));
                    if (appointment.get("availableAppointments").asInt() > 0 && (!dateToCheck.equalsIgnoreCase("2021-" + monthNum + "-" + dayOfMonth) || (dateToCheck.equalsIgnoreCase("2021-" + monthNum + "-" + dayOfMonth) && (startTimeCalendar.getTimeInMillis() - currentTime.getTimeInMillis()) >= 30*60*1000))) {
                        //System.out.println("START TIME: " + appointment.get("startTime"));
                        HttpPost request2 = new HttpPost("https://api-server-podvaccine.azdhs.gov/appointment/booking");
                        String json = "{\n" +
                                "  \"contactId\": 1952166,\n" +
                                "  \"slotId\": " + appointment.get("id").asInt() + ",\n" +
                                "  \"insuranceId\": 1008849,\n" +
                                "  \"vaccineGroupIds\": \"1\",\n" +
                                "  \"priorityGroups\": \"Adults_16_older,Adults_18_older,Adults_20_older,Adults_25_older\",\n" +
                                "  \"isFollowUp\": false,\n" +
                                "  \"priorityAnswers\": [\n" +
                                "    {\n" +
                                "        \"isRequired\": true,\n" +
                                "        \"questionId\": 1,\n" +
                                "        \"question\": \"Do you live in a shelter or other congregate living settings with vulnerable populations?\",\n" +
                                "        \"type\": \"Radio\",\n" +
                                "        \"answer\": \"false\"\n" +
                                "    },\n" +
                                "    {\n" +
                                "        \"isRequired\": true,\n" +
                                "        \"questionId\": 2,\n" +
                                "        \"question\": \"Are you immunocompromised or do you have an underlying medical condition not limited to COPD, heart disease, diabetes, or chronic kidney disease?\",\n" +
                                "        \"type\": \"Radio\",\n" +
                                "        \"answer\": \"false\"\n" +
                                "    }\n" +
                                "  ]\n" +
                                "}";
                        request2.setHeader("Authorization", userToken);
                        StringEntity entity = new StringEntity(json);
                        System.out.println("JSON " + json);
                        request2.setEntity(entity);
                        HttpResponse response2 = client.execute(request2);
                        BufferedReader rd2 = new BufferedReader
                                (new InputStreamReader(
                                        response2.getEntity().getContent()));

                        String appendLine2 = "";
                        String line2 = "";
                        while ((line2 = rd2.readLine()) != null) {
                            appendLine2 += line2;
                        }

                        System.out.println("STATUS: " + response2.getStatusLine());
                        System.out.println("Found: " + appendLine2);
                        if (objectMapper.readTree(appendLine2).get("message").asText().equalsIgnoreCase("SLOT_FULL")) {
                            attemptCounter++;
                            //slotFullIds.add(appointment.get("id").asInt());
                            return;
                        }
                        else {
                            System.exit(1);
                        }
                    }
                }
            }
        }
    }

    private void todayAnd5DaysOutGetVaccineAppointment(int id) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {
        trustEveryone();
        HttpClient client = HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                                .build()
                        )
                ).build();
        HttpGet request = new HttpGet("https://api-server-podvaccine.azdhs.gov/appointment-slot/event/" + id + "?date=2021-03-" + dayOfMonth);
        request.setHeader("Authorization", userToken);
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader
                (new InputStreamReader(
                        response.getEntity().getContent()));

        String appendLine = "";
        String line = "";
        while ((line = rd.readLine()) != null) {
            appendLine += line;
        }
        JsonNode responseNode = objectMapper.readTree(appendLine);
        if (responseNode.has("statusCode") && responseNode.get("statusCode").asInt() == 401) {
            userToken = null;
            authorize();
            return;
        }
        ArrayNode appointments = (ArrayNode) responseNode;
        Calendar currentTime = Calendar.getInstance();
        for (JsonNode appointment : appointments) {
            String startTimeString = appointment.get("startTime").asText();
            //14:48:00
            String[] startTimeSplit = startTimeString.split(":");
            int startTimeHours = Integer.parseInt(startTimeSplit[0]);
            int startTimeMinutes = Integer.parseInt(startTimeSplit[1]);
            Calendar startTimeCalendar = Calendar.getInstance();
            startTimeCalendar.set(Calendar.HOUR_OF_DAY, startTimeHours);
            startTimeCalendar.set(Calendar.MINUTE, startTimeMinutes);
            if (appointment.get("availableAppointments").asInt() > 0 && (startTimeCalendar.getTimeInMillis() - currentTime.getTimeInMillis()) >= 30*60*1000) {
                System.out.println("Appointment on day at " + startTimeCalendar.getTime().getHours() + ":" + startTimeCalendar.getTime().getMinutes());
                HttpPost request2 = new HttpPost("https://api-server-podvaccine.azdhs.gov/appointment/booking");
                String json = "{\n" +
                        "  \"contactId\": 1952166,\n" +
                        "  \"slotId\": " + appointment.get("id").asInt() + ",\n" +
                        "  \"insuranceId\": 1008849,\n" +
                        "  \"vaccineGroupIds\": \"1\",\n" +
                        "  \"priorityGroups\": \"Adults_16_older,Adults_18_older,Adults_20_older,Adults_25_older\",\n" +
                        "  \"isFollowUp\": false,\n" +
                        "  \"priorityAnswers\": [\n" +
                        "    {\n" +
                        "        \"isRequired\": true,\n" +
                        "        \"questionId\": 1,\n" +
                        "        \"question\": \"Do you live in a shelter or other congregate living settings with vulnerable populations?\",\n" +
                        "        \"type\": \"Radio\",\n" +
                        "        \"answer\": \"false\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"isRequired\": true,\n" +
                        "        \"questionId\": 2,\n" +
                        "        \"question\": \"Are you immunocompromised or do you have an underlying medical condition not limited to COPD, heart disease, diabetes, or chronic kidney disease?\",\n" +
                        "        \"type\": \"Radio\",\n" +
                        "        \"answer\": \"false\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
                request2.setHeader("Authorization", userToken);
                StringEntity entity = new StringEntity(json);
                System.out.println("JSON " + json);
                request2.setEntity(entity);
                HttpResponse response2 = client.execute(request2);
                BufferedReader rd2 = new BufferedReader
                        (new InputStreamReader(
                                response2.getEntity().getContent()));

                String appendLine2 = "";
                String line2 = "";
                while ((line2 = rd2.readLine()) != null) {
                    appendLine2 += line2;
                }

                System.out.println("STATUS: " + response2.getStatusLine());
                System.out.println("Found: " + appendLine2);
                if (objectMapper.readTree(appendLine2).get("message").asText().equalsIgnoreCase("SLOT_FULL")) {
                    return;
                }
                else {
                    System.exit(1);
                }
            }
        }
        FiveDaysOutGetVaccineAppointment(id);
    }

    private void FiveDaysOutGetVaccineAppointment(int id) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        trustEveryone();
        HttpClient client = HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                                .build()
                        )
                ).build();
        for (int i = 0; i < 5; i++) {
            HttpGet request = new HttpGet("https://api-server-podvaccine.azdhs.gov/appointment-slot/event/" + id + "?date=2021-03-" + (dayOfMonth + 1 + i));
            request.setHeader("Authorization", userToken);
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(
                            response.getEntity().getContent()));

            String appendLine = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                appendLine += line;
            }
            JsonNode responseNode = objectMapper.readTree(appendLine);
            if (responseNode.has("statusCode") && responseNode.get("statusCode").asInt() == 401) {
                userToken = null;
                authorize();
                return;
            }
            ArrayNode appointments = (ArrayNode) responseNode;
            Calendar currentTime = Calendar.getInstance();
            for (JsonNode appointment : appointments) {
                String startTimeString = appointment.get("startTime").asText();
                //14:48:00
                String[] startTimeSplit = startTimeString.split(":");
                int startTimeHours = Integer.parseInt(startTimeSplit[0]);
                int startTimeMinutes = Integer.parseInt(startTimeSplit[1]);
                Calendar startTimeCalendar = Calendar.getInstance();
                startTimeCalendar.set(Calendar.HOUR_OF_DAY, startTimeHours);
                startTimeCalendar.set(Calendar.MINUTE, startTimeMinutes);
                if (appointment.get("availableAppointments").asInt() > 0) {
                    System.out.println("Appointment on day at " + startTimeCalendar.getTime().getHours() + ":" + startTimeCalendar.getTime().getMinutes());
                    HttpPost request2 = new HttpPost("https://api-server-podvaccine.azdhs.gov/appointment/booking");
                    String json = "{\n" +
                            "  \"contactId\": 1952166,\n" +
                            "  \"slotId\": " + appointment.get("id").asInt() + ",\n" +
                            "  \"insuranceId\": 1008849,\n" +
                            "  \"vaccineGroupIds\": \"1\",\n" +
                            "  \"priorityGroups\": \"Adults_16_older,Adults_18_older,Adults_20_older,Adults_25_older\",\n" +
                            "  \"isFollowUp\": false,\n" +
                            "  \"priorityAnswers\": [\n" +
                            "    {\n" +
                            "        \"isRequired\": true,\n" +
                            "        \"questionId\": 1,\n" +
                            "        \"question\": \"Do you live in a shelter or other congregate living settings with vulnerable populations?\",\n" +
                            "        \"type\": \"Radio\",\n" +
                            "        \"answer\": \"false\"\n" +
                            "    },\n" +
                            "    {\n" +
                            "        \"isRequired\": true,\n" +
                            "        \"questionId\": 2,\n" +
                            "        \"question\": \"Are you immunocompromised or do you have an underlying medical condition not limited to COPD, heart disease, diabetes, or chronic kidney disease?\",\n" +
                            "        \"type\": \"Radio\",\n" +
                            "        \"answer\": \"false\"\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}";
                    request2.setHeader("Authorization", userToken);
                    StringEntity entity = new StringEntity(json);
                    System.out.println("JSON " + json);
                    request2.setEntity(entity);
                    HttpResponse response2 = client.execute(request2);
                    BufferedReader rd2 = new BufferedReader
                            (new InputStreamReader(
                                    response2.getEntity().getContent()));

                    String appendLine2 = "";
                    String line2 = "";
                    while ((line2 = rd2.readLine()) != null) {
                        appendLine2 += line2;
                    }

                    System.out.println("STATUS: " + response2.getStatusLine());
                    System.out.println("Found: " + appendLine2);
                    if (objectMapper.readTree(appendLine2).get("message").asText().equalsIgnoreCase("SLOT_FULL")) {
                        return;
                    }
                    else {
                        System.exit(1);
                    }
                }
            }
        }
    }

    private void authorize() throws KeyStoreException, NoSuchAlgorithmException, IOException, KeyManagementException {
        System.out.println("Unauthorized");
        /*HttpClient client = HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                                .build()
                        )
                ).build();
        HttpPost request = new HttpPost("https://azvacpat.b2clogin.com/azvacpat.onmicrosoft.com/B2C_1A_signup_signin_saml/SelfAsserted?tx=StateProperties=eyJUSUQiOiIyMTMyZjI0ZS0wMTc2LTRhMTEtODJjNS1hZmM5MWY5ZmVjZDIifQ&p=B2C_1A_signup_signin_saml");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("request_type", "RESPONSE"));
        params.add(new BasicNameValuePair("signInName", "mbogushefsky@gmail.com"));
        params.add(new BasicNameValuePair("password", "Xboxlive880"));
        request.setEntity(new UrlEncodedFormEntity(params));
        request.setHeader("Authorization", userToken);
        request.setHeader("cookie", "_ga=GA1.2.375305488.1616612749; _gid=GA1.2.31382270.1616612749; refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMjZmYzM0Y2YtODY3NS00MDE5LWE3NjQtNTBjNjU0NDE0ZjlmIiwibmFtZUlEIjoiMjZmYzM0Y2YtODY3NS00MDE5LWE3NjQtNTBjNjU0NDE0ZjlmIiwibmFtZUlERm9ybWF0IjoidXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOm5hbWVpZC1mb3JtYXQ6cGVyc2lzdGVudCIsImlhdCI6MTYxNjYxNjU4MSwiZXhwIjoxNjE2NjQ1MzgxfQ.JuRWn2uffxH6LeAlExegCkL2cTMuR5zw0S-7St0CjXo; _gat_gtag_UA_1758531_31=1");
        HttpResponse response = client.execute(request);

        HttpGet request2 = new HttpGet("https://api-server-podvaccine.azdhs.gov/login/token?authtoken=" + authToken);
        request2.setHeader("Authorization", userToken);
        request2.setHeader("cookie", "_ga=GA1.2.375305488.1616612749; _gid=GA1.2.31382270.1616612749; refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMjZmYzM0Y2YtODY3NS00MDE5LWE3NjQtNTBjNjU0NDE0ZjlmIiwibmFtZUlEIjoiMjZmYzM0Y2YtODY3NS00MDE5LWE3NjQtNTBjNjU0NDE0ZjlmIiwibmFtZUlERm9ybWF0IjoidXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOm5hbWVpZC1mb3JtYXQ6cGVyc2lzdGVudCIsImlhdCI6MTYxNjYxNjU4MSwiZXhwIjoxNjE2NjQ1MzgxfQ.JuRWn2uffxH6LeAlExegCkL2cTMuR5zw0S-7St0CjXo; _gat_gtag_UA_1758531_31=1");
        HttpResponse response2 = client.execute(request2);
        BufferedReader rd2 = new BufferedReader
                (new InputStreamReader(
                        response2.getEntity().getContent()));

        String appendLine2 = "";
        String line2 = "";
        while ((line2 = rd2.readLine()) != null) {
            appendLine2 += line2;
        }
        JsonNode authNode =  objectMapper.readTree(appendLine2);
        System.out.println("Auth2 " + authNode.toString());
        userToken = authNode.get("token").asText();*/
    }

    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }

    private String getQueryParamsString(HashMap<String, String> queryParams) throws UnsupportedEncodingException {
        if (queryParams.size() == 0) {
            return "";
        }
        String resultQueryParamsString = "";
        int index = 0;
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            resultQueryParamsString += key + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
            if (index != (queryParams.size() - 1)) {
                resultQueryParamsString += "&";
            }
            index++;
        }
        return resultQueryParamsString;
    }

    private String getUriWithSignature(String uriBase, HashMap<String, String> queryParams) throws UnsupportedEncodingException {
        if (queryParams.size() == 0) {
            return uriBase;
        }
        String queryParamsString = getQueryParamsString(queryParams);
        return uriBase + "?" + queryParamsString;
    }

    private HttpEntity<?> getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", "Bearer " + userToken);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<?> getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", "Bearer " + userToken);
        return new HttpEntity<>(headers);
    }
}
