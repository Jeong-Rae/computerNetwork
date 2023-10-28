package computerNetwork.http;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class HttpClient {
    private String httpMethod;
    private String serverHost;
    private String requestPath;
    private int port;
    private String contentType;

    public HttpClient() {
    }

    private void parseUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            serverHost = url.getHost();
            requestPath = url.getPath();
            if (urlStr.startsWith("https"))
                port = 443;
            else if (urlStr.startsWith("http"))
                port = 80;

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String sendRequest(String url, String method) {
        this.httpMethod = method;
        String requestMessage = createHttpMessage(url);
        String responseMessage = null;

        System.out.println(httpMethod);
        System.out.println(serverHost);
        System.out.println(requestPath);
        System.out.println(port);
        System.out.println(requestMessage);

        try {

            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            Socket socket = sslSocketFactory.createSocket(serverHost, port);
            System.out.println("Connect Server\n");

            // HTTP 요청 전송
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), false);
            printWriter.println(requestMessage);
            printWriter.flush();

            // HTTP 응답 출력
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String responseLine;
            while ((responseLine = bufferedReader.readLine()) != null) {
                System.out.println(responseLine);
            }

            // 연결 종료
            socket.close();
            System.out.println("\nDisconnect Server");
        }
        catch (IOException e){
            e.printStackTrace();
        }



        return responseMessage;
    }
    public String createHttpMessage(String url){
        parseUrl(url);
        if (httpMethod.equals("GET"))
            return createGetMessage();
        return null;
    }

    private String createGetMessage(){
        String requestMessage = "";
        requestMessage += httpMethod + " " + requestPath + " HTTP/1.1\r\n";
        requestMessage += "Host: " + serverHost + "\r\n";
        requestMessage += "Connection: close\r\n\r\n";

        return requestMessage;
    }

}