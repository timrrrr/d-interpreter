package com.projectd.interpreter.syntax;

import com.projectd.interpreter.lex.token.*;
import com.projectd.interpreter.syntax.exception.SyntaxAnalyzerParseException;
import com.projectd.interpreter.syntax.tree.AstGrammarNode;
import com.projectd.interpreter.syntax.tree.AstGrammarNodeType;
import com.projectd.interpreter.syntax.tree.AstNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SyntaxAnalyserImpl implements SyntaxAnalyser {

    private final Iterator<LexToken> iterator;

    public SyntaxAnalyserImpl(List<LexToken> tokens) {
        this.iterator = tokens.iterator();
    }

    @Override
    public AstNode buildAstTree() {
        AstNode root = new AstGrammarNode(AstGrammarNodeType.PROGRAM, null);

        while (iterator.hasNext()) {
            LexToken nextToken = iterator.next();

            switch (nextToken.getCode()) {
                case RETURN -> {
                    incrementCurrentPositionOrError(String.format("Couldn't parse expected symbol %s, unexpected end of lex token list.",
                            LexTokenCode.OPEN_SQUARE_BRACKET));
                    AstNode returnStatement = new AstNode(nextToken, null);

                    nextToken = tokens.get(currentPosition);
                    if(nextToken.getCode() != LexTokenCode.OPEN_SQUARE_BRACKET) {
                        throw new SyntaxAnalyzerParseException(String.format("Couldn't parse symbol, expected %s, but got %s",
                                LexTokenCode.OPEN_SQUARE_BRACKET, nextToken.getCode()));
                    }

                    incrementCurrentPositionOrError("Couldn't parse expression, unexpected end of lex token list.");
                    AstNode expression = parseExpression(returnStatement);
                    incrementCurrentPositionOrError(String.format("Couldn't parse expected symbol %s, unexpected end of lex token list.",
                            LexTokenCode.CLOSE_SQUARE_BRACKET));

                    nextToken = tokens.get(currentPosition);
                    if(nextToken.getCode() != LexTokenCode.CLOSE_SQUARE_BRACKET) {
                        throw new SyntaxAnalyzerParseException(String.format("Couldn't parse symbol, expected %s, but got %s",
                                LexTokenCode.CLOSE_SQUARE_BRACKET, nextToken.getCode()));
                    }

                    returnStatement.addChild(expression);
                    resultingTree.getRoot().addChild(returnStatement);
                }

                case PRINT -> {
                    // TODO: multiple
                    AstNode printStatement = new AstNode(nextToken, resultingTree.getRoot());
                    incrementCurrentPositionOrError("Couldn't parse expression, unexpected end of lex token list.");
                    AstNode expression = parseExpression(printStatement);

                    printStatement.addChild(expression);
                    resultingTree.getRoot().addChild(printStatement);
                }

            }
        }

        return root;
    }

    private AstNode parseExpression(AstNode parent) {
        AstNode firstTermTemp = parseTerm(null);

        if(!possibleIncrementCurrentPosition()) return new AstNode(firstTermTemp.getData(), parent);
        incrementCurrentPosition();

        LexToken nextToken = tokens.get(currentPosition);

        AstNode currentExpressionTemp = null;
        switch (nextToken.getCode()) {
            case ADDITION, SUBTRACTION -> {
                AstNode op = new AstNode(nextToken, null);
                AstNode firstTerm = new AstNode(firstTermTemp.getData(), op);
                op.addChild(firstTerm);
                incrementCurrentPositionOrError("Couldn't parse expression, unexpected end of token list. But expected term.");
                AstNode secondTerm = parseTerm(op);
                op.addChild(secondTerm);
                currentExpressionTemp = op;
            }

            default -> {
                decrementCurrentPosition();
                return new AstNode(firstTermTemp.getData(), parent);
            }
        }

        while (true) {
            if(!possibleIncrementCurrentPosition()) {
                AstNode result = new AstNode(currentExpressionTemp.getData(), parent);
                result.addChildren(currentExpressionTemp.getChildren().stream().map(x -> new AstNode(x.getData(), result, x.getChildren())).toList());
                return result;
            }
            incrementCurrentPosition();

            LexToken next = tokens.get(currentPosition);
            switch (next.getCode()) {
                case ADDITION, SUBTRACTION -> {
                    AstNode op = new AstNode(next, currentExpressionTemp);
                    op.addChild(currentExpressionTemp);
                    incrementCurrentPositionOrError("Couldn't parse expression, unexpected end of token list. But expected term.");
                    AstNode secondTerm = parseTerm(op);
                    op.addChild(secondTerm);
                    currentExpressionTemp = op;
                }

                default -> {
                    decrementCurrentPosition();
                    AstNode result = new AstNode(currentExpressionTemp.getData(), parent);
                    result.addChildren(currentExpressionTemp.getChildren().stream().map(x -> new AstNode(x.getData(), result, x.getChildren())).toList());
                    return result;
                }
            }
        }
    }

    private AstNode parseTerm(AstNode parent) {
        AstNode factor = parseFactor(parent);

        if(!possibleIncrementCurrentPosition()) return factor;
        incrementCurrentPosition();

        LexToken currentToken = tokens.get(currentPosition);
        switch (currentToken.getCode()) {
            case MULTIPLICATION, DIVISION -> {
                AstNode resOp = new AstNode(currentToken, parent);
                AstNode newFactor = new AstNode(factor.getData(), resOp);
                resOp.addChild(newFactor);
                AstNode term = parseTerm(resOp);
                resOp.addChild(term);
                return resOp;
            }

            default -> {
                decrementCurrentPosition();
                return factor;
            }
        }
    }

    private AstNode parseFactor(AstNode parent) {
        LexToken currentToken = tokens.get(currentPosition);
        switch (currentToken.getCode()) {
            case IDENTIFIER -> {
                if (currentToken instanceof LexIdentifierToken) {
                    return new AstNode(currentToken, parent);
                } else {
                    throw new IllegalStateException(String.format("Unexpected instance of class for %s lex currentToken", LexTokenCode.IDENTIFIER));
                }
            }

            case LITERAL -> {
                if (currentToken instanceof LexLiteralToken) {
                    return new AstNode(currentToken, parent);
                } else {
                    throw new IllegalStateException(String.format("Unexpected instance of class for %s lex currentToken", LexTokenCode.LITERAL));
                }
            }

            case OPEN_ROUND_BRACKET -> {
                incrementCurrentPositionOrError("Couldn't parse expression. Unexpected end of lex list.");
                AstNode expression = parseExpression(parent);
                incrementCurrentPositionOrError("Couldn't parse expression factor, unexpected end of currentToken list.");
                LexToken nextToken = tokens.get(currentPosition);
                if(nextToken.getCode() == LexTokenCode.CLOSED_ROUND_BRACKET) {
                    return expression;
                } else {
                    throw new SyntaxAnalyzerParseException(String.format("Couldn't parse expression, expected currentToken %s, but got %s",
                            LexTokenCode.CLOSED_ROUND_BRACKET, nextToken.getCode()));
                }
            }

            case SUBTRACTION, ADDITION -> {
                AstNode addOp = new AstNode(currentToken, parent);
                AstNode factor = parseFactor(addOp);
                addOp.addChild(factor);
                return addOp;
            }

            default ->
                throw new SyntaxAnalyzerParseException(String.format("Expected factor, but got %s", currentToken));
        }
    }
}
