package ch.iptiq.assignment.loadbalancer;

import ch.iptiq.assignment.heartbeat.DefaultHeartBeatChecker;
import ch.iptiq.assignment.heartbeat.HeartBeatChecker;
import ch.iptiq.assignment.loadbalancer.strategy.LoadBalanceStrategy;
import ch.iptiq.assignment.provider.Provider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DefaultLoadBalancer implements LoadBalancer {
    private final int PROVIDERS_LIMIT = 10;
    private final Map<String, RegisteredProvider> registeredProviders = Collections.synchronizedMap(new LinkedHashMap<>());
    private LoadBalanceStrategy algorithm;
    private final HeartBeatChecker heartBeatChecker = new DefaultHeartBeatChecker();

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public DefaultLoadBalancer(LoadBalanceStrategy algorithm) {
        Objects.requireNonNull(algorithm);
        this.algorithm = algorithm;
    }

    @Override
    public String get() throws LoadBalancerException {
        if (registeredProviders.isEmpty()) {
            throw new LoadBalancerException("Missing registered providers");
        }
        List<RegisteredProvider> aliveProviders = getAliveProviders();
        if (aliveProviders.isEmpty()) {
            throw new LoadBalancerException("No alive providers");
        }
        RegisteredProvider provider = algorithm.pickFrom(aliveProviders);
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

        this.registeredProviders.clear();
        providers.forEach(provider -> this.registeredProviders.put(provider.getId(), new RegisteredProvider(provider)));
    }

    public void start() {
        executor.scheduleAtFixedRate(this::check, 0, 2, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdownNow();
    }

    @Override
    public void exclude(Provider excludedProvider) {
        changeProviderStatus(excludedProvider.getId(), true);
    }

    @Override
    public void include(Provider includeProvider) throws LoadBalancerException {
        if (registeredProviders.size() == PROVIDERS_LIMIT) {
            throw new LoadBalancerException("To many providers, limit is " + PROVIDERS_LIMIT);
        }

        RegisteredProvider provider = registeredProviders.get(includeProvider.getId());

        if (provider == null) {
            registeredProviders.put(includeProvider.getId(), new RegisteredProvider(includeProvider));
        } else {
            changeProviderStatus(includeProvider.getId(), !includeProvider.isAlive());
        }

    }

    public void check() {
        for (RegisteredProvider provider : registeredProviders.values()) {
            boolean check = heartBeatChecker.check(provider.getProvider());
            updateStatus(provider.getStatus(), check);
            System.out.printf("Provider %s is alive: %b\n", provider.getProvider().getId(), check);
        }
    }

    private void changeProviderStatus(String providerId, boolean excluded) {
        RegisteredProvider provider = registeredProviders.get(providerId);
        if (provider != null) {
            provider.getStatus().setExcluded(excluded);
            provider.getStatus().setSuccessfulConsecutiveHeartBeats(0);
        }
    }

    private List<RegisteredProvider> getAliveProviders() {
        return this.registeredProviders.values().stream()
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

}
