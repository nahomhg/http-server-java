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

public class HttpServer {

    private final ServerSocket serverSocket;
    private final ExecutorService service;
    private final String directory;

    private static final String HTTP_200 = "HTTP/1.1 200 OK\r\n\r\n";
    private static final String HTTP_201 = "HTTP/1.1 201 CREATED\r\n\r\n";
    private static final String HTTP_404 = "HTTP/1.1 404 NOT FOUND\r\n\r\n";


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

    private void handleRequest(Socket socket)  {
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int readByteCount = inputStream.read(buffer);

            if (readByteCount != -1) {
                String requestString = new String(buffer, 0, readByteCount, StandardCharsets.UTF_8);
                CustomHttpRequest httpRequest = getHttpRequest(requestString);
                System.out.println("Dir: "+this.directory+"\nRequest: "+httpRequest);
                if (httpRequest.method().equals("POST")) {
                    handlePost(socket, httpRequest);
                } else if (httpRequest.method().equals("GET")) {
                    handleGet(socket, httpRequest);
                }
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
                int indexOfDirectory = Arrays.asList(this.directory).indexOf("--directory");
                String fileName = customHttpRequest.path().substring(7);
                if (indexOfDirectory != -1 && doesFileExist(this.directory, fileName)) {
                    byte[] fileContent = getFileContent(indexOfDirectory + 1 + fileName);
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

    private void handlePost(Socket socket, CustomHttpRequest customHttpRequest){
        String pathToPost = customHttpRequest.path();
        String fileName = pathToPost.substring(7);
        System.out.println("path: "+pathToPost+"\nfile: "+fileName);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(pathToPost))){
            OutputStream output = socket.getOutputStream();
            //byte[] body = getFileContent(pathToPost);
            writer.write(customHttpRequest.body());
            writer.close();
            output.write(HTTP_201.getBytes());
            output.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static CustomHttpRequest getHttpRequest(String request){
        String[] requestArray = request.split("\r\n");
        String requestMethod = requestArray[0].split("\\s+")[0];
        String requestPath = requestArray[0].split("\\s+")[1];
        HashMap<String, String> headers = new HashMap<>();
        int i = 1;
        while(i <requestArray.length && !requestArray[i].equals("")){
            headers.put(requestArray[i].split(": ")[0],requestArray[i].split(": ")[1]);
            i++;
        }
        String body = (i + 1 < requestArray.length) ? requestArray[i+1] : "";
        return new CustomHttpRequest(requestMethod, requestPath, headers, body);
    }

    private static String getHeader(Map<String, String> headers, String headerName){
        return headers.getOrDefault(headerName, "");
    }

    private static boolean doesFileExist(String directory, String fileName){
        File file = new File(directory+fileName);
        System.out.println("file info: "+file.getPath()+"\t"+fileName);
        return file.exists();
    }

    private static byte[] getFileContent(String filePath) throws IOException {
        return Files.readAllBytes(Path.of(filePath));
    }

}
record CustomHttpRequest(String method, String path, Map<String, String> headers, String body){};


