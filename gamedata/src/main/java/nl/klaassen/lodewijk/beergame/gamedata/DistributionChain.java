package nl.klaassen.lodewijk.beergame.gamedata;

import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorType;

import java.util.*;
import java.util.stream.Collectors;

public class DistributionChain {
    private final int chainNumber;
    private final Set<Distributor> distributors;
    private final int numberOfSuppliers;
    private final int numberOfConsumers;
    private final Map<DistributorType, Integer> amountOfDistributorsByType;
    private final int size;

    public DistributionChain(int chainNumber, int numberOfSuppliers, int numberOfConsumers) {
        this(chainNumber, numberOfSuppliers, numberOfConsumers, DistributorType.getDistributorTypes());
    }

    public DistributionChain(int chainNumber, int numberOfSuppliers, int numberOfConsumers, Collection<DistributorType> distributorTypes) {
        this(chainNumber, numberOfSuppliers, numberOfConsumers, Arrays.stream(DistributorType.getDistributorTypes()).distinct().filter(distributorTypes::contains).sorted(DistributorType.getComparator()).toArray(DistributorType[]::new));
    }

    private DistributionChain(int chainNumber, int numberOfSuppliers, int numberOfConsumers, DistributorType[] distributorTypes) {
        if (numberOfSuppliers <= 0) {
            throw new IllegalArgumentException("Number of suppliers must be 1 or greater (was " + numberOfSuppliers + ")");
        }
        if (numberOfConsumers <= 0) {
            throw new IllegalArgumentException("Number of consumers must be 1 or greater (was " + numberOfConsumers + ")");
        }
        if (distributorTypes.length <= 1) {
            throw new IllegalArgumentException("A distribution chain must have at least 2 types of distributors");
        } else if (Arrays.stream(distributorTypes).distinct().count() != distributorTypes.length) {
            throw new IllegalArgumentException("A distributor type may not be used more than once in a distribution chain");
        }
        this.chainNumber = chainNumber;
        this.numberOfSuppliers = numberOfSuppliers;
        this.numberOfConsumers = numberOfConsumers;
        this.amountOfDistributorsByType = new EnumMap<>(DistributorType.class);

        Map<DistributorId, Set<DistributorId>> supplierMap = new HashMap<>();
        Map<DistributorId, Set<DistributorId>> consumerMap = new HashMap<>();

        for (int distributionLayer = 0; distributionLayer < distributorTypes.length - 1; distributionLayer++) {
            int layerSize = (int) Math.pow(numberOfSuppliers, distributorTypes.length - 1 - distributionLayer) * (int) Math.pow(numberOfConsumers, distributionLayer);
            int nextLayerSize = layerSize / numberOfSuppliers * numberOfConsumers;
            amountOfDistributorsByType.putIfAbsent(distributorTypes[distributionLayer], layerSize);
            amountOfDistributorsByType.putIfAbsent(distributorTypes[distributionLayer + 1], nextLayerSize);
            for (int s = 0; s < layerSize; s++) {
                int supplierNumber = s + 1;
                DistributorId supplier = new DistributorId(chainNumber, distributorTypes[distributionLayer], supplierNumber);
                for (int c = 0; c < numberOfConsumers; c++) {
                    int consumerNumber = (s * numberOfConsumers + c) % nextLayerSize + 1;
                    DistributorId consumer = new DistributorId(chainNumber, distributorTypes[distributionLayer + 1], consumerNumber);
                    supplierMap.computeIfAbsent(supplier, set -> new HashSet<>()).add(consumer);
                    consumerMap.computeIfAbsent(consumer, set -> new HashSet<>()).add(supplier);
                }
            }
        }
        size = amountOfDistributorsByType.values().stream().mapToInt(i -> i).sum();

        Set<DistributorId> allIds = new HashSet<>();
        allIds.addAll(supplierMap.keySet());
        allIds.addAll(consumerMap.keySet());

        Set<DistributorId> FINAL_CONSUMER_SET = Set.of(new DistributorId(chainNumber, DistributorType.FINAL_CONSUMER, 1));
        Set<DistributorId> FIRST_SUPPLIER_SET = Set.of(new DistributorId(chainNumber, DistributorType.FIRST_SUPPLIER, 1));
        distributors = allIds.stream().map(id -> new Distributor(id, supplierMap.getOrDefault(id, FINAL_CONSUMER_SET), consumerMap.getOrDefault(id, FIRST_SUPPLIER_SET))).collect(Collectors.toSet());
    }

    public int getAmountOfDistributorsOfType(DistributorType type) {
        return amountOfDistributorsByType.get(type);
    }

    public Distributor getDistributor(DistributorId distributorId) {
        return distributors.stream().filter(d -> d.self().equals(distributorId)).findAny().orElse(null);
    }

    public Distributor getDistributor(DistributorType type, int number) {
        return getDistributor(new DistributorId(this.chainNumber, type, number));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Distribution chain ").append(chainNumber).append(", consisting of ").append(size).append(" distributors (");
        amountOfDistributorsByType.forEach((key, value) -> sb.append(value).append(" ").append(key).append("s, "));
        sb.setLength(sb.length() - 2);
        sb.append("), with each distributor having ").append(numberOfSuppliers).append(" suppliers and ").append(numberOfConsumers).append(" consumers");
        return sb.toString();
    }

}
