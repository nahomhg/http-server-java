package Filter;

import http.HttpResponse;

public interface RequestFilter {
    HttpResponse doFilter(HttpResponse response);
}
