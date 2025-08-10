package nl.klaassen.lodewijk.beergame.agent.compiler.ast;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract class ASTValue extends ASTNode {
    private ReturnType returnType;

    private enum ReturnType {
        NUMBER, BOOLEAN
    }
}
