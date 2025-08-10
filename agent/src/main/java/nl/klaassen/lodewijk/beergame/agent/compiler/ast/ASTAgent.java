package nl.klaassen.lodewijk.beergame.agent.compiler.ast;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class ASTAgent extends ASTNode {
    public List<ASTValueAssignment> valueAssignments = new ArrayList<>();

    @Override
    public List<ASTNode> getChildren() {
        return new ArrayList<>(valueAssignments);
    }

    @Override
    public ASTNode addChild(ASTNode child) {
        if (child instanceof ASTValueAssignment valueAssignment) {
            valueAssignments.add(valueAssignment);
        }
        return this;
    }

    @Override
    public String getNodeLabel() {
        return "Agent";
    }
}
