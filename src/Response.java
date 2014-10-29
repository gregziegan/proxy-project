import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Response {
    private static final int BUFFER_SIZE = ProxyWorker.BUFFER_SIZE;
    private InputStream contentStream;
    private BufferedReader contentReader;
    private DataOutputStream outputStream;

    public Response(InputStream contentStream, BufferedReader contentReader, DataOutputStream outputStream) {
        this.contentStream = contentStream;
        this.contentReader = contentReader;
        this.outputStream = outputStream;
    }

    public BufferedReader getContentReader() {
        return contentReader;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public void writeToOutputStream() throws IOException {
        byte responseBytes[] = new byte[BUFFER_SIZE];
        int index = contentStream.read(responseBytes, 0, BUFFER_SIZE);
        while (index != -1) {
            outputStream.write(responseBytes, 0, index);
            index = contentStream.read(responseBytes, 0, BUFFER_SIZE);
        }
        outputStream.flush();
    }

}
