//package backup;
//
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static java.util.Map.entry;
//
//public class Main {
//
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
//    private static void handleRequest(Socket socket, String[] argumentsPassed) throws IOException{
//        InputStream inputStream = socket.getInputStream();
//        byte[] buffer = new byte[1024];
//        int readByteCount = inputStream.read(buffer);
//        String response_200 = "HTTP/1.1 200 OK\r\n\r\n";
//        String response_404 = "HTTP/1.1 404 Not Found\r\n\r\n";
//
//        if(readByteCount != -1) {
//            String request = new String(buffer, 0, readByteCount, StandardCharsets.UTF_8);
//            CustomHttpRequest customHttpRequest = getHttpRequest(request);
//            String response = "";
//            byte[] fileContent;
//            int indexOfDirectory = 0;
//
//            if(argumentsPassed.length > 0 && customHttpRequest.path().startsWith("/files/")) {
//                indexOfDirectory = Arrays.asList(argumentsPassed).indexOf("--directory");
//                String fileName = customHttpRequest.path().split("/")[2];
//                if (indexOfDirectory != -1 && doesFileExist(argumentsPassed[indexOfDirectory + 1], fileName)) {
//                    fileContent = getFileContent(argumentsPassed[indexOfDirectory + 1] + fileName);
//                    String header = "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
//
//                    CustomHttpResponse httpResponse = new CustomHttpResponse.CustomHttpResponseBuilder()
//                            .httpVersion("HTTP/1.1")
//                            .statusCode("200 OK")
//                            .headers(Map.ofEntries(
//                                    entry("Content-Type: ","application/octet-stream"),
//                                    entry("Content-Length: ",String.valueOf(fileContent.length))
//                            )).build();
//
//                    OutputStream out = socket.getOutputStream();
//                    out.write(header.getBytes(StandardCharsets.UTF_8));
//                    out.write(fileContent);
//                /*
//                fileContent = getFileContent(argumentsPassed[indexOfDirectory + 1] + fileName);
//                byte[] customResponse = new CustomHttpResponse().setHttpType("HTTP/1.1").setStatusCode("200 OK").setHeaders(new HashMap<>("Content-Type" : "application/octet-stream", "Content-Length":fileContent.length));
//                OutputStream out = socket.getOutputStream();
//                  out.write(customResponse);
//                  out.write(fileContent);
//                                   */
//                } else {
//                    response = response_404;
//                }
//            }
//            else if(customHttpRequest.path().equals("/")){
//                response = response_200;
//            }else if(customHttpRequest.path().startsWith("/echo/") && !customHttpRequest.path().substring(6).isEmpty()) {
//                String pathStr = customHttpRequest.path().substring(6);
//                response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+pathStr.length()+"\r\n\r\n"+pathStr+"\n";
//            }
//            else if(customHttpRequest.path().equals("/user-agent")){
//                String userAgentContent = getHeader(customHttpRequest.headers(),"User-Agent");
//                response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "+userAgentContent.length()+"\r\n\r\n"+userAgentContent+"\n";
//            }
//            else {
//                response = response_404;
//            }
//
//            socket.getOutputStream().write(response.getBytes());
//        }
//    }
//
//    private static CustomHttpRequest getHttpRequest(String request){
//        String[] requestArray = request.split("\r\n");
//        String requestMethod = requestArray[0].split("\\s+")[0];
//        String requestPath = requestArray[0].split("\\s+")[1];
//        HashMap<String, String> headers = new HashMap<>();
//        int i = 1;
//        while(i <requestArray.length && !requestArray[i].equals("")){
//            headers.put(requestArray[i].split(": ")[0],requestArray[i].split(": ")[1]);
//            i++;
//        }
//        String body = (i + 1 < requestArray.length) ? requestArray[i+1] : "";
//        return new CustomHttpRequest(requestMethod, requestPath, headers, body);
//    }
//
//
//    private static String getHeader(Map<String, String> headers, String headerName){
//        return headers.getOrDefault(headerName, "");
//    }
//
//    private static boolean doesFileExist(String directory, String fileName){
//        File file = new File(directory+fileName);
//        System.out.println("file info: "+file.getPath()+"\t"+fileName);
//        return file.exists();
//    }
//
//    private static byte[] getFileContent(String filePath) throws IOException {
//        return Files.readAllBytes(Path.of(filePath));
//    }
//
////  private static void manageFiles(CustomHttpRequest customHttpRequest, String[] argumentsPassed, Socket socket){
////      try {
////          byte[] fileContent = "";
////          String header = "";
////
////          int indexOfDirectory = Arrays.asList(argumentsPassed).indexOf("--directory");
////          String fileName = customHttpRequest.path().split("/")[2];
////          if (indexOfDirectory != -1 && doesFileExist(argumentsPassed[indexOfDirectory + 1], fileName)) {
////              System.out.println("got here:\n" + indexOfDirectory + "\t" + doesFileExist(argumentsPassed[indexOfDirectory + 1], fileName));
////              fileContent = getFileContent(argumentsPassed[indexOfDirectory + 1] + fileName);
////              System.out.println("file: " + Arrays.toString(fileContent) + "\tlength: " + fileContent.length);
////              header = "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
////              OutputStream out = socket.getOutputStream();
////              out.write(header.getBytes(StandardCharsets.UTF_8));
////              out.write(fileContent);
////          } else {
////              response = response_404;
////          }
////      }catch(IOException e){
////          e.printStackTrace();
////      }
//}
//
//
//
//record CustomHttpRequest(String method, String path, Map<String, String> headers, String body){};
//
//class CustomHttpResponse{
//
//    private String httpType;
//    private String statusCode;
//    private Map<String, String> headers;
//    private byte[] body;
//
//    public CustomHttpResponse(CustomHttpResponseBuilder builder){
//        this.httpType = builder.httpVersion;
//        this.statusCode = builder.statusCode;
//        this.headers = builder.headers;
//        this.body = builder.body;
//    }
//
//    public String getHttpType() {
//        return httpType;
//    }
//
//    public String getStatusCode() {
//        return statusCode;
//    }
//
//    public Map<String, String> getHeaders() {
//        return headers;
//    }
//
//    public byte[] getBody() {
//        return body;
//    }
//
//    public static class CustomHttpResponseBuilder{
//        private String httpVersion;
//        private String statusCode;
//        private Map<String, String> headers;
//        private byte[] body;
//
//        public CustomHttpResponseBuilder httpVersion(String httpType){
//            this.httpVersion = httpType;
//            return this;
//        }
//
//        public CustomHttpResponseBuilder statusCode(String statusCode){
//            this.statusCode = statusCode;
//            return this;
//        }
//
//        public CustomHttpResponseBuilder headers(Map<String, String> headers){
//            this.headers = headers;
//            return this;
//        }
//
//        public CustomHttpResponseBuilder body(byte[] body){
//            this.body = body;
//            return this;
//        }
//
//        public CustomHttpResponse build(){
//            return new CustomHttpResponse(this);
//        }
//
//    }
//    public void send(OutputStream outputStream) throws IOException {
//        StringBuilder responseText = new StringBuilder();
//        responseText.append(httpVersion).append(" ").append(statusCode).append("\r\n");
//        for(String i : headers.keySet()){
//            responseText.append(i).append(": ").append(headers.get(i));
//        }
//        responseText.append("\r\n\r\n");
//        outputStream.write(responseText.toString().getBytes(StandardCharsets.UTF_8));
//        if(body!= null){
//            outputStream.write(body);
//        }
//    }
//    /*
//
//     */
//}