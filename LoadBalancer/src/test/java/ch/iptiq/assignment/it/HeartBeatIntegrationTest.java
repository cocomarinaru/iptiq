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
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeartBeatIntegrationTest {

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
    public void loadBalancerExcludeNodeThatIsNotAlive() throws LoadBalancerException, InterruptedException, ExecutionException {

        Provider provider1 = new DefaultProvider();
        Provider provider2 = new DefaultProvider();

        loadBalancer.registerProviders(List.of(provider1, provider2));

        String actualResponse1 = loadBalancer.send().get();
        String actualResponse2 = loadBalancer.send().get();

        assertEquals(provider1.getId(), actualResponse1);
        assertEquals(provider2.getId(), actualResponse2);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.submit(() -> provider1.setIsAlive(false));

        String actualResponse3 = sendGetRequestWithDelay(loadBalancer);
        String actualResponse4 = sendGetRequestWithDelay(loadBalancer);

        assertEquals(provider2.getId(), actualResponse3);
        assertEquals(provider2.getId(), actualResponse4);
    }

    @Test
    public void loadBalancerIncludesNodeThatCameAlive() throws LoadBalancerException, InterruptedException, ExecutionException {

        Provider provider1 = new DefaultProvider(false);
        Provider provider2 = new DefaultProvider();

        loadBalancer.registerProviders(List.of(provider1, provider2));

        String actualResponse1 = loadBalancer.send().get();
        String actualResponse2 = loadBalancer.send().get();

        assertEquals(provider2.getId(), actualResponse1);
        assertEquals(provider2.getId(), actualResponse2);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.submit(() -> provider1.setIsAlive(true));

        String actualResponse3 = sendGetRequestWithDelay(loadBalancer);
        String actualResponse4 = sendGetRequestWithDelay(loadBalancer);

        assertEquals(provider1.getId(), actualResponse3);
        assertEquals(provider2.getId(), actualResponse4);

    }

    private String sendGetRequestWithDelay(LoadBalancer loadBalancer) throws ExecutionException, InterruptedException {
        return Executors.newSingleThreadScheduledExecutor()
                .schedule(() -> loadBalancer.send().get(), 4, TimeUnit.SECONDS).get();
    }
}
