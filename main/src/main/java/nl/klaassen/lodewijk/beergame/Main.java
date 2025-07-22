package nl.klaassen.lodewijk.beergame;

import nl.klaassen.lodewijk.beergame.gamedata.Distributor;
import nl.klaassen.lodewijk.beergame.gamedata.gameplay.GameAction;
import nl.klaassen.lodewijk.beergame.gamedata.gameplay.ScoreSheet;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorType;

import java.util.Set;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        Distributor distributor = new Distributor(
                new DistributorId(DistributorType.WHOLESALER, 3),
                Set.of(new DistributorId(DistributorType.RETAILER, 5), new DistributorId(DistributorType.RETAILER, 6)),
                Set.of(new DistributorId(DistributorType.WAREHOUSE, 2))
        );

//        System.out.println(distributor);


        ScoreSheet scoreSheet = new ScoreSheet(
                distributor.getId(),
                distributor.getConsumers(),
                distributor.getProviders(),
                12, 4, 4
        );

//        System.out.println(scoreSheet);

        Set<GameAction> gameActions = Set.of(
                new GameAction(1, new DistributorId(DistributorType.RETAILER, 5), new DistributorId(DistributorType.WHOLESALER, 3), 5, GameAction.GameActionType.ORDERS),
                new GameAction(1, new DistributorId(DistributorType.RETAILER, 6), new DistributorId(DistributorType.WHOLESALER, 3), 7, GameAction.GameActionType.ORDERS),
                new GameAction(1, new DistributorId(DistributorType.WAREHOUSE, 2), new DistributorId(DistributorType.WHOLESALER, 3), 9, GameAction.GameActionType.GOODS),
                new GameAction(2, new DistributorId(DistributorType.RETAILER, 5), new DistributorId(DistributorType.WHOLESALER, 3), 6, GameAction.GameActionType.ORDERS),
                new GameAction(2, new DistributorId(DistributorType.RETAILER, 6), new DistributorId(DistributorType.WHOLESALER, 3), 8, GameAction.GameActionType.ORDERS),
                new GameAction(2, new DistributorId(DistributorType.WAREHOUSE, 2), new DistributorId(DistributorType.WHOLESALER, 3), 10, GameAction.GameActionType.GOODS)
        );

        scoreSheet.nextRound(gameActions);
//        System.out.println(scoreSheet);

        scoreSheet.nextRound(gameActions);
        System.out.println(scoreSheet);

        Set<DistributorId> ts = new TreeSet<>();
        ts.add(distributor.getId());
        ts.addAll(distributor.getConsumers());
        ts.addAll(distributor.getProviders());
//        System.out.println(ts);
    }
}
