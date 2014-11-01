import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class ProxyWorker extends Thread {
    private Socket socket = null;
    private Socket httpSocket = null;
    public static final int BUFFER_SIZE = 32768;
    public ProxyWorker(Socket socket) {
        this.socket = socket;
    }

    public Request getClientRequest() throws IOException {
        BufferedReader clientInputReader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        String inputLine;
        int lineNumber = 0;
        String requestedURL = "";
        StringBuilder header = new StringBuilder();
        while ((inputLine = clientInputReader.readLine()) != null) {
            try {
                StringTokenizer tok = new StringTokenizer(inputLine);
                tok.nextToken();
            } catch (Exception e) {
                break;
            }
            if (lineNumber == 0) {
                String[] tokens = inputLine.split(" ");
                requestedURL = tokens[1];
            }

            header.append(inputLine);
            header.append("\r\n");
            lineNumber++;
        }

        header.append("\r\n");

        return new Request(requestedURL, header.toString());
    }

    public Response getServerResponse(Request clientRequest) throws IOException {
        String httpMethod = clientRequest.getHTTPMethod();
        //if (httpMethod.equals("POST"))
        //    throw new IOException("does not accept post requests");

        System.out.println(httpMethod + " " + clientRequest.getUrl());
        httpSocket = new Socket();
        httpSocket.connect(new InetSocketAddress(clientRequest.getHost(), 80));
        PrintWriter outWriter = new PrintWriter(httpSocket.getOutputStream(), true);
        outWriter.println(clientRequest.getHeader());
        InputStream inputStream = httpSocket.getInputStream();

        return new Response(inputStream, outWriter);
    }

    public void closeResources(Response serverResponse) throws IOException {
        serverResponse.closeStreams();
        if (httpSocket != null) httpSocket.close();
        //if (socket != null) socket.close();
    }

    public void run() {
        try {
            Request clientRequest = getClientRequest();
            Response serverResponse = getServerResponse(clientRequest);
            serverResponse.writeToOutputStream(socket.getOutputStream());
            closeResources(serverResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
