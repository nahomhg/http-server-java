import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class HttpServer {

    private final ServerSocket serverSocket;
    private final ExecutorService service;
    private final String directory;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private static final String HTTP_200 = "HTTP/1.1 200 OK\r\n\r\n";
    private static final String HTTP_201 = "HTTP/1.1 201 Created\r\n\r\n";
    private static final String HTTP_404 = "HTTP/1.1 404 Not Found\r\n\r\n";

    public HttpServer(ServerSocket socket) throws SocketException {
        this(socket, "");
    }

    public HttpServer(ServerSocket serverSocket, String directory) throws SocketException {
        this.serverSocket = serverSocket;
        this.directory = directory;
        this.service = Executors.newFixedThreadPool(10);
        this.serverSocket.setReuseAddress(true);

    }

    public void startServer() {
        try{
            while(true){
                Socket socket = this.serverSocket.accept();
                service.execute(() -> {
                        handleRequest(socket);
                });
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void handleRequest(Socket socket) {
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            CustomHttpRequest httpRequest = RequestParser.parser(inputStream);

            if (httpRequest.method().equals("POST")) {
                handlePost(socket, httpRequest);
            } else {
                handleGet(socket, httpRequest);
            }
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void handleGet(Socket socket, CustomHttpRequest customHttpRequest){
        try {
            String response = "";
            OutputStream output = socket.getOutputStream();
            if (customHttpRequest.path().startsWith("/files/")) {
                String fileName = customHttpRequest.path().substring(7);
                if (doesFileExist(this.directory, fileName)) {
                    byte[] fileContent = getFileContent(this.directory + fileName);
                    String header = "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
                    OutputStream out = socket.getOutputStream();
                    out.write(header.getBytes(StandardCharsets.UTF_8));
                    out.write(fileContent);

                } else {
                    response = HTTP_404;
                }
            } else if (customHttpRequest.path().equals("/")) {
                response = HTTP_200;
            } else if (customHttpRequest.path().startsWith("/echo/") && !customHttpRequest.path().substring(6).isEmpty()) {
                String pathStr = customHttpRequest.path().substring(6);
                response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + pathStr.length() + "\r\n\r\n" + pathStr + "\n";
            } else if (customHttpRequest.path().equals("/user-agent")) {
                String userAgentContent = getHeader(customHttpRequest.headers(), "User-Agent");
                response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + userAgentContent.length() + "\r\n\r\n" + userAgentContent + "\n";
            } else {
                response = HTTP_404;
            }
            output.write(response.getBytes());
            output.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void handlePost(Socket socket, CustomHttpRequest customHttpRequest) {
        try {
            OutputStream output = socket.getOutputStream();
            if (!customHttpRequest.path().startsWith("/files/")) {
                output.write(HTTP_404.getBytes());
                return;
            }
            String fileName = customHttpRequest.path().substring(7);
            File outputFile = new File(this.directory + fileName);

            FileOutputStream file = new FileOutputStream(outputFile);

            file.write(customHttpRequest.body());
            output.write(HTTP_201.getBytes());
            System.out.println(doesFileExist(this.directory, fileName));
            output.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private String getHeader(Map<String, String> headers, String headerName){
        return headers.getOrDefault(headerName, "");
    }

    private boolean doesFileExist(String directory, String fileName){
        File file = new File(directory+fileName);
        System.out.println("file info: "+file.getPath()+"\t"+fileName);
        return file.exists();
    }

    private byte[] getFileContent(String filePath) throws IOException {
        return Files.readAllBytes(Path.of(filePath));
    }

}

class RequestParser{

    public static CustomHttpRequest parser(InputStream inputStream) {
        try {
            byte[] buffer = new byte[1024];
            int readByteCount = inputStream.read(buffer);
            if (readByteCount != -1) {
                int messageHeaderLength = -1;
                int payloadIndex = -1;

                for (int i = 0; i <= readByteCount; i++) {
                    if ((i + 3 < readByteCount) && buffer[i] == 13 && buffer[i + 1] == 10 && buffer[i + 2] == 13 && buffer[i + 3] == 10) {
                        messageHeaderLength = i;
                        payloadIndex = messageHeaderLength + 4;
                        break;
                    } else if (i == readByteCount) {
                        messageHeaderLength = i;
                    }
                }

                byte[] payloadContent = Arrays.copyOfRange(buffer, payloadIndex, readByteCount);

                String requestString = new String(buffer, 0, messageHeaderLength, StandardCharsets.UTF_8);
                int expectedContentLength = 0;
                for (String line : requestString.split("\r\n")) {
                    if (line.toLowerCase().startsWith("content-length:")) {
                        expectedContentLength = Integer.parseInt(line.substring(15).trim());
                        break;
                    }
                }
                int remainingBytes = expectedContentLength - payloadContent.length;
                byte[] completePayload = payloadContent;
                if (remainingBytes > 0) {
                    byte[] restOfPayload = new byte[remainingBytes];
                    int totalRead = 0;
                    while (totalRead < remainingBytes) {
                        int bytesRead = inputStream.read(restOfPayload, totalRead, remainingBytes - totalRead);
                        if (bytesRead == -1) {
                            break; // Client has disconnected;
                        }
                        totalRead += bytesRead;
                    }
                    completePayload = new byte[expectedContentLength];
                    System.arraycopy(payloadContent, 0, completePayload, 0, payloadContent.length);
                    System.arraycopy(restOfPayload, 0, completePayload, payloadContent.length, restOfPayload.length);
                }
                return mapHttpRequest(requestString, completePayload);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("error");
    }

    private static CustomHttpRequest mapHttpRequest(String request, byte[] payload){

        String[] requestArray = request.split("\r\n");
        String requestMethod = requestArray[0].split("\\s+")[0];
        String requestPath = requestArray[0].split("\\s+")[1];
        HashMap<String, String> headers = new HashMap<>();
        int i = 1;
        while(i < requestArray.length && !requestArray[i].equals("")){
            headers.put(requestArray[i].split(": ")[0],requestArray[i].split(": ")[1]);
            i++;
        }
        return new CustomHttpRequest(requestMethod, requestPath, headers, payload);
    }
}

record CustomHttpRequest(String method, String path, Map<String, String> headers, byte[] body){};


