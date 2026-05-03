package http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class RequestParser {

    public static Optional<CustomHttpRequest> parser(InputStream inputStream) {
        try {
            byte[] buffer = new byte[1024];
            int readByteCount = inputStream.read(buffer);
            if(readByteCount == -1){
                return Optional.empty();
            }

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
                return Optional.of(mapHttpRequest(requestString, completePayload));

        } catch (IOException e) {
            System.err.println("Read Error: Unable to read input stream");
        }
        return Optional.empty();
    }

    private static CustomHttpRequest mapHttpRequest(String request, byte[] payload) {

        String[] requestArray = request.split("\r\n");
        String requestMethod = requestArray[0].split("\\s+")[0];
        String requestPath = requestArray[0].split("\\s+")[1];
        HashMap<String, String> headers = new HashMap<>();
        System.out.println("ReqArr: " + Arrays.toString(requestArray));
        int i = 1;
        while (i < requestArray.length && !requestArray[i].equals("")) {
            headers.put(requestArray[i].split(": ")[0], requestArray[i].split(": ")[1]);
            i++;
        }
        System.out.println(headers);

        return new CustomHttpRequest(requestMethod, requestPath, headers, payload);
    }
}
