import java.net.*;
import java.io.*;
import java.util.*;


public class ProxyWorker extends Thread {
    private Socket socket = null;
    public static final int BUFFER_SIZE = 32768;
    public ProxyWorker(Socket socket) {
        this.socket = socket;
    }

    public Request getClientRequest() throws IOException {
        BufferedReader clientInputReader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        String inputLine;
        int count = 0;
        String destinationURL = "";
        while ((inputLine = clientInputReader.readLine()) != null) {
            try {
                StringTokenizer tok = new StringTokenizer(inputLine);
                tok.nextToken();
            } catch (Exception e) {
                break;
            }
            if (count == 0) {
                String[] tokens = inputLine.split(" ");
                destinationURL = tokens[1];
                //can redirect this to output log
                System.out.println("Request for : " + destinationURL);
            }

            count++;
        }

        return new Request(destinationURL, clientInputReader);
    }

    public Response getServerResponse(Request clientRequest) throws IOException {
        BufferedReader responseReader = null;

        URL requestedURL = new URL(clientRequest.getUrl());
        URLConnection conn = requestedURL.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(false);
        InputStream is = null;
        HttpURLConnection httpConnection = (HttpURLConnection)conn;
        if (conn.getContentLength() > 0) {
            is = conn.getInputStream();
            responseReader = new BufferedReader(new InputStreamReader(is));
        }
        return new Response(is, responseReader, new DataOutputStream(socket.getOutputStream()));
    }

    public void closeResources(Request clientRequest, Response serverResponse) throws IOException {
        BufferedReader contentReader = serverResponse.getContentReader();
        if (contentReader != null) contentReader.close();
        DataOutputStream outputStream = serverResponse.getOutputStream();
        if (outputStream != null) outputStream.close();
        BufferedReader clientHeaderReader = clientRequest.getHeaderReader();
        if (clientHeaderReader != null) clientHeaderReader.close();
        if (socket != null) socket.close();
    }

    public void run() {
        try {
            Request clientRequest = getClientRequest();
            Response serverResponse = getServerResponse(clientRequest);
            serverResponse.writeToOutputStream();
            closeResources(clientRequest, serverResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
