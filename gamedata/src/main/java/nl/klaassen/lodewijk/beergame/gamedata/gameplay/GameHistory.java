package nl.klaassen.lodewijk.beergame.gamedata.gameplay;

import nl.klaassen.lodewijk.beergame.gamedata.Distributor;
import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;

import java.util.*;
import java.util.stream.IntStream;

public class GameHistory {
    private static final int amountOfData = GameAction.Type.values().length;
    private final Map<Set<DistributorId>, List<int[]>> dataMap;
    private int numberOfRounds = 0;

    public GameHistory(Set<Distributor> distributors) {
        Map<Set<DistributorId>, List<int[]>> tmpDataMap = new HashMap<>();
        distributors.forEach(distributor -> {
            DistributorId d = distributor.self();
            distributor.suppliers().forEach(s -> tmpDataMap.computeIfAbsent(Set.of(s, d), t -> new ArrayList<>()));
            distributor.consumers().forEach(c -> tmpDataMap.computeIfAbsent(Set.of(d, c), t -> new ArrayList<>()));
        });
        dataMap = Map.copyOf(tmpDataMap);
    }

    public void putGameAction(GameAction action) {
        if (action.round() > numberOfRounds) {
            throw new IllegalArgumentException("Cannot register action from round " + action.round() + ": The game history currently records up to " + numberOfRounds + " rounds");
        }
        dataMap.get(Set.of(action.from(), action.to())).get(action.round() - 1)[action.type().ordinal()] = action.amount();
    }

    public void incrementRound() {
        dataMap.forEach((k, v) -> v.add(IntStream.generate(() -> Integer.MIN_VALUE).limit(amountOfData).toArray()));
        numberOfRounds++;
    }
}
