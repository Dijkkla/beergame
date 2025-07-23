package nl.klaassen.lodewijk.beergame.gamedata.identifiers;

public enum DistributorType {
    FACTORY_PROVIDER(Integer.MIN_VALUE),
    FACTORY(1),
    WAREHOUSE(2),
    WHOLESALER(3),
    RETAILER(4),
    RETAILER_CONSUMER(Integer.MAX_VALUE);

    public final int hierarchy;

    DistributorType(int hierarchy) {
        this.hierarchy = hierarchy;
    }
}
