package ch.iptiq.assignment.it;

import ch.iptiq.assignment.loadbalancer.DefaultLoadBalancer;
import ch.iptiq.assignment.loadbalancer.LoadBalancer;
import ch.iptiq.assignment.loadbalancer.LoadBalancerException;
import ch.iptiq.assignment.loadbalancer.strategy.RoundRobinBalancerStrategy;
import ch.iptiq.assignment.provider.DefaultProvider;
import ch.iptiq.assignment.provider.Provider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeartBeatIntegrationTest {

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

}
