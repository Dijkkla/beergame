package nl.klaassen.lodewijk.beergame.gamedata;

import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorType;

import java.util.*;

public class DistributionChain {
    public static final Set<DistributorId> FINAL_COMSUMER_SET = Set.of(new DistributorId(DistributorType.FINAL_CONSUMER, 1));
    public static final Set<DistributorId> FIRST_SUPPLIER_SET = Set.of(new DistributorId(DistributorType.FIRST_SUPPLIER, 1));

    public Set<Distributor> getDistributors() {
        return distributors;
    }

    private final Set<Distributor> distributors;
    private final int numberOfSuppliers;
    private final int numberOfConsumers;
    private final DistributorType[] distributorTypes;

    public DistributionChain(int numberOfSuppliers, int numberOfConsumers) {
        this(numberOfSuppliers, numberOfConsumers, DistributorType.getDistributorTypes());
    }

    public DistributionChain(int numberOfSuppliers, int numberOfConsumers, Collection<DistributorType> distributorTypes) {
        this(numberOfSuppliers, numberOfConsumers, Arrays.stream(DistributorType.getDistributorTypes()).distinct().filter(distributorTypes::contains).sorted(DistributorType.getComparator()).toArray(DistributorType[]::new));
    }

    private DistributionChain(int numberOfSuppliers, int numberOfConsumers, DistributorType[] distributorTypes) {
        if (numberOfSuppliers <= 0) {
            throw new IllegalArgumentException("Number of suppliers must be 1 or greater (was " + numberOfSuppliers + ")");
        }
        if (numberOfConsumers <= 0) {
            throw new IllegalArgumentException("Number of consumers must be 1 or greater (was " + numberOfConsumers + ")");
        }
        if (distributorTypes.length <= 1) {
            throw new IllegalArgumentException("A distribution chain must have at least 2 types of distributors");
        }
        this.numberOfSuppliers = numberOfSuppliers;
        this.numberOfConsumers = numberOfConsumers;
        this.distributorTypes = distributorTypes;

        Map<DistributorId, Set<DistributorId>> supplierMap = new HashMap<>();
        Map<DistributorId, Set<DistributorId>> consumerMap = new HashMap<>();

        for (int distributionLayer = 0; distributionLayer < distributorTypes.length - 1; distributionLayer++) {
            int layerSize = (int) Math.pow(numberOfSuppliers, distributorTypes.length - 1 - distributionLayer) * (int) Math.pow(numberOfConsumers, distributionLayer);
            int nextLayerSize = layerSize / numberOfSuppliers * numberOfConsumers;
            for (int s = 0; s < layerSize; s++) {
                int supplierNumber = s + 1;
                DistributorId supplier = new DistributorId(distributorTypes[distributionLayer], supplierNumber);
                for (int c = 0; c < numberOfConsumers; c++) {
                    int consumerNumber = (s * numberOfConsumers + c) % nextLayerSize + 1;
                    DistributorId consumer = new DistributorId(distributorTypes[distributionLayer + 1], consumerNumber);
                    supplierMap.computeIfAbsent(supplier, set -> new HashSet<>()).add(consumer);
                    consumerMap.computeIfAbsent(consumer, set -> new HashSet<>()).add(supplier);
                }
            }
        }

        Set<DistributorId> allIds = new HashSet<>();
        allIds.addAll(supplierMap.keySet());
        allIds.addAll(consumerMap.keySet());
        Set<Distributor> tmpDistributors = new HashSet<>();

        for (DistributorId id : allIds) {
            tmpDistributors.add(new Distributor(this, id, Set.copyOf(supplierMap.getOrDefault(id, FINAL_COMSUMER_SET)), Set.copyOf(consumerMap.getOrDefault(id, FIRST_SUPPLIER_SET))));
        }
        distributors = Set.copyOf(tmpDistributors);
    }

    public Distributor getDistributor(DistributorId distributorId) {
        //TODO: throw an exception instead of returning null if the distributor does not exist?
        return distributors.stream().filter(d -> d.self.equals(distributorId)).findAny().orElse(null);
    }

    public Distributor getDistributor(DistributorType type, int number) {
        return getDistributor(new DistributorId(type, number));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        distributors.stream().sorted().forEachOrdered(distributor -> sb.append("\n").append(distributor.suppliers.stream().sorted().toList()).append(" -> ").append(distributor.self).append(" -> ").append(distributor.consumers.stream().sorted().toList()));
        return sb.toString();
    }

    public record Distributor(DistributionChain chain, DistributorId self, Set<DistributorId> consumers,
                              Set<DistributorId> suppliers) implements Comparable<Distributor> {
        public Distributor {
            if (consumers.stream().anyMatch(suppliers::contains)) {
                throw new IllegalArgumentException("Distributors may not be registered as both a supplier and a consumer");
            }
        }

        @Override
        public int compareTo(Distributor o) {
            int result = this.chain.hashCode() - o.chain.hashCode();
            return result == 0 ? self.compareTo(o.self) : result;
        }
    }
}
