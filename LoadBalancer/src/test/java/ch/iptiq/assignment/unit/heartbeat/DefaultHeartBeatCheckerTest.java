package ch.iptiq.assignment.unit.heartbeat;

import ch.iptiq.assignment.heartbeat.DefaultHeartBeatChecker;
import ch.iptiq.assignment.provider.DefaultProvider;
import ch.iptiq.assignment.provider.Provider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultHeartBeatCheckerTest {

    @Test
    void heartBeatCheckReturnsTrueIfProviderIsAlive() {
        Provider provider = new DefaultProvider(true);
        DefaultHeartBeatChecker underTest = new DefaultHeartBeatChecker();

        assertTrue(underTest.check(provider));
    }

    @Test
    void heartBeatCheckReturnsFalseIfProviderIsNotAlive() {
        Provider provider = new DefaultProvider(false);
        DefaultHeartBeatChecker underTest = new DefaultHeartBeatChecker();

        assertFalse(underTest.check(provider));
    }
}