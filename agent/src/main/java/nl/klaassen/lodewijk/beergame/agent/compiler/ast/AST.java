package nl.klaassen.lodewijk.beergame.agent.compiler.ast;

import nl.klaassen.lodewijk.beergame.agent.compiler.SemanticError;

import java.util.ArrayList;
import java.util.List;

public class AST {
    public ASTAgent root;

    @Override
    public String toString() {
        return root.toString();
    }

    public List<SemanticError> getErrors() {
        List<SemanticError> errors = new ArrayList<>();
        collectErrors(errors, root);
        return errors;
    }

    private void collectErrors(List<SemanticError> errors, ASTNode node) {
        if (node.hasError()) {
            errors.addAll(node.getErrors());
        }
        for (ASTNode child : node.getChildren()) {
            collectErrors(errors, child);
        }
    }
}
