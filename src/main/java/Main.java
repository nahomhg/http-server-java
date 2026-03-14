import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {

  public static void main(String[] args) {
     try {
       ServerSocket serverSocket = new ServerSocket(4221);

       // Since the tester restarts your program quite often, setting SO_REUSEADDR
       // ensures that we don't run into 'Address already in use' errors
       serverSocket.setReuseAddress(true);
       ExecutorService service = Executors.newFixedThreadPool(10);
         while(true) {
           Socket socket = serverSocket.accept(); // Wait for connection from client
           service.execute(() -> {
               try {
                   handleRequest(socket, args);
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           });

           System.out.println("accepted new connection");
       }
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }

  private static void handleRequest(Socket socket, String[] argumentsPassed) throws IOException{
      InputStream inputStream = socket.getInputStream();
      byte[] buffer = new byte[1024];
      int readByteCount = inputStream.read(buffer);
      String response_200 = "HTTP/1.1 200 OK\r\n\r\n";
      String response_404 = "HTTP/1.1 404 Not Found\r\n\r\n";


      if(readByteCount != -1) {
          String request = new String(buffer, 0, readByteCount, StandardCharsets.UTF_8);
          CustomHttpRequest customHttpRequest = getHttpRequest(request);
          String output = "";
          String fileContent = "";

          int indexOfDirectory = 0;
          if(argumentsPassed.length > 0) {
              indexOfDirectory = Arrays.asList(argumentsPassed).indexOf("--directory");
              String fileName = customHttpRequest.path().split("/")[1];
              if(indexOfDirectory != -1 && !doesFileExist(argumentsPassed[indexOfDirectory+1],fileName)){
                  System.out.println("got here:\n"+indexOfDirectory+"\t"+doesFileExist(argumentsPassed[indexOfDirectory+1],fileName));
                  output = response_404;
              }else{
                  fileContent = getFileContent(argumentsPassed[indexOfDirectory+1]+fileName);
                  output = "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\\nContent-Length: "+fileContent.length()+"\r\n\r\n"+fileContent+"!\n";
              }
          }

          if(customHttpRequest.path().equals("/")){
              output = response_200;
          }else if(customHttpRequest.path().startsWith("/echo/") && !customHttpRequest.path().substring(6).isEmpty()) {
              String pathStr = customHttpRequest.path().substring(6);
              output = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+pathStr.length()+"\r\n\r\n"+pathStr+"\n";
          }
          else if(customHttpRequest.path().equals("/user-agent")){
              String userAgentContent = getHeader(customHttpRequest.headers(),"User-Agent");
              output = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+userAgentContent.length()+"\r\n\r\n"+userAgentContent+"\n";
          }
          else {
              output = response_404;
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

  private static boolean doesFileExist(String directory, String fileName){
      File file = new File(directory+fileName);
      System.out.println("file: "+file);
      return file.exists();
  }

  private static String getFileContent(String filePath) {
      StringBuilder fileContent = new StringBuilder();
      Path path = new File(filePath).toPath();

      try(BufferedReader br = new
              BufferedReader(
                      new InputStreamReader(Files.newInputStream(path),StandardCharsets.UTF_8)
                      )){
        String line;
        while((line = br.readLine()) != null){
            fileContent.append(line);
            fileContent.append("\n");
        }
        return fileContent.toString();

      }catch(IOException exception){
        exception.getMessage();
      }
      return "";
  }
}


record CustomHttpRequest(String method, String path, Map<String, String> headers, String body){};
