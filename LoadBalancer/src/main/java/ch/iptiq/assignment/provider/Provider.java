package ch.iptiq.assignment.provider;

public interface Provider {

    String getId();

    String get();

    boolean isAlive();

    void setIsAlive(boolean isAlive);
}
