import java.io.*;

public class Response {
    private static final int BUFFER_SIZE = ProxyWorker.BUFFER_SIZE;
    private InputStream contentStream;
    private PrintWriter outWriter;

    public Response(InputStream contentStream, PrintWriter outWriter) {
        this.contentStream = contentStream;
        this.outWriter = outWriter;
    }

    public void writeToOutputStream(OutputStream outputStream) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        byte responseBytes[] = new byte[BUFFER_SIZE];
        int index = contentStream.read(responseBytes, 0, BUFFER_SIZE);
        while (index != -1) {
            dataOutputStream.write(responseBytes, 0, index);
            index = contentStream.read(responseBytes, 0, BUFFER_SIZE);
        }
        dataOutputStream.flush();
    }

    public void closeStreams() throws IOException {
        if (outWriter != null) outWriter.close();
        if (contentStream != null) contentStream.close();
    }
}
