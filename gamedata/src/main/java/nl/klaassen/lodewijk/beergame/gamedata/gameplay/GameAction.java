package nl.klaassen.lodewijk.beergame.gamedata.gameplay;

import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;

public record GameAction(int round, DistributorId from, DistributorId to, int amount, GameActionType type) {
    private enum GameActionType {
        ORDER,
        DELIVERY
    }
}
