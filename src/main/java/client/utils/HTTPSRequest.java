package client.utils;

import client.model.ServerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

public class HTTPSRequest {

    private static final Logger logger = LogManager.getLogger(HTTPSRequest.class.getName());
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
        URL obj = new URL(serverURL + "/v1/users/");
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        int responseCode = sendRequest(con, requestJSON);
        answerRequest(con);

        return responseCode;
        // мне кажется надо вернуть JSON, а ошибки responseCode надо
//        обрабатывать тут
    }

    public static String authorization(String requestJSON) throws Exception {
        URL obj = new URL(serverURL + "/v1/auth/");
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("PUT");
        sendRequest(con, requestJSON);
        return answerRequest(con);
    }

    public static ServerResponse getUser(long id, String token) throws Exception {
        URL url = new URL(serverURL + "/v1/users/" + id);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Token", token);

        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setResponseCode(sendRequest(connection, null));
        serverResponse.setResponseJson(answerRequest(connection));

        return serverResponse;
    }

    public static ServerResponse addContact(String requestJSON, String token) throws Exception {
        URL url = new URL(serverURL + "/v1/users/contacts/");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Token", token);

        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setResponseCode(sendRequest(connection, requestJSON));
        serverResponse.setResponseJson(answerRequest(connection));

        return serverResponse;
    }

    public static ServerResponse getContacts(String token) throws Exception {
        URL url = new URL(serverURL + "/v1/users/contacts/");
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Token", token);

        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setResponseCode(sendRequest(connection, null));
        serverResponse.setResponseJson(answerRequest(connection));
        return serverResponse;
    }

    public static ServerResponse getMySelf(String token) throws Exception {
        URL url = new URL(serverURL + "/v1/users/");
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Token", token);

        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setResponseCode(sendRequest(connection, null));
        serverResponse.setResponseJson(answerRequest(connection));
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
            e.printStackTrace();
            logger.error(e);
        }
        return response.toString();
    }
}
