package computerNetwork.http;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

public class HC {
    String serverAddress ;// 웹 서버 주소
    int serverPort; // 서버 포트
    String requestMethod; // 요청 메서드 종류
    String requestPath; // 요청할 리소스 경로

    public HC() throws IOException {
    }

    private String createRequestMessage() throws IOException {
        System.out.print("serverAddress : ");
        serverAddress = readLine().trim();
        System.out.print("serverPort : ");
        serverPort = Integer.parseInt(readLine().trim());
        System.out.print("requestMethod : ");
        requestMethod = readLine().toUpperCase().trim();
        System.out.print("requestPath : ");
        requestPath = readLine().trim();


        String requestMessage = "";
        if (requestMethod.equals("GET")){
            // HTTP GET 요청 메시지
            requestMessage += requestMethod + " " + requestPath + " HTTP/1.1\r\n";
            requestMessage += "Host: " + serverAddress + "\r\n";
            requestMessage += "Connection: close\r\n\r\n";
        } else if (requestMethod.equals("POST")){
            System.out.print("Content-Type (1) form (2) json : ");
            int n = Integer.parseInt(readLine().trim());
            String contentType = null;
            if (n < 1 || 2 < n){
                System.out.println("type error");
                throw new IllegalArgumentException("type error");
            }
            if (n == 1){
                contentType = "application/x-www-form-urlencoded";
            } else if (n == 2) {
                contentType = "application/json";
            }
            System.out.print("requestBody : ");
            String requestBody = readLine().trim();

            requestMessage = "POST " + requestPath + " HTTP/1.1\r\n";
            requestMessage += "Host: " + serverAddress + "\r\n";
            requestMessage += "Content-Type: " + contentType + "\r\n";
            requestMessage += "Content-Length: " + requestBody.length() + "\r\n";
            requestMessage += "Connection: close\r\n\r\n";
            requestMessage += requestBody;
        }

        return requestMessage;
    }

    public void sendRequest() throws IOException {
        try {
            // HTTP 요청 메서드 생성
            String requestMessage = createRequestMessage();

            // 웹 서버와 연결
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            Socket socket = sslSocketFactory.createSocket(serverAddress, serverPort);
            System.out.println("Connect Server\n");

            // HTTP 요청 전송
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLine() throws IOException {
        InputStream inputStream = System.in;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        return bufferedReader.readLine();
    }
}

