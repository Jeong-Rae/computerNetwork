package computerNetwork.http;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpClient {
    private String httpMethod;
    private String serverHost;
    private String requestPath;
    private int port;
    private String contentType;
    private UUID boundary;

    Map<String, String> params;
    Map<String, String> headers;
    Map<String, String> body;

    public HttpClient(Map<String, String> params, Map<String, String> headers, Map<String, String> body, String contentType) {
        this.params = params;
        this.headers = headers;
        this.body = body;
        this.contentType = contentType;
    }

    public Map<String, String> sendRequest(String url, String method) {
        this.httpMethod = method;
        String requestMessage = createHttpMessage(url);
        StringBuilder sbResponse = new StringBuilder();
        Map<String, String> ret = new HashMap<>();
        ret.put("request",requestMessage);

        System.out.println("server host: " + serverHost);
        System.out.println("server port: " + port);
        System.out.println("request Message\n" + requestMessage);

        try {
            // TCP socket 생성
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            Socket socket = sslSocketFactory.createSocket(serverHost, port);
            System.out.println("Connect Server\n");

            // HTTP request 전송
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), false);
            printWriter.println(requestMessage);
            printWriter.flush();

            // HTTP response 출력
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
            System.out.println("\nDisconnect Server\n");
        }
        catch (UnknownHostException exception){
            System.out.println(exception.getMessage());
        }
        catch (IOException exception){
            System.out.println(exception.getMessage());
            exception.getCause();
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

    private String createGetMessage(){
        String requestMessage = "GET " + requestPath + " HTTP/1.1\r\n";
        requestMessage += "Host: " + serverHost + "\r\n";
        requestMessage += headersToString();
        requestMessage += "Accept: */*\r\n";
        requestMessage += "Connection: close\r\n\r\n";

        return requestMessage;
    }
    private String createPostMessage(){
        String requestBody = bodyToString();
        String requestMessage = "POST " + requestPath + " HTTP/1.1\r\n";
        requestMessage += "Host: " + serverHost + "\r\n";
        requestMessage += "Content-Type: " + contentType + "\r\n";
        requestMessage += "Content-Length: " + requestBody.length() + "\r\n";
        requestMessage += headersToString();
        requestMessage += "Accept: */*\r\n";
        requestMessage += "Connection: close\r\n\r\n";
        requestMessage += requestBody;

        return requestMessage;
    }
    private String createPutMessage(){
        String requestBody = bodyToString();
        String requestMessage = "PUT " + requestPath + " HTTP/1.1\r\n";
        requestMessage += "Host: " + serverHost + "\r\n";
        requestMessage += "Content-Type: " + contentType + "\r\n";
        requestMessage += "Content-Length: " + requestBody.length() + "\r\n";
        requestMessage += headersToString();
        requestMessage += "Accept: */*\r\n";
        requestMessage += "Connection: close\r\n\r\n";
        requestMessage += requestBody;

        return requestMessage;
    }
    private String createDeleteMessage(){
        String requestMessage = "DELETE " + requestPath + " HTTP/1.1\r\n";
        requestMessage += "Host: " + serverHost + "\r\n";
        requestMessage += headersToString();
        requestMessage += "Accept: */*\r\n";
        requestMessage += "Connection: close\r\n\r\n";

        return requestMessage;
    }

    private void parseUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            serverHost = url.getHost();
            requestPath = url.getPath() + paramsToQuery();
            if (urlStr.startsWith("https"))
                port = 443;
            else if (urlStr.startsWith("http"))
                port = 80;

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private String paramsToQuery(){
        if (params.isEmpty())
            return "";
        return "?" + params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    private String headersToString(){
        if (headers.isEmpty())
            return "";
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + "\r\n")
                .collect(Collectors.joining());
    }

    private String bodyToString(){
        if (body.isEmpty())
            return "";
        if (contentType.equals("application/x-www-form-urlencoded")){
            return body.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));
        }
        if (contentType.equals("application/json")){
            return body.entrySet().stream()
                    .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
                    .collect(Collectors.joining(",","{","}"));
        }
        if (contentType.equals("multipart/form-data")){
            boundary = UUID.randomUUID();
            contentType += "; boundary="+boundary;
            return body.entrySet().stream()
                    .map(entry -> "--"+boundary.toString() + "\nContent-Disposition: form-data; name=\"" + entry.getKey()+ "\"\n\n" + entry.getValue() + "\n")
                    .collect(Collectors.joining());
        }
        return "";
    }
}
