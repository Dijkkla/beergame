package nl.klaassen.lodewijk.beergame.gamedata.gameplay;

import lombok.Getter;
import nl.klaassen.lodewijk.beergame.gamedata.Distributor;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;

import java.util.*;
import java.util.stream.Collectors;


public class ScoreSheet {
    private final List<Entry> entries = new ArrayList<>();
    private final DistributorId self;
    private final Set<DistributorId> consumers;
    private final Set<DistributorId> providers;

    public ScoreSheet(Distributor distributor, int initialStock, int initialIncomingOrders, int initialIncomingGoods) {
        this(distributor.self(), distributor.consumers(), distributor.providers(), initialStock, initialIncomingOrders, initialIncomingGoods);
    }

    public ScoreSheet(DistributorId self, Collection<DistributorId> consumers, Collection<DistributorId> providers, int initialStock, int initialIncomingOrders, int initialIncomingGoods) {
        if (consumers.stream().anyMatch(providers::contains)) {
            throw new IllegalArgumentException("Distributors may not be registered as both a provider and a consumer");
        }
        this.self = self;
        this.consumers = Set.copyOf(consumers);
        this.providers = Set.copyOf(providers);
        entries.add(new Entry(initialStock, initialIncomingOrders, initialIncomingGoods));
    }

    public void nextRound(Collection<GameAction> actions) {
        Collection<GameAction> filteredActions = actions.stream().filter(a -> a.round() == entries.size() && a.to().equals(self)).collect(Collectors.toSet());
        if (filteredActions.size() != consumers.size() + providers.size()) {
            throw new IllegalArgumentException("Invalid set of gameActions");
        }
        entries.add(new Entry(filteredActions));
    }

    public int get(int entry, Column column) {
        return entries.get(entry - 1).get(column);
    }

    public int get(int entry, Column column, DistributorId distributorId) {
        int value;
        if (consumers.contains(distributorId)) {
            value = entries.get(entry - 1).getConsumerData().get(distributorId).get(column);
        } else if (providers.contains(distributorId)) {
            value = entries.get(entry - 1).getProviderData().get(distributorId).get(column);
        } else {
            throw new IllegalArgumentException(distributorId + " is neither a consumer nor a provider of " + self);
        }
        if (value == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Cannot get " + column + " from " + (consumers.contains(distributorId) ? "consumer " : "provider ") + distributorId);
        } else {
            return value;
        }
    }

    @Override
    public String toString() {
        return "Sheet of " + self + "\n\tConsumers: " + consumers + "\n\tProviders: " + providers + "\n" + entries;
    }

    public enum Column {
        WEEK_NR, INITIAL_STOCK, INCOMING_GOODS, INCOMING_ORDERS, INITIAL_OPEN_ORDERS, OUTGOING_GOODS, NEW_OPEN_ORDERS, NEW_STOCK, OUTGOING_ORDERS
    }

    private class Entry {
        private final int entryNr;
        private final int initialStock;

        @Getter
        private final Map<DistributorId, ConsumerEntry> consumerData;
        @Getter
        private final Map<DistributorId, ProviderEntry> providerData;

        public Entry(int initialStock, int initialIncomingOrders, int initialIncomingGoods) {
            this.entryNr = 1;
            this.initialStock = initialStock;
            this.consumerData = consumers.stream().collect(Collectors.toMap(c -> c, c -> new ConsumerEntry(0, initialIncomingOrders)));
            this.providerData = providers.stream().collect(Collectors.toMap(p -> p, p -> new ProviderEntry(initialIncomingGoods)));
        }

        public Entry(Collection<GameAction> actions) {
            Entry last = entries.getLast();
            this.entryNr = last.entryNr + 1;
            this.initialStock = last.get(Column.NEW_STOCK);
            this.consumerData = consumers.stream().collect(Collectors.toMap(c -> c, c -> new ConsumerEntry(last.consumerData.get(c).get(Column.NEW_OPEN_ORDERS), actions.stream().filter(a -> a.from().equals(c) && a.type() == GameAction.Type.ORDERS).findAny().get().amount())));
            this.providerData = providers.stream().collect(Collectors.toMap(p -> p, p -> new ProviderEntry(actions.stream().filter(a -> a.from().equals(p) && a.type() == GameAction.Type.GOODS).findAny().get().amount())));
        }

