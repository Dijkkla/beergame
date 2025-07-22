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
        DistributorId wholesaler3 = new DistributorId(DistributorType.WHOLESALER, 3);
        DistributorId retailer5 = new DistributorId(DistributorType.RETAILER, 5);
        DistributorId retailer6 = new DistributorId(DistributorType.RETAILER, 6);
        DistributorId warehouse2 = new DistributorId(DistributorType.WAREHOUSE, 2);

        Distributor distributor = new Distributor(
                wholesaler3,
                Set.of(retailer5, retailer6),
                Set.of(warehouse2)
        );

//        System.out.println(distributor);


        ScoreSheet scoreSheet = new ScoreSheet(distributor, 12, 4, 4);

//        System.out.println(scoreSheet);

        Set<GameAction> gameActions = Set.of(
                new GameAction(1, retailer5, wholesaler3, 5, GameAction.Type.ORDERS),
                new GameAction(1, retailer6, wholesaler3, 7, GameAction.Type.ORDERS),
                new GameAction(1, warehouse2, wholesaler3, 9, GameAction.Type.GOODS),
                new GameAction(2, retailer5, wholesaler3, 6, GameAction.Type.ORDERS),
                new GameAction(2, retailer6, wholesaler3, 8, GameAction.Type.ORDERS),
                new GameAction(2, warehouse2, wholesaler3, 10, GameAction.Type.GOODS)
        );

        scoreSheet.nextRound(gameActions);
//        System.out.println(scoreSheet);

        scoreSheet.nextRound(gameActions);
        System.out.println(scoreSheet);

        System.out.println(scoreSheet.get(3, ScoreSheet.Column.INCOMING_ORDERS));
        System.out.println(scoreSheet.get(3, ScoreSheet.Column.INCOMING_ORDERS, warehouse2));

        Set<DistributorId> ts = new TreeSet<>();
        ts.add(distributor.self());
        ts.addAll(distributor.consumers());
        ts.addAll(distributor.providers());
//        System.out.println(ts);
    }
}
