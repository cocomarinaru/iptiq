package ch.iptiq.assignment.loadbalancer;

import ch.iptiq.assignment.heartbeat.DefaultHeartBeatChecker;
import ch.iptiq.assignment.heartbeat.HeartBeatChecker;
import ch.iptiq.assignment.loadbalancer.strategy.LoadBalanceStrategy;
import ch.iptiq.assignment.provider.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultLoadBalancer implements LoadBalancer {
    private final int PROVIDERS_LIMIT = 10;
    private final List<RegisteredProvider> providers = new ArrayList<>();

    private final Map<Provider, ProviderStatus>  registeredProviders = new ConcurrentHashMap<>();
    private LoadBalanceStrategy algorithm;
    private final HeartBeatChecker heartBeatChecker = new DefaultHeartBeatChecker();

    public DefaultLoadBalancer(LoadBalanceStrategy algorithm) {
        Objects.requireNonNull(algorithm);
        this.algorithm = algorithm;
    }

    @Override
    public String get() throws LoadBalancerException {
        if (providers.isEmpty()) {
            throw new LoadBalancerException("Missing registered provides");
        }
        RegisteredProvider provider = algorithm.pickFrom(getAliveProviders());
        return provider.getProvider().get();
    }

    public void setAlgorithm(LoadBalanceStrategy algorithm) {
        this.algorithm = algorithm;
    }

    public void registerProviders(List<Provider> providers) throws LoadBalancerException {
        Objects.requireNonNull(providers);
        if (providers.size() > PROVIDERS_LIMIT) {
            throw new LoadBalancerException("To many providers, limit is " + PROVIDERS_LIMIT);
        }

        List<RegisteredProvider> registeredProviders = providers.stream()
                .map(RegisteredProvider::new)
                .collect(Collectors.toList());

        this.providers.clear();
        this.providers.addAll(registeredProviders);
    }

    @Override
    public void exclude(Provider excludedProvider) {
        changeProviderStatus(excludedProvider, true);
    }

    @Override
    public void include(Provider includeProvider) throws LoadBalancerException {
        if (providers.size() == PROVIDERS_LIMIT) {
            throw new LoadBalancerException("To many providers, limit is " + PROVIDERS_LIMIT);
        }
        changeProviderStatus(includeProvider, false);
    }

    public void check() {
        for (RegisteredProvider provider : providers) {
            boolean check = heartBeatChecker.check(provider.getProvider());
            updateStatus(provider.getStatus(), check);
            System.out.printf("Provider %s is alive: %b\n", provider.getProvider().getId(), check);
        }
    }

    private void changeProviderStatus(Provider excludedProvider, boolean excluded) {
        providers.stream()
                .filter(provider -> provider.getProvider().getId().equals(excludedProvider.getId()))
                .forEach(provider -> {
                    provider.getStatus().setExcluded(excluded);
                    provider.getStatus().setSuccessfulConsecutiveHeartBeats(0);
                });
    }

    private List<RegisteredProvider> getAliveProviders() {
        return this.providers.stream()
                .filter(provider -> !provider.getStatus().isExcluded())
                .collect(Collectors.toList());
    }

    private void updateStatus(ProviderStatus status, boolean check) {
        if (!check) {
            status.setExcluded(true);
            status.setSuccessfulConsecutiveHeartBeats(0);
            return;
        }
        if (status.isExcluded()) {
            if (status.getSuccessfulConsecutiveHeartBeats() == 1) {
                status.setExcluded(false);
                status.setSuccessfulConsecutiveHeartBeats(0);
            } else {
                status.setSuccessfulConsecutiveHeartBeats(1);
            }
        }
    }

    //for testing
    public List<RegisteredProvider> getProviders() {
        return providers;
    }
}
