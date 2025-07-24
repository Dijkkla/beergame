package nl.klaassen.lodewijk.beergame;

import nl.klaassen.lodewijk.beergame.gamedata.DistributionChain;

public class Main {
    public static void main(String[] args) {
        DistributionChain distributionChain = new DistributionChain(3, 7);

        System.out.println(distributionChain);
    }
}
