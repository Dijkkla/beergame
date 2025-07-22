package nl.klaassen.lodewijk.beergame.gamedata.gameplay;

import lombok.Data;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ScoreSheet {
    private final List<Entry> entries = new ArrayList<>();

    @Data
    private class Entry {
        private final int initialStock;
        private final Map<DistributorId, ConsumerEntry> consumerData;
        private final Map<DistributorId, ProviderEntry> providerData;
        private int newStock;

        @Data
        private class ConsumerEntry {
            private final int initialOpenOrders;
            private final int incomingOrder;
            private int outgoingDelivery;
            private int newOpenOrders;
        }

        @Data
        private class ProviderEntry {
            private final int incomingDelivery;
            private int outgoingOrder;
        }
    }
}
