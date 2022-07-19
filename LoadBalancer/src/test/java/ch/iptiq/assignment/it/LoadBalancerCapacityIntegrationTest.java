package ch.iptiq.assignment.it;

import ch.iptiq.assignment.loadbalancer.DefaultLoadBalancer;
import ch.iptiq.assignment.loadbalancer.LoadBalancer;
import ch.iptiq.assignment.loadbalancer.exception.LoadBalancerException;
import ch.iptiq.assignment.loadbalancer.exception.TooManyRequestsException;
import ch.iptiq.assignment.loadbalancer.strategy.RoundRobinBalancerStrategy;
import ch.iptiq.assignment.provider.DefaultProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoadBalancerCapacityIntegrationTest {

    private LoadBalancer loadBalancer;

    @BeforeEach
    public void setup() {
        loadBalancer = new DefaultLoadBalancer(new RoundRobinBalancerStrategy());
        loadBalancer.start();
    }

    @AfterEach
    public void cleanup() {
        loadBalancer.stop();
    }

    @Test
    public void exceptionIsThrownWhenLoadBalancerHasTooManyRequests() throws LoadBalancerException {

        loadBalancer.registerProviders(List.of(new DefaultProvider(), new DefaultProvider()));
        loadBalancer.setProviderCapacity(1);

        loadBalancer.send();
        loadBalancer.send();

        assertThrows(TooManyRequestsException.class, () -> loadBalancer.send());
    }

    @Test
    public void tooManyRequestsExceptionIsNotThrownIfIncludingMoreProviders() throws LoadBalancerException {

        loadBalancer.registerProviders(List.of(new DefaultProvider(), new DefaultProvider()));
        loadBalancer.setProviderCapacity(1);

        loadBalancer.send();
        loadBalancer.send();

        loadBalancer.include(new DefaultProvider());

        assertDoesNotThrow(() -> loadBalancer.send());
    }

    @Test
    public void tooManyRequestsExceptionIsThrownIfExcludingProviders() throws LoadBalancerException {

        DefaultProvider provider1 = new DefaultProvider();
        DefaultProvider provider2 = new DefaultProvider();
        DefaultProvider provider3 = new DefaultProvider();
        loadBalancer.registerProviders(List.of(provider1, provider2, provider3));
        loadBalancer.setProviderCapacity(1);

        loadBalancer.send();
        loadBalancer.send();

        loadBalancer.exclude(provider2);

        assertThrows(TooManyRequestsException.class, () -> loadBalancer.send());
    }
}
