package nl.klaassen.lodewijk.beergame.gamedata.identifiers;

import java.util.Objects;

public record DistributorId(DistributorType type, int number) implements Comparable<DistributorId> {
    @Override
    public String toString() {
        return type.name() + ":" + number;
    }

    @Override
    public int compareTo(DistributorId o) {
        Objects.requireNonNull(o);
        int result = DistributorType.getComparator().compare(this.type, o.type);
        return result == 0 ? this.number - o.number : result;
    }
}
