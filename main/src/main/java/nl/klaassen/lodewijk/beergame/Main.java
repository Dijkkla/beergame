package nl.klaassen.lodewijk.beergame;

import nl.klaassen.lodewijk.beergame.gamedata.DistributionChain;
import nl.klaassen.lodewijk.beergame.gamedata.gameplay.ScoreSheet;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorType;

public class Main {
    public static void main(String[] args) {
        DistributionChain distributionChain = new DistributionChain(1, 3, 7);

        System.out.println(distributionChain);

        for (DistributorType type : DistributorType.getDistributorTypes()) {
            DistributionChain.Distributor distributor = distributionChain.getDistributor(type, distributionChain.getAmountOfDistributorsOfType(type));
            if (distributor != null) {
                ScoreSheet scoreSheet = new ScoreSheet(distributor, 12, 4, 4);
                System.out.println(scoreSheet);
            }
        }
    }
}
