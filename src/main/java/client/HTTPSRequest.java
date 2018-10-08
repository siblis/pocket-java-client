package client;

import java.net.URL;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

class HTTPSRequest {
    private static String serverURL = "https://pocketmsg.ru:8888";

    static int registration(String requestJSON) throws Exception {
        URL obj = new URL(serverURL + "/v1/users/");
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        int responseCode = sendRequest(con, requestJSON);
        answerRequest(con);

        return responseCode;
        // мне кажется надо вернуть JSON, а ошибки responseCode надо
//        обрабатывать тут
    }

    static String avtorization(String requestJSON) throws Exception {
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

            System.out.println(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public static int addContact(String requestJSON, String token) throws Exception {
        URL obj = new URL(serverURL + "/v1/users/");
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Token", token);
        int responseCode = sendRequest(con, requestJSON);
        int id = -1;
        if (responseCode == 200) {
            String uid = answerRequest(con);
            id = Integer.parseInt(uid.substring(16, uid.indexOf(",")));
        }
//    возможно тут надо будет вернуть user ,когда сервер сделает чтобы
//         одинаковые контакты не добавлялись
//        Если конечно на запрос на добавление контакта вернет все поля юзера
        return id;
    }
}
