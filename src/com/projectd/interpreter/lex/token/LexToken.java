package com.projectd.interpreter.lex.token;

public class LexToken {
    protected final LexTokenSpan span;
    protected final LexTokenCode code;


    protected LexToken(LexTokenSpan span, LexTokenCode code) {
        this.span = span;
        this.code = code;
    }
}
