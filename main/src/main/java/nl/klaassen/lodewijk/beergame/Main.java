package nl.klaassen.lodewijk.beergame;

import nl.klaassen.lodewijk.beergame.gamedata.Distributor;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorType;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Distributor distributor = new Distributor(
                new DistributorId(DistributorType.WHOLESALER, 3),
                Set.of(new DistributorId(DistributorType.RETAILER, 5), new DistributorId(DistributorType.RETAILER, 6)),
                Set.of(new DistributorId(DistributorType.WAREHOUSE, 2))
        );

        System.out.println(distributor);
    }
}
