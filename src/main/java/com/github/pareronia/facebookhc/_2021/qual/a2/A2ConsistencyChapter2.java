package com.github.pareronia.facebookhc._2021.qual.a2;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * A2: Consistency - Chapter 2
 * @see <a href="https://www.facebook.com/codingcompetitions/hacker-cup/2021/qualification-round/problems/A2">https://www.facebook.com/codingcompetitions/hacker-cup/2021/qualification-round/problems/A2</a>
 */
public class A2ConsistencyChapter2 {

    private final InputStream in;
    private final PrintStream out;
    
    public A2ConsistencyChapter2(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
        final String s = sc.next();
        final int k = sc.nextInt();
        final Map<Character, List<Character>> nbs = new HashMap<>();
        for (int j = 0; j < k; j++) {
            final char[] r = sc.next().toCharArray();
            nbs.merge(r[0], new ArrayList<>(List.of(r[1])), (l1, l2) -> {
                l1.addAll(l2);
                return l1;
            });
        }
        PATHS.clear();
        int ans = Integer.MAX_VALUE;
        outer:
        for (char c = 'A'; c <= 'Z'; c++) {
            int cost = 0;
            for (int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == c) {
                    continue;
                }
                final List<Character> path = findPath(s.charAt(j), c, nbs);
                if (!path.isEmpty()) {
                    cost += (path.size() - 1);
                } else {
                    continue outer;
                }
            }
            ans = Math.min(ans, cost);
        }
        if (ans == Integer.MAX_VALUE) {
            ans = -1;
        }
        new Output(i, this.out).print(ans).println();
    }
    
    private static final Map<String, List<Character>> PATHS = new HashMap<>();
    
    private List<Character> findPath(final char start, final char dest,
            final Map<Character, List<Character>> nbs) {
        
        final String key = String.valueOf(new char[] { start, dest });
        if (PATHS.containsKey(key)) {
            return PATHS.get(key);
        }
        final PriorityQueue<List<Character>> q
                = new PriorityQueue<>((p1, p2) ->
                        Integer.compare(p1.size(), p2.size()));
        q.add(new ArrayList<>(List.of(start)));
        final Set<Character> seen = new HashSet<>();
        while (!q.isEmpty()) {
            final List<Character> last = q.poll();
            final Character lastChar = last.get(last.size() - 1);
            if (lastChar == dest) {
                PATHS.put(key, last);
                return last;
            }
            if (!nbs.containsKey(lastChar)) {
                continue;
            }
            for (final Character n : nbs.get(lastChar)) {
                if (seen.contains(n)) {
                    continue;
                }
                final List<Character> newPath = new ArrayList<>(last);
                newPath.add(n);
                q.add(newPath);
                seen.add(n);
            }
        }
        PATHS.put(key, emptyList());
        return emptyList();
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
            is = A2ConsistencyChapter2.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new A2ConsistencyChapter2(sample, is, out).solve();
        
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
                    = Paths.get(A2ConsistencyChapter2.class.getResource("sample.out").toURI());
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
