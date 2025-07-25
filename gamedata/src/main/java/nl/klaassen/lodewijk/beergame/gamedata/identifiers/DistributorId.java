package nl.klaassen.lodewijk.beergame.gamedata.identifiers;

public record DistributorId(int chainNumber, DistributorType type, int number) implements Comparable<DistributorId> {
    @Override
    public String toString() {
        return chainNumber + ":" + type.name() + ":" + number;
    }

    @Override
    public int compareTo(DistributorId o) {
        int result = this.chainNumber - o.chainNumber;
        result = result == 0 ? DistributorType.getComparator().compare(this.type, o.type) : result;
        return result == 0 ? this.number - o.number : result;
    }
}
