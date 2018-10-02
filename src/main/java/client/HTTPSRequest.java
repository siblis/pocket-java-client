package client;

import com.sun.net.httpserver.HttpsServer;
import jdk.nashorn.internal.parser.JSONParser;

import java.net.ResponseCache;
import java.net.URL;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

class HTTPSRequest {

    static int registration(String requestJSON) throws Exception {
        String url = "https://pocketmsg.ru:8888/v1/users/";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        int responseCode = sendRequest(con,requestJSON);
        answerRequest(con);

        return responseCode;
        // мне кажется надо вернуть JSON, а ошибки responseCode надо
//        обрабатывать тут
    }

    static String avtorization(String requestJSON) throws Exception {
        String url = "https://pocketmsg.ru:8888/v1/auth/";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("PUT");
        sendRequest(con,requestJSON);

        return answerRequest(con);
    }

    private static int sendRequest(HttpsURLConnection con, String requestJSON) throws Exception {
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(requestJSON);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending "+con.getRequestMethod()+" request to URL : " + con.getURL());
        System.out.println("Put parameters : " + requestJSON);
        System.out.println("Response Code : " + responseCode);

        return responseCode;
    }

    private static String answerRequest(HttpsURLConnection con){
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
    public static void addContact (String requestJSON, String token)throws Exception{

        String url = "https://pocketmsg.ru:8888/v1/users/";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Token", token);
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(requestJSON);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'PUT' request to URL : " + url);
        System.out.println("PUT parameters : " + requestJSON );
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());
    }
}
