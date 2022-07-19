package ch.iptiq.assignment.loadbalancer.strategy;

import ch.iptiq.assignment.loadbalancer.RegisteredProvider;

import java.util.List;

public class RoundRobinBalancerStrategy implements LoadBalanceStrategy {
    private int previousIndex;

    public RoundRobinBalancerStrategy() {
        this.previousIndex = -1;
    }

    @Override
    public RegisteredProvider pickFrom(List<RegisteredProvider> providers) {
        int index = providers.size() <= previousIndex + 1 ? 0 : previousIndex + 1;
        RegisteredProvider provider = providers.get(index);
        previousIndex = index + 1 == providers.size() ? -1 : index;
        return provider;
    }
}
