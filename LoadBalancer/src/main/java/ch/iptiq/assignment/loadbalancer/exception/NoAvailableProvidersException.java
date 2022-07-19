package ch.iptiq.assignment.loadbalancer.exception;

public class NoAvailableProvidersException extends LoadBalancerException {

    public NoAvailableProvidersException() {
        super("NO Available Providers");
    }
}
