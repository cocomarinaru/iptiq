package ch.iptiq.assignment.loadbalancer;

import ch.iptiq.assignment.provider.Provider;

public class RegisteredProvider {
    private final Provider provider;
    private final ProviderStatus status;

    public RegisteredProvider(Provider provider) {
        this.provider = provider;
        this.status = new ProviderStatus(false,0);
    }

    public Provider getProvider() {
        return provider;
    }

    public ProviderStatus getStatus() {
        return status;
    }
}
