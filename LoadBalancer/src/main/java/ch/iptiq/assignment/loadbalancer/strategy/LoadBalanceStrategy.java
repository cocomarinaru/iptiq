package ch.iptiq.assignment.loadbalancer.strategy;

import ch.iptiq.assignment.loadbalancer.RegisteredProvider;

import java.util.List;

public interface LoadBalanceStrategy {
    RegisteredProvider pickFrom(List<RegisteredProvider> providers);
}
