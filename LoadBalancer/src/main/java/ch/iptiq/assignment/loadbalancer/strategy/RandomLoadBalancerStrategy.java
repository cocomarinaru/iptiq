package ch.iptiq.assignment.loadbalancer.strategy;

import ch.iptiq.assignment.loadbalancer.RegisteredProvider;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancerStrategy implements LoadBalanceStrategy {

    @Override
    public RegisteredProvider pickFrom(List<RegisteredProvider> providers) {
        int randomIndex = new Random().nextInt(0, providers.size());
        return providers.get(randomIndex);
    }
}
