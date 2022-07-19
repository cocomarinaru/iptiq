package ch.iptiq.assignment.loadbalancer;

import ch.iptiq.assignment.heartbeat.DefaultHeartBeatChecker;
import ch.iptiq.assignment.heartbeat.HeartBeatChecker;
import ch.iptiq.assignment.loadbalancer.exception.*;
import ch.iptiq.assignment.loadbalancer.strategy.LoadBalanceStrategy;
import ch.iptiq.assignment.provider.Provider;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DefaultLoadBalancer implements LoadBalancer {
    private final int PROVIDERS_LIMIT = 10;
    private final int DEFAULT_PROVIDER_CAPACITY = 3;
    private int providerCapacity = DEFAULT_PROVIDER_CAPACITY;
    private final AtomicInteger parallelRequestsCount = new AtomicInteger(0);
    private boolean running = false;
    private final Map<String, RegisteredProvider> registeredProviders = Collections.synchronizedMap(new LinkedHashMap<>());
    private LoadBalanceStrategy algorithm;
    private final HeartBeatChecker heartBeatChecker = new DefaultHeartBeatChecker();

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);

    public DefaultLoadBalancer(LoadBalanceStrategy algorithm) {
        Objects.requireNonNull(algorithm);
        this.algorithm = algorithm;
    }

    @Override
    public Future<String> send() throws LoadBalancerException {
        if (!running) {
            throw new LoadBalancerNotStartedException();
        }
        List<RegisteredProvider> aliveProviders = getAliveProviders();
        if (aliveProviders.isEmpty()) {
            throw new NoAvailableProvidersException();
        }
        RegisteredProvider provider = algorithm.pickFrom(aliveProviders);

        if (parallelRequestsCount.get() >= aliveProviders.size() * providerCapacity) {
            throw new TooManyRequestsException();
        }

        Callable<String> request = () -> {
            String response = provider.getProvider().get();
            parallelRequestsCount.decrementAndGet();
            return response;
        };
        parallelRequestsCount.incrementAndGet();
        return executor.schedule(request, 2, TimeUnit.SECONDS);

    }

    public void setAlgorithm(LoadBalanceStrategy algorithm) {
        this.algorithm = algorithm;
    }

    public void registerProviders(List<Provider> providers) throws LoadBalancerException {
        Objects.requireNonNull(providers);
        if (providers.size() > PROVIDERS_LIMIT) {
            throw new ProviderLimitExceededException(PROVIDERS_LIMIT);
        }

        this.registeredProviders.clear();
        providers.forEach(provider -> this.registeredProviders.put(provider.getId(), new RegisteredProvider(provider)));
    }

    public void start() {
        if (!running) {
            parallelRequestsCount.set(0);
            running = true;
            executor.scheduleAtFixedRate(this::check, 0, 2, TimeUnit.SECONDS);
        }
    }

    public void stop() {
        running = false;
        parallelRequestsCount.set(0);
        executor.shutdownNow();
    }

    @Override
    public void exclude(Provider excludedProvider) {
        changeProviderStatus(excludedProvider.getId(), true);
    }

    @Override
    public void include(Provider includeProvider) throws LoadBalancerException {
        if (registeredProviders.size() == PROVIDERS_LIMIT) {
            throw new ProviderLimitExceededException(PROVIDERS_LIMIT);
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
        }
    }

    public void setProviderCapacity(int providerCapacity) {
        this.providerCapacity = providerCapacity;
    }

    private void changeProviderStatus(String providerId, boolean excluded) {
        RegisteredProvider provider = registeredProviders.get(providerId);
        if (provider != null) {
            provider.getProvider().setIsAlive(!excluded);
            provider.getStatus().setExcluded(excluded);
            provider.getStatus().setSuccessfulConsecutiveHeartBeats(0);
        }
    }

    private List<RegisteredProvider> getAliveProviders() {
        return this.registeredProviders.values().stream().filter(provider -> !provider.getStatus().isExcluded()).collect(Collectors.toList());
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
