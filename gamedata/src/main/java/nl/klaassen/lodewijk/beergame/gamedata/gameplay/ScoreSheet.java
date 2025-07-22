package nl.klaassen.lodewijk.beergame.gamedata.gameplay;

import lombok.Getter;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ScoreSheet {
    private final List<Entry> entries = new ArrayList<>();
    private final DistributorId id;
    private final Set<DistributorId> consumers;
    private final Set<DistributorId> providers;

    public ScoreSheet(DistributorId id, Collection<DistributorId> consumers, Collection<DistributorId> providers, int initialStock, int initialIncomingOrders, int initialIncomingGoods) {
        this.id = id;
        this.consumers = Set.copyOf(consumers);
        this.providers = Set.copyOf(providers);
        entries.add(new Entry(initialStock, initialIncomingOrders, initialIncomingGoods));
    }

    public void nextRound(Collection<GameAction> actions) {
        Collection<GameAction> filteredActions = actions.stream().filter(a -> a.round() == entries.size() && a.to().equals(id)).collect(Collectors.toSet());
        if (filteredActions.size() != consumers.size() + providers.size()) {
            throw new IllegalArgumentException("invalid set of gameActions");
        }
        entries.add(new Entry(filteredActions));
    }

    @Override
    public String toString() {
        return "Sheet of " + id + "\n\tConsumers: " + consumers + "\n\tProviders: " + providers + "\n" + entries;
    }

    @Getter
    private class Entry {
        private final int initialStock;
        private final Map<DistributorId, ConsumerEntry> consumerData;
        private final Map<DistributorId, ProviderEntry> providerData;

        public Entry(int initialStock, int initialIncomingOrders, int initialIncomingGoods) {
            this.initialStock = initialStock;
            this.consumerData = Map.ofEntries(consumers.stream().map(distributorId -> Map.entry(distributorId, new ConsumerEntry(0, initialIncomingOrders))).toArray(Map.Entry[]::new));
            this.providerData = Map.ofEntries(providers.stream().map(distributorId -> Map.entry(distributorId, new ProviderEntry(initialIncomingGoods))).toArray(Map.Entry[]::new));
        }

        public Entry(Collection<GameAction> actions) {
            Entry last = entries.getLast();
            this.initialStock = last.getNewStock();
            this.consumerData = Map.ofEntries(consumers.stream().map(distributorId -> Map.entry(distributorId, new ConsumerEntry(last.consumerData.get(distributorId).getNewOpenOrders(), actions.stream().filter(a -> a.from().equals(distributorId) && a.type() == GameAction.GameActionType.ORDERS).findAny().get().amount()))).toArray(Map.Entry[]::new));
            this.providerData = Map.ofEntries(providers.stream().map(distributorId -> Map.entry(distributorId, new ProviderEntry(actions.stream().filter(a -> a.from().equals(distributorId) && a.type() == GameAction.GameActionType.GOODS).findAny().get().amount()))).toArray(Map.Entry[]::new));
        }

        public int getNewStock() {
            return initialStock + getIncomingGoods() - getOutgoingGoods();
        }

        public int getInitialOpenOrders() {
            return consumerData.values().stream().mapToInt(ConsumerEntry::getInitialOpenOrders).sum();
        }

        public int getIncomingOrders() {
            return consumerData.values().stream().mapToInt(ConsumerEntry::getIncomingOrders).sum();
        }

        public int getOutgoingGoods() {
            return consumerData.values().stream().mapToInt(ConsumerEntry::getOutgoingGoods).sum();
        }

        public int getNewOpenOrders() {
            return consumerData.values().stream().mapToInt(ConsumerEntry::getNewOpenOrders).sum();
        }

        public int getIncomingGoods() {
            return providerData.values().stream().mapToInt(ProviderEntry::getIncomingGoods).sum();
        }

        public int getOutgoingOrders() {
            return providerData.values().stream().mapToInt(ProviderEntry::getOutgoingOrders).sum();
        }

        @Override
        public String toString() {
            final int LEFT_PAD = 16;
            StringBuilder sb = new StringBuilder();
            sb.append("\n").append(" ".repeat(LEFT_PAD));
            for (Columns c : Columns.values()) {
                sb.append(" | ").append(c);
            }
            sb.append("\n").append(" ".repeat(LEFT_PAD - 5)).append("TOTAL");
            for (Columns c : Columns.values()) {
                String v = Integer.toString(switch (c) {
                    case INITIAL_STOCK -> getInitialStock();
                    case INCOMING_GOODS -> getIncomingGoods();
                    case INCOMING_ORDERS -> getIncomingOrders();
                    case INITIAL_OPEN_ORDERS -> getInitialOpenOrders();
                    case OUTGOING_GOODS -> getOutgoingGoods();
                    case NEW_OPEN_ORDERS -> getNewOpenOrders();
                    case NEW_STOCK -> getNewStock();
                    case OUTGOING_ORDERS -> getOutgoingOrders();
                });
                sb.append(" | ").append(" ".repeat(Math.max(0, c.toString().length() - v.length()))).append(v);
            }
            consumerData.entrySet().stream().sorted((e1, e2) -> {
                DistributorId d1 = e1.getKey();
                DistributorId d2 = e2.getKey();
                int result = d1.type().ordinal() - d2.type().ordinal();
                return result == 0 ? d1.number() - d2.number() : result;
            }).forEach(e -> {
                sb.append("\n").append(" ".repeat(Math.max(0, LEFT_PAD - e.getKey().toString().length()))).append(e.getKey().toString());
                sb.append(e.getValue().toString());
            });
            providerData.entrySet().stream().sorted((e1, e2) -> {
                DistributorId d1 = e1.getKey();
                DistributorId d2 = e2.getKey();
                int result = d1.type().ordinal() - d2.type().ordinal();
                return result == 0 ? d1.number() - d2.number() : result;
            }).forEach(e -> {
                sb.append("\n").append(" ".repeat(Math.max(0, LEFT_PAD - e.getKey().toString().length()))).append(e.getKey().toString());
                sb.append(e.getValue().toString());
            });
            sb.append("\n");
            return sb.toString();
        }

        private enum Columns {
            INITIAL_STOCK,
            INCOMING_GOODS,
            INCOMING_ORDERS,
            INITIAL_OPEN_ORDERS,
            OUTGOING_GOODS,
            NEW_OPEN_ORDERS,
            NEW_STOCK,
            OUTGOING_ORDERS
        }

        @Getter
        private class ConsumerEntry {
            private final int initialOpenOrders;
            private final int incomingOrders;
            private int outgoingGoods;

            public ConsumerEntry(int initialOpenOrders, int incomingOrders) {
                this.initialOpenOrders = initialOpenOrders;
                this.incomingOrders = incomingOrders;
            }

            public int getNewOpenOrders() {
                return initialOpenOrders + incomingOrders - outgoingGoods;
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                for (Columns c : Columns.values()) {
                    String v = Integer.toString(switch (c) {
                        case INCOMING_ORDERS -> getIncomingOrders();
                        case INITIAL_OPEN_ORDERS -> getInitialOpenOrders();
                        case OUTGOING_GOODS -> getOutgoingGoods();
                        case NEW_OPEN_ORDERS -> getNewOpenOrders();
                        default -> -1;
                    });
                    v = "-1".equals(v) ? "" : v;
                    sb.append(" | ").append(" ".repeat(Math.max(0, c.toString().length() - v.length()))).append(v);
                }
                return sb.toString();
            }
        }

        @Getter
        private class ProviderEntry {
            private final int incomingGoods;
            private int outgoingOrders;

            public ProviderEntry(int incomingGoods) {
                this.incomingGoods = incomingGoods;
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                for (Columns c : Columns.values()) {
                    String v = Integer.toString(switch (c) {
                        case INCOMING_GOODS -> getIncomingGoods();
                        case OUTGOING_ORDERS -> getOutgoingOrders();
                        default -> -1;
                    });
                    v = "-1".equals(v) ? "" : v;
                    sb.append(" | ").append(" ".repeat(Math.max(0, c.toString().length() - v.length()))).append(v);
                }
                return sb.toString();
            }
        }
    }
}
