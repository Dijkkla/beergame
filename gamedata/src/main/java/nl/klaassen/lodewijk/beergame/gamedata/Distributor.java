package nl.klaassen.lodewijk.beergame.gamedata;

import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;

import java.util.Objects;
import java.util.Set;

public record Distributor(DistributorId self, Set<DistributorId> consumers,
                          Set<DistributorId> suppliers) implements Comparable<Distributor> {
    public Distributor {
        Objects.requireNonNull(self, "self must not be null");
        Objects.requireNonNull(suppliers, "suppliers must not be null");
        Objects.requireNonNull(consumers, "consumers must not be null");
        if (consumers.isEmpty()) throw new IllegalArgumentException("consumers must not be empty");
        if (suppliers.isEmpty()) throw new IllegalArgumentException("suppliers must not be empty");
        if (consumers.stream().anyMatch(suppliers::contains)) {
            throw new IllegalArgumentException("Distributors may not be registered as both a supplier and a consumer");
        }
    }

    @Override
    public String toString() {
        return self + " (consumers=" + consumers.stream().sorted().toList() + ", suppliers=" + suppliers.stream().sorted().toList() + ")";
    }

    @Override
    public int compareTo(Distributor o) {
        return self.compareTo(o.self);
    }
}
