import java.io.BufferedReader;
import java.util.StringTokenizer;

public class Request {

    private String url;
    private String header;
    private BufferedReader headerReader;

    public Request(String url, String header, BufferedReader headerReader) {
        this.url = url;
        this.header = header;
        this.headerReader = headerReader;
    }

    public String getUrl() {
        return url;
    }

    public String getHeader() {
        return header;
    }

    public String getHTTPMethod() {
        String firstLine = header.split("\n")[0];
        String[] tokens = firstLine.split(" ");
        return tokens[0];
    }

    public BufferedReader getHeaderReader() {
        return headerReader;
    }

}
