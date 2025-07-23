package nl.klaassen.lodewijk.beergame.gamedata.identifiers;

import java.util.Objects;

public record DistributorId(DistributorType type, int number) implements Comparable<DistributorId> {
//    private static final Collection<DistributorId> existingDistributorIds = new HashSet<>();
//
//    // This constructor should not be used. Use the static method `DistributorId.of()` instead.
//    public DistributorId {
//        if (existingDistributorIds.contains(this)) {
//            //TODO: make custom exception
//            throw new RuntimeException("DistributorId exists twice");
//        }
//        existingDistributorIds.add(this);
//    }
//
//    public static DistributorId of(DistributorType type, int number) {
//        return existingDistributorIds.stream()
//                .filter(id -> id.type == type && id.number == number)
//                .findFirst()
//                .orElse(new DistributorId(type, number));
//    }

    @Override
    public String toString() {
        return type.name() + ":" + number;
    }

    @Override
    public int compareTo(DistributorId o) {
        Objects.requireNonNull(o);
        int result = this.type.hierarchy - o.type.hierarchy;
        return result == 0 ? this.number - o.number : result;
    }
}
