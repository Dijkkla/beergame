package nl.klaassen.lodewijk.beergame.gamedata;

import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorType;

import java.util.*;

public class DistributionChain {
    private final DistributorId factorySupplier = new DistributorId(DistributorType.TOP_LEVEL_SUPPLIER, 1);
    //    private final Set<Distributor> distributors;
    private final DistributorId retailerConsumer = new DistributorId(DistributorType.TOP_LEVEL_CONSUMER, 1);

    public DistributionChain(int numberOfSuppliers, int numberOfConsumers) {
        this(numberOfSuppliers, numberOfConsumers, Arrays.stream(DistributorType.values()).distinct().filter(t -> t.canBeDistributor).toArray(DistributorType[]::new));
    }

    public DistributionChain(int numberOfSuppliers, int numberOfConsumers, Collection<DistributorType> distributorTypes) {
        this(numberOfSuppliers, numberOfConsumers, distributorTypes.stream().distinct().filter(t -> t.canBeDistributor).toArray(DistributorType[]::new));
    }

    private DistributionChain(int numberOfSuppliers, int numberOfConsumers, DistributorType[] distributorTypes) {
        Arrays.sort(distributorTypes, DistributorType.getComparator());
        if (numberOfSuppliers <= 0) {
            throw new IllegalArgumentException("number of suppliers must be 1 or greater (was " + numberOfSuppliers + ")");
        }
        if (numberOfConsumers <= 0) {
            throw new IllegalArgumentException("number of consumers must be 1 or greater (was " + numberOfConsumers + ")");
        }
        System.out.println(Arrays.toString(distributorTypes));
        Set<DistributorId> distributorIds = new HashSet<>();
        for (int distributorTypeIndex = 0; distributorTypeIndex < distributorTypes.length; distributorTypeIndex++) {
            int numberOfDistributorsOfType = (int) (Math.pow(numberOfConsumers, distributorTypeIndex) * Math.pow(numberOfSuppliers, (distributorTypes.length - distributorTypeIndex)));
            for (int distributorNumber = 0; distributorNumber < numberOfDistributorsOfType; distributorNumber++) {
                distributorIds.add(new DistributorId(distributorTypes[distributorTypeIndex], distributorNumber + 1));
            }
        }
        System.out.println(distributorIds.stream().sorted().toList());
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
