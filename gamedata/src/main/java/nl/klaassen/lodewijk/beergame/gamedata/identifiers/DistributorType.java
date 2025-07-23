package nl.klaassen.lodewijk.beergame.gamedata.identifiers;

import java.util.Comparator;

public enum DistributorType {
    TOP_LEVEL_SUPPLIER(Integer.MIN_VALUE, false),
    FACTORY(1),
    WAREHOUSE(2),
    WHOLESALER(3),
    RETAILER(4),
    TOP_LEVEL_CONSUMER(Integer.MAX_VALUE, false);

    public final boolean canBeDistributor;
    private final int hierarchy;

    DistributorType(int hierarchy) {
        this.hierarchy = hierarchy;
        this.canBeDistributor = true;
    }

    DistributorType(int hierarchy, boolean canBeDistributor) {
        this.hierarchy = hierarchy;
        this.canBeDistributor = canBeDistributor;
    }

    public static Comparator<DistributorType> getComparator() {
        return Comparator.comparingInt(distributorType -> distributorType.hierarchy);
    }
}
