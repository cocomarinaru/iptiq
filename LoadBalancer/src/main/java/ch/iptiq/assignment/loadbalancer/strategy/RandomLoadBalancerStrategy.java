package ch.iptiq.assignment.loadbalancer.strategy;

import ch.iptiq.assignment.provider.Provider;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancerStrategy implements LoadBalanceStrategy {

    @Override
    public Provider pickFrom(List<Provider> providers) {
        int randomIndex = new Random().nextInt(0, providers.size());
        return providers.get(randomIndex);
    }
}
