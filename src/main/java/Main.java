import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        int indexOfDirectory = Arrays.asList(args).indexOf("--directory");
        try{
            HttpServer httpServer = new HttpServer(new ServerSocket(4221), args[indexOfDirectory+1]);
            httpServer.startServer();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}

//    public static void main(String[] args) {
//        try {
//            ServerSocket serverSocket = new ServerSocket(4221);
//
//            // Since the tester restarts your program quite often, setting SO_REUSEADDR
//            // ensures that we don't run into 'Address already in use' errors
//            serverSocket.setReuseAddress(true);
//            ExecutorService service = Executors.newFixedThreadPool(10);
//            while(true) {
//                Socket socket = serverSocket.accept(); // Wait for connection from client
//                service.execute(() -> {
//                    try {
//                        handleRequest(socket, args);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//
//                System.out.println("accepted new connection");
//            }
//        } catch (IOException e) {
//            System.out.println("IOException: " + e.getMessage());
//        }
//    }
//
//  private static void handleRequest(Socket socket, String[] argumentsPassed) throws IOException{
//      InputStream inputStream = socket.getInputStream();
//      byte[] buffer = new byte[1024];
//      int readByteCount = inputStream.read(buffer);
//      String response_200 = "HTTP/1.1 200 OK\r\n\r\n";
//      String response_404 = "HTTP/1.1 404 Not Found\r\n\r\n";
//
//      if(readByteCount != -1) {
//          String request = new String(buffer, 0, readByteCount, StandardCharsets.UTF_8);
//          CustomHttpRequest customHttpRequest = getHttpRequest(request);
//          String response = "";
//          byte[] fileContent;
//          int indexOfDirectory = 0;
//
//          // TODO: Re-write this entire 'handleRequest' method to delegate to separate method to:
//          if(customHttpRequest.method().equals("POST")){
//            // TODO: private 'POST' method handler
//          } else if(customHttpRequest.method().equals("GET")){
//              // TODO: private 'GET' method handler
//          }
//
//          if(argumentsPassed.length > 0 && customHttpRequest.path().startsWith("/files/")) {
//              indexOfDirectory = Arrays.asList(argumentsPassed).indexOf("--directory");
//              String fileName = customHttpRequest.path().substring(7);
//              if (indexOfDirectory != -1 && doesFileExist(argumentsPassed[indexOfDirectory + 1], fileName)) {
//                  fileContent = getFileContent(argumentsPassed[indexOfDirectory + 1] + fileName);
//                  String header = "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
//                  OutputStream out = socket.getOutputStream();
//                  out.write(header.getBytes(StandardCharsets.UTF_8));
//                  out.write(fileContent);
//
//              } else {
//                  response = response_404;
//              }
//          }
//          else if(customHttpRequest.path().equals("/")){
//              response = response_200;
//          }else if(customHttpRequest.path().startsWith("/echo/") && !customHttpRequest.path().substring(6).isEmpty()) {
//              String pathStr = customHttpRequest.path().substring(6);
//              response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+pathStr.length()+"\r\n\r\n"+pathStr+"\n";
//          }
//          else if(customHttpRequest.path().equals("/user-agent")){
//              String userAgentContent = getHeader(customHttpRequest.headers(),"User-Agent");
//              response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+userAgentContent.length()+"\r\n\r\n"+userAgentContent+"\n";
//          }
//          else {
//              response = response_404;
//          }
//
//          socket.getOutputStream().write(response.getBytes());
//      }
//  }
//
//  private static String handleGet(Socket socket, String[] args){
//
//  }
//
//  private static CustomHttpRequest getHttpRequest(String request){
//      String[] requestArray = request.split("\r\n");
//      String requestMethod = requestArray[0].split("\\s+")[0];
//      String requestPath = requestArray[0].split("\\s+")[1];
//      System.out.println("reqest "+requestPath);
//      HashMap<String, String> headers = new HashMap<>();
//      int i = 1;
//      while(i <requestArray.length && !requestArray[i].equals("")){
//          headers.put(requestArray[i].split(": ")[0],requestArray[i].split(": ")[1]);
//          i++;
//      }
//      String body = (i + 1 < requestArray.length) ? requestArray[i+1] : "";
//      return new CustomHttpRequest(requestMethod, requestPath, headers, body);
//  }
//
//
//  private static String getHeader(Map<String, String> headers, String headerName){
//      return headers.getOrDefault(headerName, "");
//  }
//
//  private static boolean doesFileExist(String directory, String fileName){
//      File file = new File(directory+fileName);
//      System.out.println("file info: "+file.getPath()+"\t"+fileName);
//      return file.exists();
//  }
//
//  private static byte[] getFileContent(String filePath) throws IOException {
//      return Files.readAllBytes(Path.of(filePath));
//  }
//
//
//}
//
//
//
