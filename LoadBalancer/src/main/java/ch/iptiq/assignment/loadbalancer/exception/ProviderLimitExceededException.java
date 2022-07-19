package ch.iptiq.assignment.loadbalancer.exception;

public class ProviderLimitExceededException extends LoadBalancerException {
    public ProviderLimitExceededException(int limit) {
        super("To many providers, limit is " + limit);
    }
}
