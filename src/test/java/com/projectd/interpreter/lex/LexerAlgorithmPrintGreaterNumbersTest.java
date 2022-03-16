package com.projectd.interpreter.lex;

import com.projectd.interpreter.lex.token.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class LexerAlgorithmPrintGreaterNumbersTest {

    @Test
    public void testAlgorithm() {
        // Given
        String input = """
               var arr := [1, 2, 3]

                for i in 0 .. 3 loop
                	if arr[i] >= 1 then
                		print arr[i]
                	end
                end
                """;

        LexicalAnalyser lexer = new LexicalAnalyserImpl();

        // When
        List<LexToken> result = lexer.analyse(Arrays.stream(input.split("\\r?\\n")).toList());
        result.forEach(System.out::println);

        // Then
        List<LexToken> expectedTokens = List.of(
                new LexToken(LexTokenSpan.of(0, 0), LexTokenCode.VAR),
                new LexIdentifierToken("arr", LexTokenSpan.of(0, 4)),
                new LexToken(LexTokenSpan.of(0, 8), LexTokenCode.ASSIGNMENT),
                new LexToken(LexTokenSpan.of(0, 11), LexTokenCode.OPEN_SQUARE_BRACKET),
                LexLiteralToken.ofValue(1, LexTokenSpan.of(0, 12)),
                new LexToken(LexTokenSpan.of(0, 13), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(2, LexTokenSpan.of(0, 15)),
                new LexToken(LexTokenSpan.of(0, 16), LexTokenCode.COMMA),
                LexLiteralToken.ofValue(3, LexTokenSpan.of(0, 18)),
                new LexToken(LexTokenSpan.of(0, 19), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(2, 1), LexTokenCode.FOR),
                new LexIdentifierToken("i", LexTokenSpan.of(2, 5)),
                new LexToken(LexTokenSpan.of(2, 7), LexTokenCode.IN),
                LexLiteralToken.ofValue(0, LexTokenSpan.of(2, 10)),
                new LexToken(LexTokenSpan.of(2, 12), LexTokenCode.DOT),
                new LexToken(LexTokenSpan.of(2, 13), LexTokenCode.DOT),
                LexLiteralToken.ofValue(3, LexTokenSpan.of(2, 15)),
                new LexToken(LexTokenSpan.of(2, 17), LexTokenCode.LOOP),
                new LexToken(LexTokenSpan.of(3, 2), LexTokenCode.IF),
                new LexIdentifierToken("arr", LexTokenSpan.of(3, 5)),
                new LexToken(LexTokenSpan.of(3, 8), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexIdentifierToken("i", LexTokenSpan.of(3, 9)),
                new LexToken(LexTokenSpan.of(3, 10), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(3, 12), LexTokenCode.MORE_OR_EQUAL),
                LexLiteralToken.ofValue(1, LexTokenSpan.of(3, 15)),
                new LexToken(LexTokenSpan.of(3, 17), LexTokenCode.THEN),
                new LexToken(LexTokenSpan.of(4, 3), LexTokenCode.PRINT),
                new LexIdentifierToken("arr", LexTokenSpan.of(4, 9)),
                new LexToken(LexTokenSpan.of(4, 12), LexTokenCode.OPEN_SQUARE_BRACKET),
                new LexIdentifierToken("i", LexTokenSpan.of(4, 13)),
                new LexToken(LexTokenSpan.of(4, 14), LexTokenCode.CLOSE_SQUARE_BRACKET),
                new LexToken(LexTokenSpan.of(5, 2), LexTokenCode.END),
                new LexToken(LexTokenSpan.of(6, 1), LexTokenCode.END)
                );

        assert (result.equals(expectedTokens));
    }

}
