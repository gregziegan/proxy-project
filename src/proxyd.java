import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class proxyd {


    public static void main(String[] args) throws IOException {
        java.security.Security.setProperty("networkaddress.cache.ttl", "0");
        ServerSocket serverSocket = null;

        int port = Integer.parseInt(args[1]);
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on: " + port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        }

        ConcurrentMap<String, InetSocketAddress> dnsCache = new ConcurrentHashMap<String, InetSocketAddress>();
        boolean listening = true;
        while (listening) {
            new ProxyWorker(serverSocket.accept(), dnsCache).start();
        }
        serverSocket.close();
    }
}
