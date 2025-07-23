package nl.klaassen.lodewijk.beergame.gamedata;

import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;

import java.util.Collection;
import java.util.Set;

public record Distributor(DistributorId self, Set<DistributorId> consumers, Set<DistributorId> suppliers) {

    public Distributor {
        if (consumers.stream().anyMatch(suppliers::contains)) {
            throw new IllegalArgumentException("Distributors may not be registered as both a supplier and a consumer");
        }
    }

    public Distributor(DistributorId self, Collection<DistributorId> consumers, Collection<DistributorId> suppliers) {
        this(self, Set.copyOf(consumers), Set.copyOf(suppliers));
    }

    public void placeOrder(int amount) {
        if (suppliers.size() != 1) {
            throw new IllegalStateException(self + " has more than one supplier");
        }
        placeOrder(suppliers.iterator().next(), amount);
    }

    public void placeOrder(DistributorId supplier, int amount) {
        if (!suppliers.contains(supplier)) {
            throw new IllegalArgumentException(supplier + " is not a supplier of " + self);
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
