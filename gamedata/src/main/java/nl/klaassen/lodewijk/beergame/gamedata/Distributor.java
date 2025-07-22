package nl.klaassen.lodewijk.beergame.gamedata;

import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;

import java.util.Collection;
import java.util.Set;

public record Distributor(DistributorId self, Set<DistributorId> consumers, Set<DistributorId> providers) {

    public Distributor {
        if (consumers.stream().anyMatch(providers::contains)) {
            throw new IllegalArgumentException("Distributors may not be registered as both a provider and a consumer");
        }
    }

    public Distributor(DistributorId self, Collection<DistributorId> consumers, Collection<DistributorId> providers) {
        this(self, Set.copyOf(consumers), Set.copyOf(providers));
    }

    public void placeOrder(int amount) {
        if (providers.size() != 1) {
            throw new IllegalStateException(self + " has more than one provider");
        }
        placeOrder(providers.iterator().next(), amount);
    }

    public void placeOrder(DistributorId provider, int amount) {
        if (!providers.contains(provider)) {
            throw new IllegalArgumentException(provider + " is not a provider of " + self);
        }
    }

    public void sendGoods() {
        if (consumers.size() != 1) {
            throw new IllegalStateException(self + " has more than one consumer");
        }
    }

    public void sendGoods(DistributorId consumer, int amount) {
        if (!consumers.contains(consumer)) {
            throw new IllegalArgumentException(consumer + " is not a consumer of " + self);
        }
    }
}
