import java.io.BufferedReader;

public class Request {

    private String url;
    private BufferedReader headerReader;

    public Request(String url, BufferedReader headerReader) {
        this.url = url;
        this.headerReader = headerReader;
    }

    public String getUrl() {
        return url;
    }

    public BufferedReader getHeaderReader() {
        return headerReader;
    }

}
