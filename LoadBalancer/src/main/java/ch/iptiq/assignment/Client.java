package ch.iptiq.assignment;

import ch.iptiq.assignment.loadbalancer.DefaultLoadBalancer;
import ch.iptiq.assignment.loadbalancer.LoadBalancer;
import ch.iptiq.assignment.loadbalancer.LoadBalancerException;
import ch.iptiq.assignment.loadbalancer.strategy.RandomLoadBalancerStrategy;
import ch.iptiq.assignment.loadbalancer.strategy.RoundRobinBalancerStrategy;
import ch.iptiq.assignment.provider.DefaultProvider;
import ch.iptiq.assignment.provider.Provider;

import java.util.List;

public class Client {

    public static void main(String[] args) throws LoadBalancerException {

        //step1
        Provider  provider1 = new DefaultProvider();
        Provider  provider2 = new DefaultProvider();
        Provider  provider3 = new DefaultProvider();

        List<Provider> providers = List.of(provider1, provider2, provider3);

        System.out.println("Providers");
        System.out.println(providers);

        LoadBalancer loadBalancer = new DefaultLoadBalancer(new RoundRobinBalancerStrategy());

        loadBalancer.registerProviders(providers);

        System.out.println(loadBalancer.get());
        System.out.println(loadBalancer.get());
        System.out.println(loadBalancer.get());
        System.out.println(loadBalancer.get());
        System.out.println(loadBalancer.get());
        System.out.println(loadBalancer.get());
        System.out.println(loadBalancer.get());
        System.out.println(loadBalancer.get());
    }
}
