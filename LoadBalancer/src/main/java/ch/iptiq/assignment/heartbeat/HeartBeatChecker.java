package ch.iptiq.assignment.heartbeat;

import ch.iptiq.assignment.provider.Provider;

public interface HeartBeatChecker {

    boolean check(Provider provider);
}
