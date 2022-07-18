package ch.iptiq.assignment.loadbalancer;

import ch.iptiq.assignment.loadbalancer.strategy.LoadBalanceStrategy;
import ch.iptiq.assignment.provider.Provider;

import javax.print.attribute.standard.MediaSize;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class DefaultLoadBalancer implements LoadBalancer {

    Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final int PROVIDERS_LIMIT = 10;

    private List<Provider> providers;
    private LoadBalanceStrategy algorithm;

    public DefaultLoadBalancer(LoadBalanceStrategy algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String get() throws LoadBalancerException {
        if (providers == null || providers.isEmpty()) {
            throw new LoadBalancerException("Missing registered provides");
        }
        Provider provider = algorithm.pickFrom(providers);
        return provider.get();
    }

    public void setAlgorithm(LoadBalanceStrategy algorithm) {
        this.algorithm = algorithm;
    }

    public void registerProviders(List<Provider> providers) throws LoadBalancerException {
        logger.info("Registering providers");
        Objects.requireNonNull(providers);
        if (providers.size() > PROVIDERS_LIMIT) {
            logger.severe("To many providers, limit is: " + PROVIDERS_LIMIT);
            throw new LoadBalancerException("To many providers, limit is " + PROVIDERS_LIMIT);
        }
        this.providers = providers;
    }


}
