package computerNetwork;

import java.io.*;
import java.net.Socket;

public class HTTPClient {
    String serverAddress = readLine().trim(); // 웹 서버 주소
    int serverPort = Integer.parseInt(readLine().trim()); // 서버 포트
    String requestMethod = readLine().toUpperCase().trim(); // 요청 메서드 종류
    String requestPath = readLine().trim(); // 요청할 리소스 경로

    public HTTPClient() throws IOException {
    }

    private String createRequestMessage() throws IOException {
        // HTTP 요청 메시지
        String requestMessage = requestMethod + " " + requestPath + " HTTP/1.1\r\n";
        requestMessage += "Host: " + serverAddress + "\r\n";
        requestMessage += "Connection: close\r\n\r\n";

        return requestMessage;
    }

    public void sendRequest() throws IOException {
        try {
            // HTTP 요청 메서드 생성
            String requestMessage = createRequestMessage();

            // 웹 서버와 연결
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connect Server");



            // HTTP 요청 전송
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println(requestMessage);

            // HTTP 응답 출력
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String responseLine;
            while ((responseLine = bufferedReader.readLine()) != null) {
                System.out.println(responseLine);
            }

            // 연결 종료
            socket.close();
            System.out.println("Disconnect Server");

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

