package nl.klaassen.lodewijk.beergame.gamedata.identifiers;

public record DistributorId(DistributorType type, int number) {
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
}
