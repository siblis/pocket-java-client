package client.utils;

import client.controller.ClientController;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLSocketFactory;

public class Connector {
    private WebSocketChatClient chatClient;

    public Connector(String token, ClientController controller){
        try {
            connect(token,controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WebSocketChatClient getChatClient() {
        return chatClient;
    }

    public void connect(String token, ClientController controller) throws Exception {
        Map<String,String> httpHeaders = new HashMap<String, String>();
//        httpHeaders.put("Token","f5b7c119e858b9f3");
        httpHeaders.put("Token",token);
        chatClient = new WebSocketChatClient(
                new URI("wss://pocketmsg.ru:8888/v1/ws/"),httpHeaders,controller);
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        chatClient.setSocketFactory(factory);
        chatClient.connectBlocking();

//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

//            String line = reader.readLine();
//            if (line.equals("close")) {
//                chatclient.close();
//            } else {
//                chatclient.send(line);
//            }
//        }




//        uid | username | email | token
//                -----+-----------+----------+----------+------------------
//        2 | testuser1  | testmail | 2d1ea610bc493d76
//        3 | testuser2 | testmail | f5b7c119e858b9f3
//        формат сообщения
//          { "receiver":"2", "message":"helloworld" }
//      регистрация
//        { "account_name": username,"email": email,"password": password }
//       авторизация
//    { "account_name": username,"password": password }

//        httpHeaders.put("Token","36a6908c783ba6e5");
//        httpHeaders.put("Token","f5b7c119e858b9f3");
//        httpHeaders.put("Token","2d1ea610bc493d76");

//        System.out.println("httpHEADER"+httpHeaders);
//        WebSocketChatClient chatclient = new WebSocketChatClient(new URI("wss://echo.websocket.org:443/"),httpHeaders);


//        SSLSocketFactory factory = sslContext.getSocketFactory();//
    }


}
