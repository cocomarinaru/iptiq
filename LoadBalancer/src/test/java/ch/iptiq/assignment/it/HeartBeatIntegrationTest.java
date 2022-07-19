package ch.iptiq.assignment.it;

import ch.iptiq.assignment.loadbalancer.DefaultLoadBalancer;
import ch.iptiq.assignment.loadbalancer.LoadBalancer;
import ch.iptiq.assignment.loadbalancer.LoadBalancerException;
import ch.iptiq.assignment.loadbalancer.strategy.RoundRobinBalancerStrategy;
import ch.iptiq.assignment.provider.DefaultProvider;
import ch.iptiq.assignment.provider.Provider;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeartBeatIntegrationTest {

    @Test
    public void loadBalancerExcludeNodeThatIsNotAlive() throws LoadBalancerException, InterruptedException {

        Provider provider1 = new DefaultProvider();
        Provider provider2 = new DefaultProvider();

        LoadBalancer loadBalancer = new DefaultLoadBalancer(new RoundRobinBalancerStrategy());
        loadBalancer.registerProviders(List.of(provider1, provider2));

        String actualResponse1 = loadBalancer.get();
        String actualResponse2 = loadBalancer.get();

        assertEquals(provider1.getId(), actualResponse1);
        assertEquals(provider2.getId(), actualResponse2);

        loadBalancer.start();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(()-> provider1.setIsAlive(false),4, TimeUnit.SECONDS);

        executor.awaitTermination(10, TimeUnit.SECONDS);

        String actualResponse3 = loadBalancer.get();
        String actualResponse4 = loadBalancer.get();

        assertEquals(provider2.getId(), actualResponse3);
        assertEquals(provider2.getId(), actualResponse4);

    }

    @Test
    public void loadBalancerIncludesNodeThatCameAlive() throws LoadBalancerException, InterruptedException {

        Provider provider1 = new DefaultProvider(false);
        Provider provider2 = new DefaultProvider();

        LoadBalancer loadBalancer = new DefaultLoadBalancer(new RoundRobinBalancerStrategy());
        loadBalancer.registerProviders(List.of(provider1, provider2));

        String actualResponse1 = loadBalancer.get();
        String actualResponse2 = loadBalancer.get();

        assertEquals(provider2.getId(), actualResponse1);
        assertEquals(provider2.getId(), actualResponse2);

        loadBalancer.start();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(()-> provider1.setIsAlive(true),4, TimeUnit.SECONDS);

        executor.awaitTermination(10, TimeUnit.SECONDS);

        String actualResponse3 = loadBalancer.get();
        String actualResponse4 = loadBalancer.get();

        assertEquals(provider1.getId(), actualResponse3);
        assertEquals(provider2.getId(), actualResponse4);

    }

}
