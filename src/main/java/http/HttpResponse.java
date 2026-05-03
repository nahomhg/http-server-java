package http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private HttpStatus status;
    private Map<String, String> headers;
    private byte[] body;

    public HttpResponse(HttpResponseBuilder builder){
        status = builder.httpStatus;
        headers = builder.headers;
        body = builder.body;
    }

    public String getHttpStatus(){
        return status.getCode()+" "+status.getDescription();
    }

    public Map<String, String> getHeaders(){
        return headers;
    }

    public byte[] getBody(){
        return body;
    }

    public static class HttpResponseBuilder{
        private HttpStatus httpStatus;
        private Map<String, String> headers = new HashMap<>();
        private byte[] body;

        public HttpResponseBuilder setHttpStatus(HttpStatus status) {
            this.httpStatus = status;
            return this;
        }

        public HttpResponseBuilder addHeader(String key, String value){
            this.headers.put(key, value);
            return this;
        }

        public HttpResponseBuilder addBody(byte[] body){
            this.body = body;
            return this;
        }

        public HttpResponse build(){
            return new HttpResponse(this);
        }
    }

    public byte[] toByteArray(){
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            String requestMessage = "HTTP/1.1 "+getHttpStatus()+"\r\n";
            byteArrayOutputStream.write(requestMessage.getBytes(StandardCharsets.UTF_8));

            if(headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String header = entry.getKey()+": "+entry.getValue()+"\r\n";
                    byteArrayOutputStream.write(header.getBytes(StandardCharsets.UTF_8));
                }
            }
            // Add end of request message should look like '\r\n\r\n'
            byteArrayOutputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

            if(body != null && body.length > 0)
                byteArrayOutputStream.write(body);

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "http.HttpResponse{" +
                "status=" + status +
                ", headers=" + headers +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}
