import java.util.Map;

public record CustomHttpRequest(String method, String path, Map<String, String> headers, byte[] body) {
}
