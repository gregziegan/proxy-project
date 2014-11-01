import java.io.BufferedReader;
import java.util.StringTokenizer;

public class Request {

    private String url;
    private String header;

    public Request(String url, String header) {
        this.url = url;
        this.header = header;
    }

    public String getUrl() {
        return url;
    }

    public String getHost() {
        String secondLine = header.split("\n")[1];
        String[] tokens = secondLine.split(" ");
        return tokens[1];
    }

    public String getHeader() {
        return header;
    }

    public String getHTTPMethod() {
        String firstLine = header.split("\n")[0];
        String[] tokens = firstLine.split(" ");
        return tokens[0];
    }

}
