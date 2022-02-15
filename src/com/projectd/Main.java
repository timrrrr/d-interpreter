package com.projectd;

import com.projectd.interpreter.DInterpreter;
import com.projectd.interpreter.DInterpreterImpl;
import com.projectd.interpreter.lex.token.LexLiteralToken;
import com.projectd.interpreter.lex.token.LexToken;
import com.projectd.interpreter.lex.token.LexTokenCode;
import com.projectd.interpreter.lex.token.LexTokenSpan;
import com.projectd.interpreter.syntax.SyntaxAnalyserImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final DInterpreter interpreter = new DInterpreterImpl();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Specify exactly one file of source code");
        }

        String sourcePath = args[0];
        List<String> sourceCodeLines;
        try {
            Path path = Paths.get(sourcePath);
            sourceCodeLines = Files.readAllLines(path);
        } catch (IOException e) {
            System.err.println(e.toString());
            return;
        }

        interpreter.interpretFromSource(sourceCodeLines);

//        LexTokenSpan span = LexTokenSpan.of(0, 0);
//
//        List<LexToken> lex = new ArrayList<LexToken>();
//        lex.add(LexLiteralToken.ofValue(1, span));
//        lex.add(new LexToken(span, LexTokenCode.ADDITION));
//        lex.add(new LexToken(span, LexTokenCode.OPEN_ROUND_BRACKET));
//        lex.add(LexLiteralToken.ofValue(10, span));
//        lex.add(new LexToken(span, LexTokenCode.SUBTRACTION));
//        lex.add(LexLiteralToken.ofValue(5, span));
//        lex.add(new LexToken(span, LexTokenCode.CLOSED_ROUND_BRACKET));
//
//        SyntaxAnalyserImpl s = new SyntaxAnalyserImpl(lex);
//        s.buildAstTree();
    }
}
