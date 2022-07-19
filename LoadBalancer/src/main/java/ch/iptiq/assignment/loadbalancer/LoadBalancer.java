package ch.iptiq.assignment.loadbalancer;

import ch.iptiq.assignment.provider.Provider;

import java.util.List;

public interface LoadBalancer {
    String get() throws LoadBalancerException;

    void registerProviders(List<Provider> providers) throws LoadBalancerException;

    void exclude(Provider provider);

    void include(Provider provider) throws LoadBalancerException;

    void check();
}
