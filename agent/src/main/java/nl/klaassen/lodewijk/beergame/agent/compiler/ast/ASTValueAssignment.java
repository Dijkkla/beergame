package nl.klaassen.lodewijk.beergame.agent.compiler.ast;

import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class ASTValueAssignment extends ASTNode {
    @Override
    public ASTNode addChild(ASTNode child) {
        return this;
    }

    @Override
    public List<ASTNode> getChildren() {
        return List.of();
    }

    @Override
    public String getNodeLabel() {
        return "ValueAssignment";
    }
}
