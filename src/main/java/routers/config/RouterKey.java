package routers.config;

import java.util.Objects;

public class RouterKey {

    private final String method;
    private final String route;

    public RouterKey(String method, String route) {
        this.method = method;
        this.route = route;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouterKey that = (RouterKey) o;
        return Objects.equals(method, that.method) && Objects.equals(route, that.route);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, route);
    }
}
