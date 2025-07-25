package nl.klaassen.lodewijk.beergame.gamedata.gameplay;

import nl.klaassen.lodewijk.beergame.gamedata.DistributionChain;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;

import java.util.*;
import java.util.stream.Collectors;


public class ScoreSheet {
    private final List<Entry> entries = new ArrayList<>();
    private final DistributionChain.Distributor distributor;

    public ScoreSheet(DistributionChain.Distributor distributor, int initialStock, int initialIncomingOrders, int initialIncomingGoods) {
        this.distributor = distributor;
        entries.add(new Entry(initialStock, initialIncomingOrders, initialIncomingGoods));
    }

    public void placeOrder(int amount) {
        if (distributor.suppliers().size() != 1) {
            throw new IllegalStateException(distributor.self() + " has more than one supplier");
        }
        placeOrder(distributor.suppliers().iterator().next(), amount);
    }

    public void placeOrder(DistributorId supplier, int amount) {
        if (!distributor.suppliers().contains(supplier)) {
            throw new IllegalArgumentException(supplier + " is not a supplier of " + distributor.self());
        }
    }

    public void sendGoods() {
        if (distributor.consumers().size() != 1) {
            throw new IllegalStateException(distributor.self() + " has more than one consumer");
        }
    }

    public void sendGoods(DistributorId consumer, int amount) {
        if (!distributor.consumers().contains(consumer)) {
            throw new IllegalArgumentException(consumer + " is not a consumer of " + distributor.self());
        }
    }

    public void nextRound(Collection<GameAction> actions) {
        Collection<GameAction> filteredActions = actions.stream().filter(a -> a.round() == entries.size() && a.to().equals(distributor.self())).collect(Collectors.toSet());
        if (filteredActions.size() != distributor.consumers().size() + distributor.suppliers().size()) {
            throw new IllegalArgumentException("Invalid set of gameActions");
        }
        entries.add(new Entry(filteredActions));
    }

    public int get(int entry, Column column) {
        return entries.get(entry - 1).get(column);
    }

    public int get(int entry, Column column, DistributorId distributorId) {
        int value;
        if (distributor.consumers().contains(distributorId)) {
            value = entries.get(entry - 1).getFromConsumer(column, distributorId);
        } else if (distributor.suppliers().contains(distributorId)) {
            value = entries.get(entry - 1).getFromSupplier(column, distributorId);
        } else {
            throw new IllegalArgumentException(distributorId + " is neither a consumer nor a supplier of " + distributor.self());
        }
        if (value == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Cannot get " + column + " from " + (distributor.consumers().contains(distributorId) ? "consumer " : "supplier ") + distributorId);
        } else {
            return value;
        }
    }

    @Override
    public String toString() {
        return "Sheet of " + distributor.self() + "\n\tConsumers: " + distributor.consumers() + "\n\tSuppliers: " + distributor.suppliers() + "\n" + entries;
    }

    public enum Column {
        WEEK_NR, INITIAL_STOCK, INCOMING_GOODS, INCOMING_ORDERS, INITIAL_OPEN_ORDERS, OUTGOING_GOODS, NEW_OPEN_ORDERS, NEW_STOCK, OUTGOING_ORDERS
    }

    private class Entry {
        private final int entryNr;
        private final int initialStock;

        private final Map<DistributorId, ConsumerEntry> consumerData;
        private final Map<DistributorId, SupplierEntry> supplierData;

        public Entry(int initialStock, int initialIncomingOrders, int initialIncomingGoods) {
            this.entryNr = 1;
            this.initialStock = initialStock;
            this.consumerData = distributor.consumers().stream().collect(Collectors.toMap(c -> c, c -> new ConsumerEntry(0, initialIncomingOrders)));
            this.supplierData = distributor.suppliers().stream().collect(Collectors.toMap(s -> s, s -> new SupplierEntry(initialIncomingGoods)));
        }

        public Entry(Collection<GameAction> actions) {
            Entry last = entries.getLast();
            this.entryNr = last.entryNr + 1;
            this.initialStock = last.get(Column.NEW_STOCK);
            this.consumerData = distributor.consumers().stream().collect(Collectors.toMap(c -> c, c -> new ConsumerEntry(last.getFromConsumer(Column.NEW_OPEN_ORDERS, c), actions.stream().filter(a -> a.from().equals(c) && a.type() == GameAction.Type.ORDERS).findAny().get().amount())));
            this.supplierData = distributor.suppliers().stream().collect(Collectors.toMap(s -> s, s -> new SupplierEntry(actions.stream().filter(a -> a.from().equals(s) && a.type() == GameAction.Type.GOODS).findAny().get().amount())));
        }

        public int getFromConsumer(Column column, DistributorId distributorId) {
            return consumerData.get(distributorId).get(column);
        }

        public int getFromSupplier(Column column, DistributorId distributorId) {
            return supplierData.get(distributorId).get(column);
        }

        public int get(Column column) {
            return switch (column) {
                case WEEK_NR -> entryNr;
                case INITIAL_STOCK -> initialStock;
                case INCOMING_GOODS -> supplierData.values().stream().mapToInt(s -> s.incomingGoods).sum();
                case INCOMING_ORDERS -> consumerData.values().stream().mapToInt(c -> c.incomingOrders).sum();
                case INITIAL_OPEN_ORDERS -> consumerData.values().stream().mapToInt(c -> c.initialOpenOrders).sum();
                case OUTGOING_GOODS -> consumerData.values().stream().mapToInt(c -> c.outgoingGoods).sum();
                case NEW_OPEN_ORDERS ->
                        consumerData.values().stream().mapToInt(c -> c.get(Column.NEW_OPEN_ORDERS)).sum();
                case NEW_STOCK -> initialStock + get(Column.INCOMING_GOODS) - get(Column.OUTGOING_GOODS);
                case OUTGOING_ORDERS -> supplierData.values().stream().mapToInt(s -> s.outgoingOrders).sum();
            };
        }

        @Override
        public String toString() {
            final int LEFT_PAD = 16;
            StringBuilder sb = new StringBuilder();
            sb.append("\n").append(" ".repeat(LEFT_PAD));
            for (Column c : Column.values()) {
                sb.append(" | ").append(c);
            }
            sb.append("\n").append(" ".repeat(LEFT_PAD));
            for (Column c : Column.values()) {
                toStringHelper(c, get(c), sb);
            }
            distributor.consumers().stream().sorted().forEachOrdered(c -> sb.append("\n").append(" ".repeat(Math.max(0, LEFT_PAD - c.toString().length()))).append(c).append(consumerData.get(c).toString()));
            distributor.suppliers().stream().sorted().forEachOrdered(s -> sb.append("\n").append(" ".repeat(Math.max(0, LEFT_PAD - s.toString().length()))).append(s).append(supplierData.get(s).toString()));
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

        private class SupplierEntry {
            private final int incomingGoods;
            private int outgoingOrders;

            public SupplierEntry(int incomingGoods) {
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
