package ch.iptiq.assignment.heartbeat;

import ch.iptiq.assignment.provider.Provider;

import java.util.logging.Logger;

public class DefaultHeartBeatChecker implements HeartBeatChecker {

    private static final Logger LOG = Logger.getLogger(DefaultHeartBeatChecker.class.getName());

    @Override
    public boolean check(Provider provider) {
        boolean alive = provider.isAlive();
        LOG.info(String.format("Provider %s is alive: %b", provider.getId(), alive));
        return alive;
    }
}
