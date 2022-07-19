package ch.iptiq.assignment.it;

import ch.iptiq.assignment.loadbalancer.DefaultLoadBalancer;
import ch.iptiq.assignment.loadbalancer.LoadBalancer;
import ch.iptiq.assignment.loadbalancer.LoadBalancerException;
import ch.iptiq.assignment.loadbalancer.strategy.RandomLoadBalancerStrategy;
import ch.iptiq.assignment.loadbalancer.strategy.RoundRobinBalancerStrategy;
import ch.iptiq.assignment.provider.DefaultProvider;
import ch.iptiq.assignment.provider.Provider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoadBalancerIntegrationTests {

    @Test
    public void loadBalancerCallsRandomProvider() throws LoadBalancerException {
        Provider provider1 = new DefaultProvider();
        Provider provider2 = new DefaultProvider();
        Provider provider3 = new DefaultProvider();
        LoadBalancer loadBalancer = new DefaultLoadBalancer(new RandomLoadBalancerStrategy());
        loadBalancer.registerProviders(List.of(provider1, provider2, provider3));

        String actualResponse1 = loadBalancer.get();
        String actualResponse2 = loadBalancer.get();

        List<String> possibleResponses = List.of(provider1.get(), provider2.get(), provider3.get());
        assertTrue(possibleResponses.contains(actualResponse1));
        assertTrue(possibleResponses.contains(actualResponse2));
    }

    @Test
    public void roundRobinLoadBalancerCallsProvidersSequentially() throws LoadBalancerException {
        Provider provider1 = new DefaultProvider();
        Provider provider2 = new DefaultProvider();
        Provider provider3 = new DefaultProvider();

        LoadBalancer loadBalancer = new DefaultLoadBalancer(new RoundRobinBalancerStrategy());
        loadBalancer.registerProviders(List.of(provider1, provider2, provider3));

        String actualResponse1 = loadBalancer.get();
        String actualResponse2 = loadBalancer.get();
        String actualResponse3 = loadBalancer.get();
        String actualResponse4 = loadBalancer.get();

        assertEquals(provider1.get(), actualResponse1);
        assertEquals(provider2.get(), actualResponse2);
        assertEquals(provider3.get(), actualResponse3);
        assertEquals(provider1.get(), actualResponse4);
    }

    @Test
    public void loadBalancerExcludesNode() throws LoadBalancerException {

        Provider provider1 = new DefaultProvider();
        Provider provider2 = new DefaultProvider();
        Provider provider3 = new DefaultProvider();

        LoadBalancer loadBalancer = new DefaultLoadBalancer(new RoundRobinBalancerStrategy());
        loadBalancer.registerProviders(List.of(provider1, provider2, provider3));

        String actualResponse1 = loadBalancer.get();
        String actualResponse2 = loadBalancer.get();
        String actualResponse3 = loadBalancer.get();

        assertEquals(provider1.getId(), actualResponse1);
        assertEquals(provider2.getId(), actualResponse2);
        assertEquals(provider3.getId(), actualResponse3);

        loadBalancer.exclude(provider1);

        String actualResponse4 = loadBalancer.get();
        String actualResponse5 = loadBalancer.get();
        String actualResponse6 = loadBalancer.get();

        assertEquals(provider2.getId(), actualResponse4);
        assertEquals(provider3.getId(), actualResponse5);
        assertEquals(provider2.getId(), actualResponse6);
    }

    @Test
    public void loadBalancerIncludesNode() throws LoadBalancerException {

        Provider provider1 = new DefaultProvider();
        Provider provider2 = new DefaultProvider();
        Provider provider3 = new DefaultProvider();

        LoadBalancer loadBalancer = new DefaultLoadBalancer(new RoundRobinBalancerStrategy());
        loadBalancer.registerProviders(List.of(provider1, provider2));

        String actualResponse1 = loadBalancer.get();
        String actualResponse2 = loadBalancer.get();
        String actualResponse3 = loadBalancer.get();

        assertEquals(provider1.getId(), actualResponse1);
        assertEquals(provider2.getId(), actualResponse2);
        assertEquals(provider1.getId(), actualResponse3);

        loadBalancer.include(provider3);

        String actualResponse4 = loadBalancer.get();
        String actualResponse5 = loadBalancer.get();
        String actualResponse6 = loadBalancer.get();

        assertEquals(provider2.getId(), actualResponse4);
        assertEquals(provider3.getId(), actualResponse5);
        assertEquals(provider1.getId(), actualResponse6);

    }

    @Test
    public void loadBalancerIncludesNotAliveNode() throws LoadBalancerException {

        Provider provider1 = new DefaultProvider();
        Provider provider2 = new DefaultProvider();
        Provider provider3 = new DefaultProvider();

        LoadBalancer loadBalancer = new DefaultLoadBalancer(new RoundRobinBalancerStrategy());
        loadBalancer.registerProviders(List.of(provider1, provider2));

        String actualResponse1 = loadBalancer.get();
        String actualResponse2 = loadBalancer.get();
        String actualResponse3 = loadBalancer.get();

        assertEquals(provider1.getId(), actualResponse1);
        assertEquals(provider2.getId(), actualResponse2);
        assertEquals(provider1.getId(), actualResponse3);

        provider3.setIsAlive(false);
        loadBalancer.include(provider3);

        String actualResponse4 = loadBalancer.get();
        String actualResponse5 = loadBalancer.get();
        String actualResponse6 = loadBalancer.get();

        assertEquals(provider2.getId(), actualResponse4);
        assertEquals(provider1.getId(), actualResponse5);
        assertEquals(provider2.getId(), actualResponse6);

    }

}
