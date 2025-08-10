package nl.klaassen.lodewijk.beergame.agent.compiler.parser;

import nl.klaassen.lodewijk.beergame.agent.compiler.ast.ASTNode;
import nl.klaassen.lodewijk.beergame.agent.grammar.AGENT_GRAMMARBaseListener;

import java.util.Stack;

public class AgentCompilerListener extends AGENT_GRAMMARBaseListener {
    private final Stack<ASTNode> ASTStack = new Stack<>();
}
