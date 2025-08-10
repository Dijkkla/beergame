package nl.klaassen.lodewijk.beergame.agent.compiler.ast;

import lombok.EqualsAndHashCode;
import nl.klaassen.lodewijk.beergame.agent.compiler.SemanticError;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
public abstract class ASTNode {
    private final List<SemanticError> errors = new ArrayList<>();

    public abstract ASTNode addChild(ASTNode child);

    public abstract List<ASTNode> getChildren();

    public abstract String getNodeLabel();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringHelper(sb, "");
        return sb.toString();
    }

    private void toStringHelper(StringBuilder sb, String indent) {
        sb.append(indent).append(getNodeLabel()).append("\n");
        getChildren().forEach(child -> child.toStringHelper(sb, indent + "\t"));
    }

    public void addError(String description) {
        SemanticError error = new SemanticError(description);
        if (!errors.contains(error)) {
            errors.add(error);
        }
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public List<SemanticError> getErrors() {
        return errors;
    }
}
