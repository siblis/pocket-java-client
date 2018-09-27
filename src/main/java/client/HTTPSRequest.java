package client;

import java.net.URL;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

class HTTPSRequest {

    static int registration(String requestJSON) throws Exception {
        String url = "https://pocketmsg.ru:8888/v1/users/";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(requestJSON);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + requestJSON);
        System.out.println("Response Code : " + responseCode);

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            //print result
            System.out.println(response.toString());
        } catch (IOException e) {
            System.out.println("Ошибка, код: " + responseCode);
            e.printStackTrace();
            return responseCode;
        }

        return responseCode;
    }

    static String avtorization(String requestJSON) throws Exception {
        String url = "https://pocketmsg.ru:8888/v1/auth/";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("PUT");

        // Send request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(requestJSON);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'PUT' request to URL : " + url);
        System.out.println("Put parameters : " + requestJSON);
        System.out.println("Response Code : " + responseCode);

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            //print result
            System.out.println(response.toString());
        } catch (IOException e) {
            System.out.println("Ошибка, код: " + responseCode);
            e.printStackTrace();
        }

        return response.toString();
    }

}
