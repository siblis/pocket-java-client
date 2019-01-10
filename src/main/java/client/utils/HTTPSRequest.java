package client.utils;

import client.model.ServerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class HTTPSRequest {
    private static final Logger requestLogger = LogManager.getLogger(HTTPSRequest.class.getName());
    private static String serverURL = "https://pocketmsg.ru:8888";

    public static String restorePassword(String requestJSON) throws Exception {
        //TODO нужен API на сервере
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
        //TODO нужен API на сервере
        URL obj = new URL(serverURL + "/v1/pass/");
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        sendRequest(con, requestJSON);
        return answerRequest(con);
    }

    public static int registration(String requestJSON) throws Exception {
        URL obj = new URL(serverURL + "/v1/auth/register/");
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        int responseCode = sendRequest(con, requestJSON);
        answerRequest(con);

        return responseCode;
        // мне кажется надо вернуть JSON, а ошибки responseCode надо
//        обрабатывать тут
    }

    public static String authorization(String requestJSON) throws Exception {
        URL obj = new URL(serverURL + "/v1/auth/login/");
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        sendRequest(con, requestJSON);
        return answerRequest(con);
    }

    public static ServerResponse getUser(long id, String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/users/" + id, "GET", token);
        return getServerResponse(connection, null);
    }

    //todo разделить поиск по имени от поиска по почте? сейчас это не разделено на сервере
    public static ServerResponse getUser(String nameOrEmail, String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/users/" + nameOrEmail, "GET", token);
        return getServerResponse(connection, null);
    }

    public static ServerResponse addContact(String requestJSON, String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/account/contacts/", "POST", token);
        return getServerResponse(connection, requestJSON);
    }

    public static ServerResponse deleteContact(String contastsEmailJSON, String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/account/contacts/", "DELETE", token);
        return getServerResponse(connection, contastsEmailJSON);
    }

    public static ServerResponse getContacts(String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/account/contacts/", "GET", token);
        return getServerResponse(connection, null);
    }

    public static ServerResponse getMySelf(String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/account/", "GET", token);
        return getServerResponse(connection, null);
    }

    public static ServerResponse addGroup(String requestJSON, String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/chats/", "POST", token);
        return getServerResponse(connection, requestJSON);
    }

    public static ServerResponse addUserGroup(String requestJSON, String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/chats/", "PUT", token);
        return getServerResponse(connection, requestJSON);
    }

    // я вообще поражаюсь зачем этот метод в АПИ сделали. ведь в вебсокете должнеы быть все сообщения
    public static ServerResponse addMessageGroup(String requestJSON, String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/chats/messages/", "POST", token);
        return getServerResponse(connection, requestJSON);
    }

    public static ServerResponse getGroupInfo(String id, String token) throws Exception {
        HttpsURLConnection connection = getConnection("/v1/chats/" + id, "GET", token);
        return getServerResponse(connection, null);
    }

    private static HttpsURLConnection getConnection(String urlPath, String method, String token) throws Exception {
        URL url = new URL(serverURL + urlPath);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Token", token);
        return connection;
    }

    private static ServerResponse getServerResponse(HttpsURLConnection con, String requestJSON) throws Exception {
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setResponseCode(sendRequest(con, requestJSON));
        serverResponse.setResponseJson(answerRequest(con));
        return serverResponse;
    }

    private static int sendRequest(HttpsURLConnection con, String requestJSON) throws Exception {
        if (requestJSON != null) {
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(requestJSON);
            wr.flush();
            wr.close();
        }

        int responseCode = con.getResponseCode();
        System.out.println("\nSending " + con.getRequestMethod() + " request to URL : " + con.getURL());
        System.out.println("Put parameters : " + requestJSON);
        System.out.println("Response Code : " + responseCode);

        return responseCode;
    }

    private static String answerRequest(HttpsURLConnection con) {
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            System.out.println(response.toString());
        } catch (IOException e) {
            requestLogger.error("answerRequest_error", e);
        }
        return response.toString();
    }
}
