package ch.iptiq.assignment.it;

import ch.iptiq.assignment.loadbalancer.DefaultLoadBalancer;
import ch.iptiq.assignment.loadbalancer.LoadBalancer;
import ch.iptiq.assignment.loadbalancer.exception.LoadBalancerException;
import ch.iptiq.assignment.loadbalancer.strategy.RoundRobinBalancerStrategy;
import ch.iptiq.assignment.provider.DefaultProvider;
import ch.iptiq.assignment.provider.Provider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoadBalancerIntegrationTests {

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
    public void loadBalancerCallsRandomProvider() throws LoadBalancerException, ExecutionException, InterruptedException {
        Provider provider1 = new DefaultProvider();
        Provider provider2 = new DefaultProvider();
        Provider provider3 = new DefaultProvider();

        loadBalancer.registerProviders(List.of(provider1, provider2, provider3));

        Future<String> actualResponse1 = loadBalancer.send();
        Future<String> actualResponse2 = loadBalancer.send();

        List<String> possibleResponses = List.of(provider1.get(), provider2.get(), provider3.get());
        assertTrue(possibleResponses.contains(actualResponse1.get()));
        assertTrue(possibleResponses.contains(actualResponse2.get()));
    }

    @Test
    public void roundRobinLoadBalancerCallsProvidersSequentially() throws LoadBalancerException, ExecutionException, InterruptedException {
        Provider provider1 = new DefaultProvider();
        Provider provider2 = new DefaultProvider();
        Provider provider3 = new DefaultProvider();

        loadBalancer.registerProviders(List.of(provider1, provider2, provider3));

        Future<String> actualResponse1 = loadBalancer.send();
        Future<String> actualResponse2 = loadBalancer.send();
        Future<String> actualResponse3 = loadBalancer.send();
        Future<String> actualResponse4 = loadBalancer.send();

        assertEquals(provider1.get(), actualResponse1.get());
        assertEquals(provider2.get(), actualResponse2.get());
        assertEquals(provider3.get(), actualResponse3.get());
        assertEquals(provider1.get(), actualResponse4.get());
    }

    @Test
    public void loadBalancerExcludesNode() throws LoadBalancerException, ExecutionException, InterruptedException {

        Provider provider1 = new DefaultProvider();
        Provider provider2 = new DefaultProvider();
        Provider provider3 = new DefaultProvider();

        loadBalancer.registerProviders(List.of(provider1, provider2, provider3));

        Future<String> actualResponse1 = loadBalancer.send();
        Future<String> actualResponse2 = loadBalancer.send();
        Future<String> actualResponse3 = loadBalancer.send();

        assertEquals(provider1.getId(), actualResponse1.get());
        assertEquals(provider2.getId(), actualResponse2.get());
        assertEquals(provider3.getId(), actualResponse3.get());

        loadBalancer.exclude(provider1);

        Future<String> actualResponse4 = loadBalancer.send();
        Future<String> actualResponse5 = loadBalancer.send();
        Future<String> actualResponse6 = loadBalancer.send();

        assertEquals(provider2.getId(), actualResponse4.get());
        assertEquals(provider3.getId(), actualResponse5.get());
        assertEquals(provider2.getId(), actualResponse6.get());
    }

    @Test
    public void loadBalancerIncludesNode() throws LoadBalancerException, ExecutionException, InterruptedException {

        Provider provider1 = new DefaultProvider();
        Provider provider2 = new DefaultProvider();
        Provider provider3 = new DefaultProvider();

        loadBalancer.registerProviders(List.of(provider1, provider2));

        Future<String> actualResponse1 = loadBalancer.send();
        Future<String> actualResponse2 = loadBalancer.send();
        Future<String> actualResponse3 = loadBalancer.send();

        assertEquals(provider1.getId(), actualResponse1.get());
        assertEquals(provider2.getId(), actualResponse2.get());
        assertEquals(provider1.getId(), actualResponse3.get());

        loadBalancer.include(provider3);

        Future<String> actualResponse4 = loadBalancer.send();
        Future<String> actualResponse5 = loadBalancer.send();
        Future<String> actualResponse6 = loadBalancer.send();

        assertEquals(provider2.getId(), actualResponse4.get());
        assertEquals(provider3.getId(), actualResponse5.get());
        assertEquals(provider1.getId(), actualResponse6.get());

    }

    @Test
    public void loadBalancerIncludesNotAliveNode() throws LoadBalancerException, ExecutionException, InterruptedException {

        Provider provider1 = new DefaultProvider();
        Provider provider2 = new DefaultProvider();
        Provider provider3 = new DefaultProvider();

        loadBalancer.registerProviders(List.of(provider1, provider2));

        Future<String> actualResponse1 = loadBalancer.send();
        Future<String> actualResponse2 = loadBalancer.send();
        Future<String> actualResponse3 = loadBalancer.send();

        assertEquals(provider1.getId(), actualResponse1.get());
        assertEquals(provider2.getId(), actualResponse2.get());
        assertEquals(provider1.getId(), actualResponse3.get());

        provider3.setIsAlive(false);
        loadBalancer.include(provider3);

        Future<String> actualResponse4 = loadBalancer.send();
        Future<String> actualResponse5 = loadBalancer.send();
        Future<String> actualResponse6 = loadBalancer.send();

        assertEquals(provider2.getId(), actualResponse4.get());
        assertEquals(provider1.getId(), actualResponse5.get());
        assertEquals(provider2.getId(), actualResponse6.get());

    }
}
