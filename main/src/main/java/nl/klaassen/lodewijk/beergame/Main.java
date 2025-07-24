package nl.klaassen.lodewijk.beergame;

import nl.klaassen.lodewijk.beergame.gamedata.DistributionChain;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorType;

public class Main {
    public static void main(String[] args) {
        DistributionChain distributionChain = new DistributionChain(2, 3);

//        System.out.println(distributionChain);

        DistributionChain.Distributor distributor = distributionChain.getDistributor(DistributorType.WHOLESALER, 1);
    }
}
