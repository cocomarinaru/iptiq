package ch.iptiq.assignment.provider;

import java.util.Objects;
import java.util.UUID;

public class DefaultProvider implements Provider {

    private boolean isAlive = true;
    private final String id;

    public DefaultProvider() {
        this.id = UUID.randomUUID().toString();
    }

    public DefaultProvider(boolean isAlive) {
        this.id = UUID.randomUUID().toString();
        this.isAlive = isAlive;
    }

    @Override
    public String get() {
        return id;
    }

    @Override
    public boolean isAlive() {
        return this.isAlive;
    }

    @Override
    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.id;
    }

}
