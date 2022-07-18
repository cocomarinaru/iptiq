package ch.iptiq.assignment.provider;

import java.util.Objects;
import java.util.UUID;

public class DefaultProvider implements Provider {

    private final String id;

    public DefaultProvider() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public String get() {
        return id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultProvider that = (DefaultProvider) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
