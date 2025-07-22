package nl.klaassen.lodewijk.beergame.gamedata.gameplay;

import nl.klaassen.lodewijk.beergame.gamedata.identifiers.DistributorId;

public record GameAction(int round, DistributorId from, DistributorId to, int amount, Type type) {
    public enum Type {
        ORDERS,
        GOODS
    }
}
