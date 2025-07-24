package nl.klaassen.lodewijk.beergame.gamedata.identifiers;

import java.util.Arrays;
import java.util.Comparator;

public enum DistributorType {
    FIRST_SUPPLIER(Integer.MIN_VALUE, false),
    FACTORY(1),
    WAREHOUSE(2),
    WHOLESALER(3),
    RETAILER(4),
    FINAL_CONSUMER(Integer.MAX_VALUE, false);

    private final boolean canBeDistributor;
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

    public static DistributorType[] getDistributorTypes() {
        return Arrays.stream(values()).filter(t -> t.canBeDistributor).sorted(getComparator()).toArray(DistributorType[]::new);
    }
}
