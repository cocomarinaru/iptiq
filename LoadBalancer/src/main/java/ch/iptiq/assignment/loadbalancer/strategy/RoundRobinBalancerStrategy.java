package ch.iptiq.assignment.loadbalancer.strategy;

import ch.iptiq.assignment.provider.Provider;

import java.util.List;

public class RoundRobinBalancerStrategy implements LoadBalanceStrategy {

    private int lastProviderIndex;

    public RoundRobinBalancerStrategy() {
        this.lastProviderIndex = -1;
    }

    @Override
    public Provider pickFrom(List<Provider> providers) {
        int index = providers.size() == lastProviderIndex + 1 ? 0 : lastProviderIndex + 1;
        Provider provider = providers.get(index);
        lastProviderIndex = index;
        return provider;
    }
}
