package client.utils;

import java.net.URL;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

public class HTTPSRequest {
    private static String serverURL = "https://pocketmsg.ru:8888";

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

    private static int sendRequest(HttpsURLConnection con, String requestJSON) throws Exception {
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(requestJSON);
        wr.flush();
        wr.close();

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

            System.out.println("response " + response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public static String addContact(String requestJSON, String token) throws Exception {
        URL obj = new URL(serverURL + "/v1/users/contacts/");
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Token", token);
        int responseCode = sendRequest(con, requestJSON);
//    возможно тут надо будет вернуть user ,когда сервер сделает чтобы
//         одинаковые контакты не добавлялись
//        Если конечно на запрос на добавление контакта вернет все поля юзера
        if (responseCode != 201) {
            return "" + responseCode;
        }
        return answerRequest(con);
    }

    public static String getContact(String token)throws Exception {
        URL obj = new URL(serverURL + "/v1/users/contacts/");
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Token", token);
        int responseCode = con.getResponseCode();
        return answerRequest(con);
    }
}
