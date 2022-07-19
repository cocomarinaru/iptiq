package ch.iptiq.assignment.loadbalancer.exception;

public class TooManyRequestsException extends LoadBalancerException {

    public TooManyRequestsException() {
        super("Load Balancer cannot accept requests: Too many incoming requests");
    }
}
