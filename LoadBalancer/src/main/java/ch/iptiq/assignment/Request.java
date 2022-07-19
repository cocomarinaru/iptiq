package ch.iptiq.assignment;

import ch.iptiq.assignment.loadbalancer.LoadBalancer;

import java.util.concurrent.Callable;

public class Request implements Callable<String> {
    private LoadBalancer loadBalancer;

    @Override
    public String call() throws Exception {
        return loadBalancer.get();
    }
}
