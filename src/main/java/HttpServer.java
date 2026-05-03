import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
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
        this.routerRequest.registerHandler(new HomeHandler());
        this.routerRequest.registerHandler(new EchoEncodingHandler());
        this.routerRequest.registerHandler(new FileHandler(this.directory));
        this.routerRequest.registerHandler(new EchoHandler());
        this.routerRequest.registerHandler(new UserAgentHandler());
        this.routerRequest.registerHandler(new PostHandler(this.directory));

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
        try (socket) {
                InputStream inputStream = socket.getInputStream();
                while (true) {
                    Optional<CustomHttpRequest> httpRequest = RequestParser.parser(inputStream);
                    if (httpRequest.isEmpty()) {
                        break;
                    }
                    CustomHttpRequest request = httpRequest.get();
                    OutputStream output = socket.getOutputStream();
                    HttpResponse response = routerRequest.route(request);
                    System.out.println(request);
                    System.out.println(response);
                    output.write(response.toByteArray());
                }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
//    private void handleGet(Socket socket, CustomHttpRequest customHttpRequest){
//        // Refactored
//        try {
//
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        }
//    }
//
//    private void handlePost(Socket socket, CustomHttpRequest customHttpRequest) {
//// TODO: Refactor code below:
//        try {
//            OutputStream output = socket.getOutputStream();
//            HttpResponse response = routerRequest.route(customHttpRequest);
//            output.write(response.toByteArray());
////            if (!customHttpRequest.path().startsWith("/files/")) {
////                output.write(HTTP_404.getBytes());
////                return;
////            }
////            String fileName = customHttpRequest.path().substring(7);
////            File outputFile = new File(this.directory + fileName);
////            FileOutputStream file = new FileOutputStream(outputFile);
////            file.write(customHttpRequest.body());
////            output.write(HTTP_201.getBytes());
////            //System.out.println(doesFileExist(this.directory, fileName));
////            output.close();
////            file.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


//  private void handleGet(Socket socket, CustomHttpRequest customHttpRequest){
// TODO: Refactor
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
// }


