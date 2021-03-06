package com.github.pareronia.facebookhc._2021.qual.a1;

import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * A1: Consistency - Chapter 1
 * @see <a href="https://www.facebook.com/codingcompetitions/hacker-cup/2021/qualification-round/problems/A1">https://www.facebook.com/codingcompetitions/hacker-cup/2021/qualification-round/problems/A1</a>
 */
public class ConsistencyChapter1 {

    private final InputStream in;
    private final PrintStream out;
    
    public ConsistencyChapter1(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private static final Set<Character> VOWELS = Set.of('A', 'E', 'I', 'O', 'U');
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
        final String s = sc.next();
        int ans = Integer.MAX_VALUE;
        for (char c = 'A'; c <= 'Z'; c++) {
            int cost = 0;
            for (int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == c) {
                    continue;
                }
                if (isVowel(c) && isVowel(s.charAt(j))) {
                    cost += 2;
                } else if (isConsonant(c) && isConsonant(s.charAt(j))) {
                    cost += 2;
                } else {
                    cost += 1;
                }
            }
            ans = Math.min(ans, cost);
        }
        new Output(i, this.out).print(ans).println();
    }
    
    private boolean isVowel(final char c) {
        return VOWELS.contains(c);
    }

    private boolean isConsonant(final char c) {
        return !VOWELS.contains(c);
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases = sc.nextInt();
            for (int i = 1; i <= numberOfTestCases; i++) {
                handleTestCase(i, sc);
            }
        }
    }

    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final InputStream is;
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            is = ConsistencyChapter1.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new ConsistencyChapter1(sample, is, out).solve();
        
        out.flush();
        if (sample) {
            final long timeSpent = (System.nanoTime() - timerStart) / 1_000;
            final double time;
            final String unit;
            if (timeSpent < 1_000) {
                time = timeSpent;
                unit = "??s";
            } else if (timeSpent < 1_000_000) {
                time = timeSpent / 1_000.0;
                unit = "ms";
            } else {
                time = timeSpent / 1_000_000.0;
                unit = "s";
            }
            final Path path
                    = Paths.get(ConsistencyChapter1.class.getResource("sample.out").toURI());
            final List<String> expected = Files.readAllLines(path);
            final List<String> actual = asList(baos.toString().split("\\r?\\n"));
            if (!expected.equals(actual)) {
                throw new AssertionError(String.format(
                        "Expected %s, got %s", expected, actual));
            }
            actual.forEach(System.out::println);
            System.out.println(String.format("took: %.3f %s", time, unit));
        }
    }
    
    private static boolean isSample() {
        try {
            return "sample".equals(System.getProperty("fbhc"));
        } catch (final SecurityException e) {
            return false;
        }
    }
    
    private static final class FastScanner implements Closeable {
        private final BufferedReader br;
        private StringTokenizer st;
        
        public FastScanner(final InputStream in) {
            this.br = new BufferedReader(new InputStreamReader(in));
            st = new StringTokenizer("");
        }
        
        public String next() {
            while (!st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return st.nextToken();
        }
    
        public int nextInt() {
            return Integer.parseInt(next());
        }
        
        @Override
        public void close() {
            try {
                this.br.close();
            } catch (final IOException e) {
                // ignore
            }
        }
    }
    
    private static final class Output {
        private final int caseNumber;
        private final PrintStream out;
        
        public Output(final int caseNumber, final PrintStream out) {
            this.caseNumber = caseNumber;
            this.out = out;
        }

        public Output print(final int... a) {
            this.out.print("Case #" + this.caseNumber + ": ");
            for (int j = 0; j < a.length; j++) {
                if (j > 0) {
                    this.out.print(" ");
                }
                this.out.print(a[j]);
            }
            return this;
        }
        
        public Output println() {
            this.out.println();
            return this;
        }
    }
}
