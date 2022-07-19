package ch.iptiq.assignment;

import ch.iptiq.assignment.loadbalancer.DefaultLoadBalancer;
import ch.iptiq.assignment.loadbalancer.LoadBalancer;
import ch.iptiq.assignment.loadbalancer.LoadBalancerException;
import ch.iptiq.assignment.loadbalancer.strategy.RoundRobinBalancerStrategy;
import ch.iptiq.assignment.provider.DefaultProvider;
import ch.iptiq.assignment.provider.Provider;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client {

    public static void main(String[] args) throws LoadBalancerException {

        //step1
        Provider provider1 = new DefaultProvider();
        Provider provider2 = new DefaultProvider();
        Provider provider3 = new DefaultProvider();
        provider2.setIsAlive(false);

        List<Provider> providers = List.of(provider1, provider2, provider3);

        System.out.println("Providers");
        System.out.println(providers);

        LoadBalancer loadBalancer = new DefaultLoadBalancer(new RoundRobinBalancerStrategy());

        loadBalancer.registerProviders(providers);

        ScheduledExecutorService heartBeatScheduler = Executors.newScheduledThreadPool(1);
        heartBeatScheduler.scheduleAtFixedRate(loadBalancer::check, 0, 2, TimeUnit.SECONDS);

        heartBeatScheduler.schedule(()-> provider2.setIsAlive(true),3, TimeUnit.SECONDS);

        ScheduledExecutorService shutdownScheduler = Executors.newScheduledThreadPool(1);
        shutdownScheduler.schedule(heartBeatScheduler::shutdownNow, 10 , TimeUnit.SECONDS);
        shutdownScheduler.shutdown();

    }
}
