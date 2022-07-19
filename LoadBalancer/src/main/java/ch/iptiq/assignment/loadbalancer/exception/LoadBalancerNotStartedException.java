package ch.iptiq.assignment.loadbalancer.exception;

public class LoadBalancerNotStartedException extends LoadBalancerException {
    public LoadBalancerNotStartedException() {
        super("Load Balancer not started");
    }
}
