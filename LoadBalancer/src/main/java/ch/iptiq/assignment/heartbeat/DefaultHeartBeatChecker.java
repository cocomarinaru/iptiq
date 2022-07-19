package ch.iptiq.assignment.heartbeat;

import ch.iptiq.assignment.provider.Provider;

public class DefaultHeartBeatChecker implements HeartBeatChecker {
    @Override
    public boolean check(Provider provider) {
        return provider.isAlive();
    }
}
