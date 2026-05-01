import java.util.Map;

record CustomHttpRequest(String method, String path, Map<String, String> headers, byte[] body) {
}
