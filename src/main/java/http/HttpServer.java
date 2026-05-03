package http;

import routers.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private final ServerSocket serverSocket;
    private final ExecutorService service;
    private final String directory;
    private final Router routerRequest;

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
        try {
            while (true) {
                Socket socket = this.serverSocket.accept();
                service.execute(() -> {
                    handleRequest(socket);
                });
            }
        } catch (IOException e) {
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
                if(request.headers().containsKey("Connection") && request.headers().get("Connection").equalsIgnoreCase(" close")){
                    break;
                }
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