package client;

import java.net.URL;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

public class HTTPSRequest {

    public static void registration(String requestJSON ) throws Exception {
        String url = "https://pocketmsg.ru:8888/v1/users/";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");

//        String requestJSON  = "{" +
//                "\"account_name\": \"OzzyFrost\"," +
//                "\"email\": \"5kla@mail.ru\"," +
//                "\"password\": \"12345\"" +
//                "}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(requestJSON );
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + requestJSON );
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

    public static String avtorization(String requestJSON ) throws Exception {
        String url = "https://pocketmsg.ru:8888/v1/auth/";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("PUT");

//        String requestJSON = "{" +
////                "\"user\": \"OzzyFrost\"," +
////                "\"password\": \"12345\"" +
////                "}";

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

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

        return response.toString();
    }

}
