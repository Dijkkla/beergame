package nl.klaassen.lodewijk.beergame.gamedata;

import lombok.Data;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;

import java.util.Set;

@Data
public class Distributor {
    private final DistributorId id;
    private final Set<DistributorId> consumers;
    private final Set<DistributorId> providers;

    public void placeOrder(int amount) {
        if (providers.size() != 1) {
            throw new IllegalStateException(id + " has more than one provider");
        }
        placeOrder(providers.iterator().next(), amount);
    }

    public void placeOrder(DistributorId provider, int amount) {
        if (!providers.contains(provider)) {
            throw new IllegalArgumentException(provider + " is not a provider of " + id);
        }
    }

    public void sendGoods() {
        if (consumers.size() != 1) {
            throw new IllegalStateException(id + " has more than one consumer");
        }
    }

    public void sendGoods(DistributorId consumer, int amount) {
        if (!consumers.contains(consumer)) {
            throw new IllegalArgumentException(consumer + " is not a consumer of " + id);
        }
    }
}
