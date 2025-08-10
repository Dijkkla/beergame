package nl.klaassen.lodewijk.beergame.agent.compiler;

public record SemanticError(String description) {
    public String toString() {
        return "ERROR: " + description;
    }
}
