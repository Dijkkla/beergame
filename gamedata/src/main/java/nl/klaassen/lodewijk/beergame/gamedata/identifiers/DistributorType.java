package nl.klaassen.lodewijk.beergame.gamedata.identifiers;

import java.util.Comparator;

public enum DistributorType {
    FACTORY_SUPPLIER(Integer.MIN_VALUE),
    FACTORY(1),
    WAREHOUSE(2),
    WHOLESALER(3),
    RETAILER(4),
    RETAILER_CONSUMER(Integer.MAX_VALUE);

    private final int hierarchy;

    DistributorType(int hierarchy) {
        this.hierarchy = hierarchy;
    }

    public static Comparator<DistributorType> getComparator() {
        return Comparator.comparingInt(distributorType -> distributorType.hierarchy);
    }
}
