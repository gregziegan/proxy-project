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

    public String getHeader() {
        return header;
    }

    public String getHTTPMethod() {
        String firstLine = header.split("\n")[0];
        String[] tokens = firstLine.split(" ");
        return tokens[0];
    }

}
