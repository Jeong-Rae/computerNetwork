# ComputerNetwork HTTP TCP socket 통신
* * *

## tcp socket part

### SSL 인증이 가능한 TCP socket 생성
```java
SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
Socket socket = sslSocketFactory.createSocket(serverHost, port);
```
`socketFactory`는 `socket`을 만들 수 있는 객체   
실제 통신할 `socket`은 `createSocket` 으로 만들어진다.   
이때 `client-side socket`이 만들어지면서 `server-side socket`과 연결이 이루어진다.   

### Client -> Server 데이터 전송
```java
PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), false);
printWriter.println(requestMessage);
printWriter.flush();
```
`printWriter`는 stream 문자열을 출력할 수 있는 객체   
`c/c++`의 `fprintf`와 유사하다.   
`client-side socket`의 `output stream`으로 byte를 보낸다.   

### Server -> Client 데이터 수신
```java
BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
String responseLine;
while ((responseLine = bufferedReader.readLine()) != null) {
    System.out.println(responseLine);
}
```
`bufferReader`가 `client-side socket`의 `input stream`으로 들어온 byte를 읽는다.   
**\n**가 등장할 때까지 한 줄을 기준으로 읽어서 출력한다.

### TCP socket 연결 해제
```java
socket.close();
System.out.println("\nDisconnect Server\n");
```

`socket`간의 연결을 해제한다.

## HTTP part

### 1. URL 자르기
```java
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
```
URL은 `Protocol://Domain/Path?Parameter`로 구성된다.   
예시로는 `https://api.interfacesejong.xyz/login.html?mode=light&device=mobile` 에서
- protocol : https
- port : 443 (https이기 때문)
- domain(host) : api.interfacesejong.xyz
- path : /login.html
- parameter : mode=light, device=mobile (2개 이상 연결은 & 연결)

GUI에서 입력받은 `PARAMS`를 문자열로 가공하여 요청 `PATH`뒤에 추가한다.   
   

### 2. 요청 메세지 만들기
```java
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
```
`HTTP`에서 사용할 `method`는 `GET, POST, PUT, DELETE`만 사용한다. (`RESTful API 요청 기준`)   


#### GET 예시 (DELETE도 구조 동일)
```java
private String createGetMessage(){
        String requestMessage = "GET " + requestPath + " HTTP/1.1\r\n";
        requestMessage += "Host: " + serverHost + "\r\n";
        requestMessage += headersToString();
        requestMessage += "Accept: */*\r\n";
        requestMessage += "Connection: close\r\n\r\n";
        
        return requestMessage;
}
```
`HTTP` 요청 양식에 맞추어 `request header` 작성  
GUI에서 입력받은 `HEADERS`는 여기서 문자열로 가공해서 추가

#### POST 예시 (PUT도 구조 동일)
```java
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
```

`Content-Type`, `Content-Length` 헤더 추가   
`requestBody`가 추가된다.   

#### Request Body

- `application/x-www-form-urlencoded`   
    `key1=val1&key2=val2` 형식으로 구성
- `application/json`   
    `{"key1"="val1","key2"="val2"` 형식으로 구성
- `multipart/form-data`   
    ```shell
  Content-Type: type; boundary= $code
  
  --$code
  Content-Disposition: form-data; name=key1
  
  val1
  ```
  형식으로 구성   

#FIN