        public int get(Column column) {
            return switch (column) {
                case WEEK_NR -> entryNr;
                case INITIAL_STOCK -> initialStock;
                case INCOMING_GOODS -> providerData.values().stream().mapToInt(p -> p.incomingGoods).sum();
                case INCOMING_ORDERS -> consumerData.values().stream().mapToInt(c -> c.incomingOrders).sum();
                case INITIAL_OPEN_ORDERS -> consumerData.values().stream().mapToInt(c -> c.initialOpenOrders).sum();
                case OUTGOING_GOODS -> consumerData.values().stream().mapToInt(c -> c.outgoingGoods).sum();
                case NEW_OPEN_ORDERS ->
                        consumerData.values().stream().mapToInt(c -> c.get(Column.NEW_OPEN_ORDERS)).sum();
                case NEW_STOCK -> initialStock + get(Column.INCOMING_GOODS) - get(Column.OUTGOING_GOODS);
                case OUTGOING_ORDERS -> providerData.values().stream().mapToInt(p -> p.outgoingOrders).sum();
            };
        }

        @Override
        public String toString() {
            final int LEFT_PAD = 16;
            StringBuilder sb = new StringBuilder();
            String entryNrString = Integer.toString(entryNr);
            sb.append("\n").append(" ".repeat(LEFT_PAD - 9 - entryNrString.length())).append("ENTRY_NR:").append(entryNrString);
            for (Column c : Column.values()) {
                sb.append(" | ").append(c);
            }
            sb.append("\n").append(" ".repeat(LEFT_PAD - 5)).append("TOTAL");
            for (Column c : Column.values()) {
                toStringHelper(c, get(c), sb);
            }
            consumers.stream().sorted().forEach(c -> sb.append("\n").append(" ".repeat(Math.max(0, LEFT_PAD - c.toString().length()))).append(c).append(consumerData.get(c).toString()));
            providers.stream().sorted().forEach(p -> sb.append("\n").append(" ".repeat(Math.max(0, LEFT_PAD - p.toString().length()))).append(p).append(providerData.get(p).toString()));
            sb.append("\n");
            return sb.toString();
        }

        private void toStringHelper(Column c, int v, StringBuilder sb) {
            String vs = v == Integer.MIN_VALUE ? "" : Integer.toString(v);
            sb.append(" | ").append(" ".repeat(Math.max(0, c.toString().length() - vs.length()))).append(vs);
        }

        private class ConsumerEntry {
            private final int initialOpenOrders;
            private final int incomingOrders;
            private int outgoingGoods;

            public ConsumerEntry(int initialOpenOrders, int incomingOrders) {
                this.initialOpenOrders = initialOpenOrders;
                this.incomingOrders = incomingOrders;
            }

            public int get(Column column) {
                return switch (column) {
                    case INCOMING_ORDERS -> incomingOrders;
                    case INITIAL_OPEN_ORDERS -> initialOpenOrders;
                    case OUTGOING_GOODS -> outgoingGoods;
                    case NEW_OPEN_ORDERS -> initialOpenOrders + incomingOrders - outgoingGoods;
                    default -> Integer.MIN_VALUE;
                };
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                for (Column c : Column.values()) {
                    toStringHelper(c, get(c), sb);
                }
                return sb.toString();
            }
        }

        private class ProviderEntry {
            private final int incomingGoods;
            private int outgoingOrders;

            public ProviderEntry(int incomingGoods) {
                this.incomingGoods = incomingGoods;
            }

            public int get(Column column) {
                return switch (column) {
                    case INCOMING_GOODS -> incomingGoods;
                    case OUTGOING_ORDERS -> outgoingOrders;
                    default -> Integer.MIN_VALUE;
                };
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                for (Column c : Column.values()) {
                    toStringHelper(c, get(c), sb);
                }
                return sb.toString();
            }
        }
    }
}
