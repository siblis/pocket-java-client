package ru.geekbrains.pocket.messenger.client.utils;

import ru.geekbrains.pocket.messenger.client.model.ServerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class HTTPSRequest {
    private static final Logger requestLogger = LogManager.getLogger(HTTPSRequest.class.getName());
    private static String serverURL = "https://" + Connector.connectTo;
    private static HttpsURLConnection con;
    private static boolean isTestMode = false;
    private static int testResponseCode;
    private static String testResponseString;
    
    static void testModeInit(int forTestResponseCode, String forTestResponseString) {
        isTestMode = true;
        testResponseCode = forTestResponseCode;
        testResponseString = forTestResponseString;
    }

    static void testModeEnd() {
        isTestMode = false;
    }

    public static int sendRequest(String path, String method, String requestJSON, String token) throws Exception {
        if (isTestMode) { return testResponseCode; }
        else { return sendRequestToServer(path, method, token, requestJSON); }
    }

    public static String getResponse() throws Exception {
        if (isTestMode) { return testResponseString; }
        else { return getResponseFromServer(); }
    }

    private static int sendRequestToServer(String path, String method, String token, String requestJSON) throws IOException, Exception {
        con = getConnection(path, method, token);
        if (requestJSON != null) {
            con.setRequestProperty("content-type", "application/json");
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(requestJSON);
            wr.flush();
            wr.close();
        }

        int responseCode = con.getResponseCode();
        requestLogger.info("\nSending " + con.getRequestMethod() + 
                " request to URL : " + con.getURL() + "\nPut parameters : " + 
                requestJSON + "\nResponse Code : " + responseCode);

        return responseCode;
    }

    private static String getResponseFromServer() throws IOException {
        int responseCode = con.getResponseCode();
        StringBuilder response = new StringBuilder();
        InputStream stream = (responseCode == 200 || responseCode == 201) ?
                con.getInputStream() : con.getErrorStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        requestLogger.info(response.toString());
        
        return response.toString();
    }

    public static <T> T getResponse(Class<T> valueType) throws Exception {
        return Converter.toJavaObject(getResponse(), valueType);
    }

    public static String restorePassword(String requestJSON) throws Exception {
        //TODO удалить после рефакторинга (этот методы был к старому бэкенду)
        URL obj = new URL(serverURL + "/v1/email/");
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        int responseCode = sendRequest(con, requestJSON);
        //TODO ошибки responseCode обрабатывать тут или нет?
        //responseCode == ?
        if (responseCode == 201) {
            //успешно
            return answerRequest(con);
        } else
            //Ошибка
            return Integer.toString(responseCode); //код ошибки?
    }

    public static String changePassword(String requestJSON) throws Exception {
        //TODO удалить после рефакторинга (этот методы был к старому бэкенду)
        URL obj = new URL(serverURL + "/v1/pass/");
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        sendRequest(con, requestJSON);
        return answerRequest(con);
    }

    //TODO удалить после рефакторинга (этот методы был к старому бэкенду)
    public static ServerResponse getMySelf(String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/account/", "GET", token);
        return getServerResponse(connection, null);
    }

    //TODO удалить после рефакторинга (этот методы был к старому бэкенду)
    public static ServerResponse addGroup(String requestJSON, String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/chats/", "POST", token);
        return getServerResponse(connection, requestJSON);
    }

    //TODO удалить после рефакторинга (этот методы был к старому бэкенду)
    public static ServerResponse addUserGroup(String requestJSON, String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/chats/", "PUT", token);
        return getServerResponse(connection, requestJSON);
    }

    //TODO удалить после рефакторинга (этот методы был к старому бэкенду)
    public static ServerResponse getUserMessages(String token, String userId, int offset) throws Exception {
        HttpsURLConnection connection = getConnection("/user/" + userId +
                "/messages/?offset=" + offset, "GET", token);
        return getServerResponse(connection, null);
    }

    //TODO удалить после рефакторинга (этот методы был к старому бэкенду)
    public static ServerResponse getGroupInfo(String id, String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/chats/" + id, "GET", token);
        return getServerResponse(connection, null);
    }

    private static HttpsURLConnection getConnection(String urlPath, String method, String token) throws Exception {
        URL url = new URL(serverURL + urlPath);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        if (token != null)
            connection.setRequestProperty("Authorization", "Bearer " + token);
        return connection;
    }

    //TODO удалить после рефакторинга (этот методы был к старому бэкенду)
    private static ServerResponse getServerResponse(HttpsURLConnection con, String requestJSON) throws Exception {
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setResponseCode(sendRequest(con, requestJSON));
        serverResponse.setResponseJson(answerRequest(con));
        return serverResponse;
    }

    //TODO удалить после рефакторинга (этот методы был к старому бэкенду)
    private static int sendRequest(HttpsURLConnection con, String requestJSON) throws Exception {
        if (requestJSON != null) {
            con.setRequestProperty("content-type", "application/json");
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(requestJSON);
            wr.flush();
            wr.close();
        }

        int responseCode = con.getResponseCode();
        requestLogger.info("\nSending " + con.getRequestMethod() + 
                " request to URL : " + con.getURL() + "\nPut parameters : " + 
                requestJSON + "\nResponse Code : " + responseCode);

        return responseCode;
    }

    //TODO удалить после рефакторинга (этот методы был к старому бэкенду)
    private static String answerRequest(HttpsURLConnection con) throws Exception {
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            requestLogger.info(response.toString());
        } catch (IOException e) {
            requestLogger.error("answerRequest_error", e);
            throw e;
        }
        return response.toString();
    }
}
