import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // TODO: Uncomment the code below to pass the first stage
    //
     try {
       ServerSocket serverSocket = new ServerSocket(4221);

       // Since the tester restarts your program quite often, setting SO_REUSEADDR
       // ensures that we don't run into 'Address already in use' errors
       serverSocket.setReuseAddress(true);

       Socket socket = serverSocket.accept(); // Wait for connection from client
       ExecutorService service = Executors.newFixedThreadPool(10);
       service.execute(() -> {
           try {
               handleRequest(socket);
           } catch (IOException e) {
               e.printStackTrace();
           }
       });

       System.out.println("accepted new connection");
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }

  private static void handleRequest(Socket socket) throws IOException{
      InputStream inputStream = socket.getInputStream();
      byte[] buffer = new byte[1024];
      int readByteCount = inputStream.read(buffer);
      if(readByteCount != -1) {
          
          String request = new String(buffer, 0, readByteCount, StandardCharsets.UTF_8);
          CustomHttpRequest customHttpRequest = getHttpRequest(request);
          String output = "";

          if(customHttpRequest.path().equals("/")){
              output = "HTTP/1.1 200 OK\r\n\r\n";
          }else if(customHttpRequest.path().startsWith("/echo/") && !customHttpRequest.path().substring(6).isEmpty()) {
              String pathStr = customHttpRequest.path().substring(6);
              output = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+pathStr.length()+"\r\n\r\n"+pathStr+"\n";
          }
          else if(customHttpRequest.path().equals("/user-agent")){
              String userAgentContent = getHeader(customHttpRequest.headers(),"User-Agent");
              output = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+userAgentContent.length()+"\r\n\r\n"+userAgentContent+"\n";
          }
          else {
              output = "HTTP/1.1 404 Not Found\r\n\r\n";
          }

          socket.getOutputStream().write(output.getBytes());

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
}


record CustomHttpRequest(String method, String path, Map<String, String> headers, String body){};
