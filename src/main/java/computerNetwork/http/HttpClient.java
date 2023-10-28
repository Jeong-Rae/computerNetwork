package computerNetwork.http;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
    private String httpMethod;
    private String serverHost;
    private String requestPath;
    private int port;
    private String contentType;

    Map<String, String> params;
    Map<String, String> headers;
    Map<String, String> body;

    public HttpClient() {
    }

    public HttpClient(Map<String, String> params, Map<String, String> headers, Map<String, String> body) {
        this.params = params;
        this.headers = headers;
        this.body = body;
    }

    public Map<String, String> sendRequest(String url, String method) {
        this.httpMethod = method;
        String requestMessage = createHttpMessage(url);
        StringBuilder sbResponse = new StringBuilder();
        Map<String, String> ret = new HashMap<>();
        ret.put("request",requestMessage);

        System.out.println(serverHost);
        System.out.println(port);

        try {

            // TCP socket 생성
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
                sbResponse.append(responseLine);
                sbResponse.append("\n");
            }
            ret.put("response",sbResponse.toString());

            // 연결 종료
            socket.close();
            System.out.println("\nDisconnect Server");
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return ret;
    }
    public String createHttpMessage(String url){
        parseUrl(url);
        if (httpMethod.equals("GET"))
            return createGetMessage();
        if (httpMethod.equals("POST"))
            return createPostMessage();
        if (httpMethod.equals("PUT"))
            return createPutMessage();
        if (httpMethod.equals("DELETE"))
            return createDeleteMessage();
        return null;
    }

    private String createPostMessage(){
        String requestMessage = "";
        requestMessage += httpMethod + " " + requestPath + " HTTP/1.1\r\n";
        requestMessage += "Host: " + serverHost + "\r\n";
        requestMessage += "Connection: close\r\n\r\n";

        return requestMessage;
    }
    private String createPutMessage(){
        String requestMessage = "";
        requestMessage += httpMethod + " " + requestPath + " HTTP/1.1\r\n";
        requestMessage += "Host: " + serverHost + "\r\n";
        requestMessage += "Connection: close\r\n\r\n";

        return requestMessage;
    }
    private String createDeleteMessage(){
        String requestMessage = "";
        requestMessage += httpMethod + " " + requestPath + " HTTP/1.1\r\n";
        requestMessage += "Host: " + serverHost + "\r\n";
        requestMessage += "Connection: close\r\n\r\n";

        return requestMessage;
    }
    private String createGetMessage(){
        String requestMessage = "";
        requestMessage += httpMethod + " " + requestPath + " HTTP/1.1\r\n";
        requestMessage += "Host: " + serverHost + "\r\n";
        requestMessage += "Connection: close\r\n\r\n";

        return requestMessage;
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
}
