import java.net.*;
import java.io.*;
import java.util.*;

public class ProxyWorker extends Thread {
    private Socket socket = null;
    private static final int BUFFER_SIZE = 32768;
    public ProxyWorker(Socket socket) {
        super("ProxyThread");
        this.socket = socket;
    }

    public String getRequestInfo() throws IOException {
        DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        String inputLine, outputLine;
        int cnt = 0;
        String destinationURL = "";

        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
            try {
                StringTokenizer tok = new StringTokenizer(inputLine);
                tok.nextToken();
            } catch (Exception e) {
                break;
            }
            //parse the first line of the request to find the url
            if (cnt == 0) {
                String[] tokens = inputLine.split(" ");
                destinationURL = tokens[1];
                //can redirect this to output log
                System.out.println("Request for : " + destinationURL);
            }

            cnt++;
        }
        return destinationURL;
    }

    public BufferedReader getServerResponse(String urlString) throws MalformedURLException, IOException {
        System.out.println("sending request to server for url: " + urlString);
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        conn.setDoInput(true);
        //not doing HTTP posts (yet)
        conn.setDoOutput(false);
        System.out.println("Content-Type: " + conn.getContentType());
        System.out.println("Content-Length: " + conn.getContentLength());
        System.out.println("Content-Encoding: " + conn.getContentEncoding());

        BufferedReader response = null;
        // Get the response
        InputStream is = null;
        HttpURLConnection huc = (HttpURLConnection)conn;
        if (conn.getContentLength() > 0) {
            try {
                is = conn.getInputStream();
                response = new BufferedReader(new InputStreamReader(is));
            } catch (IOException ioe) {
                System.out.println(ioe.toString());
            }
        }
        return response;
    }

    public void sendResponseToClient() {

        byte by[] = new byte[ BUFFER_SIZE ];
        int index = is.read( by, 0, BUFFER_SIZE );
        while ( index != -1 )
        {
            out.write( by, 0, index );
            index = is.read( by, 0, BUFFER_SIZE );
        }
        out.flush();
    }

    public void cleanUp() {

    }

    public void run() {
        //send response to user

        try {

            String destinationURL = getRequestInfo();


            //close out all resources
            if (rd != null) {
                rd.close();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}