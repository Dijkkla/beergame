package nl.klaassen.lodewijk.beergame.gamedata.identifiers;

import java.util.Objects;

public record DistributorId(DistributorType type, int number) implements Comparable<DistributorId> {
    public DistributorId {
        if (number <= 0) {
            throw new IllegalArgumentException("number must be 1 or greater");
        }
    }

    @Override
    public String toString() {
        return type.name() + ":" + number;
    }

    @Override
    public int compareTo(DistributorId o) {
        int result = DistributorType.getComparator().compare(this.type, o.type);
        return result == 0 ? this.number - o.number : result;
    }
}
