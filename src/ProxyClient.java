import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ProxyClient {

    public static void main(String[] args) throws IOException {
        String serverAddress = args[0];
        int port = Integer.parseInt(args[1]);
        Socket s = new Socket(serverAddress, port);
        BufferedReader input =
                new BufferedReader(new InputStreamReader(s.getInputStream()));
        String answer = input.readLine();
        System.exit(0);
    }
}
