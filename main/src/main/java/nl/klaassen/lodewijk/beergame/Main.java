package nl.klaassen.lodewijk.beergame;

import nl.klaassen.lodewijk.beergame.gamedata.DistributionChain;
import nl.klaassen.lodewijk.beergame.gamedata.gameplay.ScoreSheet;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorType;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;

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

        DistributorType distributorType = Arrays.stream(DistributorType.getDistributorTypes()).findAny().get();
        int distributorNumber = new Random().nextInt(distributionChain.getAmountOfDistributorsOfType(distributorType)) + 1;
        ScoreSheet scoreSheet = new ScoreSheet(distributionChain.getDistributor(distributorType, distributorNumber), 12, 4, 4);
        scoreSheet.nextRound(Set.of());
    }
}
