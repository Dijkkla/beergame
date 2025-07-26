package nl.klaassen.lodewijk.beergame.gamedata.gameplay;

import nl.klaassen.lodewijk.beergame.gamedata.DistributionChain;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ScoreSheet {
    private final int toStringLeftPad;
    private final DistributionChain.Distributor distributor;
    private final List<Entry> entries = new ArrayList<>();

    public ScoreSheet(DistributionChain.Distributor distributor, int initialStock, int initialIncomingOrders, int initialIncomingGoods) {
        this.distributor = distributor;
        this.toStringLeftPad = Math.max(distributor.self().toString().length(), Stream.concat(distributor.consumers().stream(), distributor.suppliers().stream()).mapToInt(d -> d.toString().length()).max().orElse(5));
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
        entries.getLast().supplierData.get(supplier).outgoingOrders = amount;
    }

    public void deliverGoods() {
        if (distributor.consumers().size() != 1) {
            throw new IllegalStateException(distributor.self() + " has more than one consumer");
        }
        deliverGoods(distributor.consumers().iterator().next(), entries.getLast().requiredOutgoingGoods());
    }

    public void deliverGoods(DistributorId consumer, int amount) {
        if (!distributor.consumers().contains(consumer)) {
            throw new IllegalArgumentException(consumer + " is not a consumer of " + distributor.self());
        }
        entries.getLast().consumerData.get(consumer).outgoingGoods = amount;
    }

    public void nextRound(Collection<GameAction> actions) {
        Entry last = entries.getLast();
        if (last.requiredOutgoingGoods() != last.get(Column.OUTGOING_GOODS)) {
            if (distributor.consumers().size() == 1) {
                deliverGoods();
            } else {
                throw new IllegalStateException("Outgoing goods needs to be " + last.requiredOutgoingGoods() + last);
            }
        }
        Collection<GameAction> filteredActions = actions.stream()
                .filter(this::relevantGameAction)
                .filter(a -> a.round() == last.entryNr)
                .filter(a -> a.to().equals(distributor.self()))
                .collect(Collectors.toSet());
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
            value = entries.get(entry - 1).consumerData.get(distributorId).get(column);
        } else if (distributor.suppliers().contains(distributorId)) {
            value = entries.get(entry - 1).supplierData.get(distributorId).get(column);
        } else {
            throw new IllegalArgumentException(distributorId + " is neither a consumer nor a supplier of " + distributor.self());
        }
        if (value == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Cannot get " + column + " from " + (distributor.consumers().contains(distributorId) ? "consumer " : "supplier ") + distributorId);
        } else {
            return value;
        }
    }

    private boolean relevantGameAction(GameAction action) {
        if (!distributor.self().equals(action.to()) && !distributor.self().equals(action.from())) {
            return false;
        }
        // A GameAction is relevant iff GOODS go from a supplier to a consumer, or the other way around for ORDERS
        return switch (action.type()) {
            case GOODS ->
                    distributor.suppliers().contains(action.from()) || distributor.consumers().contains(action.to());
            case ORDERS ->
                    distributor.consumers().contains(action.from()) || distributor.suppliers().contains(action.to());
        };
    }

    @Override
    public String toString() {
        return "Score sheet of " + distributor + entries;
    }

    public enum Column {
        WEEK_NR, INITIAL_STOCK, INCOMING_GOODS, INCOMING_ORDERS, INITIAL_OPEN_ORDERS, OUTGOING_GOODS, NEW_OPEN_ORDERS, NEW_STOCK, OUTGOING_ORDERS
    }

    private class Entry {
        private final int entryNr;
        private final int initialStock;

        private final Map<DistributorId, SupplierEntry> supplierData;
        private final Map<DistributorId, ConsumerEntry> consumerData;

        public Entry(int initialStock, int initialIncomingOrders, int initialIncomingGoods) {
            this.entryNr = 1;
            this.initialStock = initialStock;
            this.supplierData = distributor.suppliers().stream().collect(Collectors.toMap(s -> s, s -> new SupplierEntry(initialIncomingGoods)));
            this.consumerData = distributor.consumers().stream().collect(Collectors.toMap(c -> c, c -> new ConsumerEntry(0, initialIncomingOrders)));
        }

        public Entry(Collection<GameAction> actions) {
            Entry last = entries.getLast();
            this.entryNr = last.entryNr + 1;
            this.initialStock = last.get(Column.NEW_STOCK);
            this.supplierData = distributor.suppliers().stream().collect(Collectors.toMap(s -> s, s -> {
                int incomingGoods = actions.stream().filter(a -> a.from().equals(s) && a.type() == GameAction.Type.GOODS).findAny().orElseThrow().amount();
                return new SupplierEntry(incomingGoods);
            }));
            this.consumerData = distributor.consumers().stream().collect(Collectors.toMap(c -> c, c -> {
                int initialOpenOrders = last.consumerData.get(c).get(Column.NEW_OPEN_ORDERS);
                int incomingOrders = actions.stream().filter(a -> a.from().equals(c) && a.type() == GameAction.Type.ORDERS).findAny().orElseThrow().amount();
                return new ConsumerEntry(initialOpenOrders, incomingOrders);
            }));
        }

        public int requiredOutgoingGoods() {
            return Math.min(get(Column.INITIAL_STOCK) + get(Column.INCOMING_GOODS), get(Column.INCOMING_ORDERS) + get(Column.INITIAL_OPEN_ORDERS));
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
            StringBuilder sb = new StringBuilder();
            toStringLeftPad(sb, distributor.self().toString());
            for (Column c : Column.values()) {
                sb.append(" | ").append(c);
            }
            toStringLeftPad(sb, "TOTAL");
            for (Column c : Column.values()) {
                toStringColumnValue(sb, c, get(c));
            }
            distributor.suppliers().stream().sorted().forEachOrdered(supplier -> {
                toStringLeftPad(sb, supplier.toString());
                for (Column c : Column.values()) {
                    toStringColumnValue(sb, c, supplierData.get(supplier).get(c));
                }
            });
            distributor.consumers().stream().sorted().forEachOrdered(consumer -> {
                toStringLeftPad(sb, consumer.toString());
                for (Column c : Column.values()) {
                    toStringColumnValue(sb, c, consumerData.get(consumer).get(c));
                }
            });
            sb.append("\n");
            return sb.toString();
        }

        private void toStringLeftPad(StringBuilder sb, String string) {
            sb.append("\n").append(" ".repeat(Math.max(0, toStringLeftPad - string.length()))).append(string);
        }

        private void toStringColumnValue(StringBuilder sb, Column c, int v) {
            String vs = v == Integer.MIN_VALUE ? "" : Integer.toString(v);
            sb.append(" | ").append(" ".repeat(Math.max(0, c.toString().length() - vs.length()))).append(vs);
        }

        private static class SupplierEntry {
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
        }

        private static class ConsumerEntry {
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
        }
    }
}
