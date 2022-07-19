package ch.iptiq.assignment.loadbalancer;

import ch.iptiq.assignment.loadbalancer.exception.LoadBalancerException;
import ch.iptiq.assignment.provider.Provider;

import java.util.List;
import java.util.concurrent.Future;

public interface LoadBalancer {
    Future<String> send() throws LoadBalancerException;

    void registerProviders(List<Provider> providers) throws LoadBalancerException;

    void exclude(Provider provider);

    void include(Provider provider) throws LoadBalancerException;

    void check();

    void start();

    void stop();

    void setProviderCapacity(int providerCapacity);
}
