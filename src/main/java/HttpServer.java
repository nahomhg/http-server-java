import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class HttpServer {

    private final ServerSocket serverSocket;
    private final ExecutorService service;
    private final String directory;
    private final Router routerRequest;
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
        this.routerRequest = new Router();
        this.routerRequest.registerHandler(new EchoEncodingHandler());
        this.routerRequest.registerHandler(new FileHandler(this.directory));
        this.routerRequest.registerHandler(new EchoHandler());

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
        // TODO Refactor method
        try {
            OutputStream output = socket.getOutputStream();
            HttpResponse response = routerRequest.route(customHttpRequest);
            System.out.println(customHttpRequest);
            output.write(customHttpRequest.body());
            System.out.println(response);
            output.write(response.toByteArray());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
//        try {
//            String response = "";
//            OutputStream output = socket.getOutputStream();
//            HttpResponse httpResponse = router.route(customHttpRequest);
//            if (customHttpRequest.path().startsWith("/files/")) {
//                String fileName = customHttpRequest.path().substring(7);
//                if (doesFileExist(this.directory, fileName)) {
//                    byte[] fileContent = getFileContent(this.directory + fileName);
//                    String header = "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
//                    OutputStream out = socket.getOutputStream();
//                    out.write(header.getBytes(StandardCharsets.UTF_8));
//                    out.write(fileContent);
//
//                } else {
//                    response = HTTP_404;
//                }
//            } else if (customHttpRequest.path().equals("/")) {
//                response = HTTP_200;
//            } else if (customHttpRequest.path().startsWith("/echo/") && !customHttpRequest.path().substring(6).isEmpty()) {
//                String pathStr = customHttpRequest.path().substring(6);
//                if(customHttpRequest.headers().containsKey("Accept-Encoding")){
//                    String encodingType = extractEncoding(customHttpRequest.headers().get("Accept-Encoding"));
//                    response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Encoding: "+encodingType+"\r\nContent-Length\r\n\r\n";
//                }else {
//                    response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + pathStr.length() + "\r\n\r\n" + pathStr + "\n";
//                }
//            } else if (customHttpRequest.path().equals("/user-agent")) {
//                String userAgentContent = getHeader(customHttpRequest.headers(), "User-Agent");
//                response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + userAgentContent.length() + "\r\n\r\n" + userAgentContent + "\n";
//            } else {
//                response = HTTP_404;
//            }
//            output.write(response.getBytes());
//            output.close();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
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

    private String extractEncoding(String encodingHeader){
        if(encodingHeader.toLowerCase().contains("gzip")){
            for(String encoding : encodingHeader.split(", ")){
                if(encoding.equalsIgnoreCase("gzip")){
                    return encoding;
                }
            }
        }
        return "";
    }
}


