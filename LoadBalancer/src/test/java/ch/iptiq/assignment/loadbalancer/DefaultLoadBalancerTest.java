package ch.iptiq.assignment.loadbalancer;

import ch.iptiq.assignment.loadbalancer.strategy.RandomLoadBalancerStrategy;
import ch.iptiq.assignment.provider.DefaultProvider;
import ch.iptiq.assignment.provider.Provider;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultLoadBalancerTest {

    private static final int PROVIDERS_LIMIT = 10;

    private DefaultLoadBalancer underTest;

    @Test
    void getThrowsExceptionIfProvidersAreMissing() {
        underTest = new DefaultLoadBalancer(new RandomLoadBalancerStrategy());
        assertThrows(LoadBalancerException.class, () -> underTest.get());
    }

    @Test
    void registerProvidersDoesNotAcceptNullProviders() {
        underTest = new DefaultLoadBalancer(new RandomLoadBalancerStrategy());
        assertThrows(NullPointerException.class, () -> underTest.registerProviders(null));
    }

    @Test
    void registerProvidersDoesNotAcceptProvidersOverLimit() {
        underTest = new DefaultLoadBalancer(new RandomLoadBalancerStrategy());
        List<Provider> providers = new ArrayList<>();

        for (int i = 0; i < PROVIDERS_LIMIT + 1; i++) {
            providers.add(new DefaultProvider());
        }

        assertThrows(LoadBalancerException.class, () -> underTest.registerProviders(providers));
    }
}