import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentMap;

public class ProxyWorker extends Thread {
    private Socket socket = null;
    private ConcurrentMap<String, InetSocketAddress> dnsCache;
    private Socket httpSocket = null;
    public static final int BUFFER_SIZE = 32768;
    public ProxyWorker(Socket socket, ConcurrentMap<String, InetSocketAddress> dnsCache) {
        this.socket = socket;
        this.dnsCache = dnsCache;  // a concurrent HashMap that is updated atomically with new <host, IP address> pairs
    }

    public Request getClientRequest() throws IOException {
        /*
        Returns a Request object that holds the client requested URL and header message
         */
        BufferedReader clientInputReader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        String inputLine;
        int lineNumber = 0;
        String requestedURL = "";
        StringBuilder headerBuilder = new StringBuilder();
        boolean hasConnectionField = false;

        while ((inputLine = clientInputReader.readLine()) != null) {
            try {
                StringTokenizer tok = new StringTokenizer(inputLine);
                tok.nextToken();
            } catch (Exception e) {
                break;  // break if there is no token (must be blank line/"EOF")
            }

            if (lineNumber == 0) {
                String[] tokens = inputLine.split(" ");
                requestedURL = tokens[1]; // grab URL
            }

            if (inputLine.contains("Connection")) {
                inputLine = "Connection: close";
                hasConnectionField = true;
            }

            headerBuilder.append(inputLine);
            headerBuilder.append("\r\n");
            lineNumber++;
        }
        if (!hasConnectionField)  // force the addition of connection: close if there is no specification
            headerBuilder.append("Connection: close\r\n");

        String header = headerBuilder.toString();

        return new Request(requestedURL, header);
    }

    public Response getServerResponse(Request clientRequest) throws IOException {
        /*
        Returns a Response object that contains the input/output streams of the new socket opened to make http requests
         */
        String httpMethod = clientRequest.getHTTPMethod();

        System.out.println(httpMethod + " " + clientRequest.getUrl());
        httpSocket = new Socket();
        String host = new URL(clientRequest.getUrl()).getHost();

        httpSocket.connect(getCachedDNSAddress(host));

        InputStream inputStream = httpSocket.getInputStream();
        PrintWriter outWriter = new PrintWriter(httpSocket.getOutputStream(), true);
        outWriter.println(clientRequest.getHeader());
        outWriter.flush();

        return new Response(inputStream, outWriter);
    }

    public InetSocketAddress getCachedDNSAddress(final String host) {
        /*
        Returns a cached socket address combination if one exists for a given host.
         */
        InetSocketAddress resolvedAddress = dnsCache.get(host);
        if (resolvedAddress == null) resolvedAddress = new InetSocketAddress(host, 80);
        dnsCache.putIfAbsent(host, resolvedAddress);
        giveEntryTimeToLive(host);
        return resolvedAddress;
    }

    public void giveEntryTimeToLive(final String host) {
        /*
        Helper method to spawn a thread that removes cached entries after 30 seconds of their first addition
         */
        (new Thread() {
            public void run() {
                try {
                    Thread.sleep(30000);  // Wait 30 seconds to delete thread
                    dnsCache.remove(host);
                } catch (InterruptedException e) {
                    // let thread discontinue
                }
            }
        }).start();
    }

    public void closeResources(Response serverResponse) throws IOException {
        /*
        Closes resources that remain open on the Response object (the HTTP socket streams) and also closes the two
        sockets to free up the connections.
         */
        serverResponse.closeStreams();
        if (httpSocket != null) httpSocket.close();
        if (socket != null) socket.close();
    }

    public void run() {
        try {
            Request clientRequest = getClientRequest();
            Response serverResponse = getServerResponse(clientRequest);
            serverResponse.writeToOutputStream(socket.getOutputStream());
            closeResources(serverResponse);
        } catch (IOException e) {
            e.printStackTrace();  // the stack trace for any IOException is usually enough to debug the application
        }
    }
}
