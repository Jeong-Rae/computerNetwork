package computerNetwork;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        HTTPClient httpClient = new HTTPClient();

        httpClient.sendRequest();
    }
}