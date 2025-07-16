package nl.klaassen.lodewijk.gamedata.identifiers;

public record DistributorId(DistributorType type, int number) {
    @Override
    public String toString() {
        return type.name() + ":" + number;
    }
}
