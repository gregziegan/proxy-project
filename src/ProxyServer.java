import java.net.*;
import java.io.*;

public class ProxyServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;

        int port = Integer.parseInt(args[0]);

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started on: " + port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + args[0]);
            System.exit(-1);
        }

        boolean listening = true;
        while (listening) {
            new ProxyWorker(serverSocket.accept()).start();
        }
        serverSocket.close();
    }
}
