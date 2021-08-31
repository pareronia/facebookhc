package com.github.pareronia.facebookhc._2021.qual.b;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Supplier;

/**
 * B: Xs and Os
 * @see <a href="https://www.facebook.com/codingcompetitions/hacker-cup/2021/qualification-round/problems/B">https://www.facebook.com/codingcompetitions/hacker-cup/2021/qualification-round/problems/B</a>
 */
public class XsAndOs {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public XsAndOs(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.sample = sample;
        this.in = in;
        this.out = out;
    }
    
    @SuppressWarnings("unused")
    private void log(final Supplier<Object> supplier) {
        if (!sample) {
            return;
        }
        System.out.println(supplier.get());
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
        final int n = sc.nextInt();
        final char[][] g = new char[n][n];
        for (int j = 0; j < n; j++) {
            g[j] = sc.next().toCharArray();
        }
        int min = Integer.MAX_VALUE;
        final Set<Set<Pair<Integer, Integer>>> sets = new HashSet<>();
        for (int j = 0; j < n; j++) {
            for (final Set<Pair<Integer, Integer>> x : List.of(countRow(g, j), countCol(g, j))) {
                if (x.isEmpty()) {
                    continue;
                }
                if (x.size() < min) {
                    min = x.size();
                    sets.clear();
                    sets.add(x);
                } else if (x.size() == min) {
                    sets.add(x);
                }
            }
        }
        if (sets.size() == 0) {
            new Output(i, this.out).print("Impossible").println();
        } else {
            new Output(i, this.out).print(min, sets.size()).println();
        }
    }
    
    private Set<Pair<Integer, Integer>> countRow(final char[][] g, final int row) {
        final Set<Pair<Integer, Integer>> ans = new HashSet<>();
        for (int j = 0; j < g.length; j++) {
            if (g[row][j] == 'O') {
                return Collections.emptySet();
            } else if (g[row][j] == '.') {
                ans.add(Pair.of(row, j));
            }
        }
        return ans;
    }
    
    private Set<Pair<Integer, Integer>> countCol(final char[][] g, final int col) {
        final Set<Pair<Integer, Integer>> ans = new HashSet<>();
        for (int j = 0; j < g.length; j++) {
            if (g[j][col] == 'O') {
                return Collections.emptySet();
            } else if (g[j][col] == '.') {
                ans.add(Pair.of(j, col));
            }
        }
        return ans;
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
            is = XsAndOs.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new XsAndOs(sample, is, out).solve();
        
        out.flush();
        if (sample) {
            final long timeSpent = (System.nanoTime() - timerStart) / 1_000;
            final double time;
            final String unit;
            if (timeSpent < 1_000) {
                time = timeSpent;
                unit = "µs";
            } else if (timeSpent < 1_000_000) {
                time = timeSpent / 1_000.0;
                unit = "ms";
            } else {
                time = timeSpent / 1_000_000.0;
                unit = "s";
            }
            final Path path
                    = Paths.get(XsAndOs.class.getResource("sample.out").toURI());
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

        public Output print(final Object... a) {
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

    private static final class Pair<L, R> {
        private final L one;
        private final R two;

        private Pair(final L one, final R two) {
            this.one = one;
            this.two = two;
        }

        public static <L, R> Pair<L, R> of(final L one, final R two) {
            return new Pair<>(one, two);
        }

        @Override
        public int hashCode() {
            return Objects.hash(one, two);
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Pair<L, R> other = (Pair<L, R>) obj;
            return Objects.equals(one, other.one) && Objects.equals(two, other.two);
        }
    }
}
