package ch.iptiq.assignment.loadbalancer.strategy;

import ch.iptiq.assignment.provider.Provider;

import java.util.List;

public interface LoadBalanceStrategy {

    Provider pickFrom(List<Provider> providers);
}
