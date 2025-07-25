package nl.klaassen.lodewijk.beergame;

import nl.klaassen.lodewijk.beergame.gamedata.DistributionChain;
import nl.klaassen.lodewijk.beergame.gamedata.gameplay.ScoreSheet;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorType;

public class Main {
    public static void main(String[] args) {
        DistributionChain distributionChain = new DistributionChain(3, 7);

        System.out.println(distributionChain);

        for (DistributorType type : DistributorType.getDistributorTypes()) {
            int number;
            if ((number = distributionChain.getAmountOfDistributorsOfType(type)) > 0) {
                DistributionChain.Distributor distributor = distributionChain.getDistributor(type, number);
                ScoreSheet scoreSheet = new ScoreSheet(distributor, 12, 4, 4);
                System.out.println(scoreSheet);
            }
        }
    }
}
