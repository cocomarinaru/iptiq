package ch.iptiq.assignment.loadbalancer.strategy;

import ch.iptiq.assignment.loadbalancer.RegisteredProvider;

import java.util.List;

public class RoundRobinBalancerStrategy implements LoadBalanceStrategy {
    private int lastProviderIndex;

    public RoundRobinBalancerStrategy() {
        this.lastProviderIndex = -1;
    }

    @Override
    public RegisteredProvider pickFrom(List<RegisteredProvider> providers) {
        int index = providers.size() <= lastProviderIndex + 1 ? 0 : lastProviderIndex + 1;
        RegisteredProvider provider = providers.get(index);
        lastProviderIndex = index;
        return provider;
    }
}
