package com.github.pareronia.facebookhc._2021.qual.c1;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

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
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 * C1: Gold Mine - Chapter 1
 * @see <a href="https://www.facebook.com/codingcompetitions/hacker-cup/2021/qualification-round/problems/C1">https://www.facebook.com/codingcompetitions/hacker-cup/2021/qualification-round/problems/C1</a>
 */
public class GoldMineChapter1 {

    private final InputStream in;
    private final PrintStream out;
    
    public GoldMineChapter1(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
        final int n = sc.nextInt();
        final Vertex[] g = new Vertex[n + 1];
        for (int j = 1; j <= n; j++) {
            final Vertex v = new Vertex();
            v.name = j;
            v.value = sc.nextInt();
            g[j] = v;
        }
        for (int j = 0; j < n - 1; j++) {
            final int a = sc.nextInt();
            final int b = sc.nextInt();
            g[a].neighbours.add(g[b]);
            g[b].neighbours.add(g[a]);
        }
        root(g, 1, 1);
        final List<List<Vertex>> pathsToRoot = pathsToRoot(g);
        int ans = -1;
        for (int j = 0; j < pathsToRoot.size(); j++) {
            final List<Vertex> path1 = pathsToRoot.get(j);
            final int value1 = g[1].value + value(path1);
            ans = Math.max(ans, value1);
            for (int k = 0; k < pathsToRoot.size(); k++) {
                if (j == k) {
                    continue;
                }
                final List<Vertex> path2 = pathsToRoot.get(k);
                if (!disjoint(path1, path2)) {
                    continue;
                }
                final int gold = value1 + value(path2);
                ans = Math.max(ans, gold);
            }
        }
        new Output(i, this.out).print(ans).println();
    }
    
    private int value(final List<Vertex> path) {
        int ans = 0;
        for (final Vertex vertex : path) {
            if (vertex.name != 1) {
                ans += vertex.value;
            }
        }
        return ans;
    }
    
    private void root(final Vertex[] g, final int v, final int parent) {
        if (g[v].neighbours.equals(List.of(g[parent]))) {
            g[v].neighbours.remove(g[parent]);
            g[v].parent = g[parent];
        } else {
            if (v != parent) {
                g[v].neighbours.remove(g[parent]);
                g[v].parent = g[parent];
            }
            for (final Vertex n : g[v].neighbours) {
                root(g, n.name, g[v].name);
            }
        }
    }
    
    private List<Vertex> leaves(final Vertex[] g) {
        final List<Vertex> ans = new ArrayList<>();
        for (int j = 1; j < g.length; j++) {
            if (g[j].neighbours.isEmpty()) {
                ans.add(g[j]);
            }
        }
        return ans;
    }
    
    private List<List<Vertex>> pathsToRoot(final Vertex[] g) {
        final List<List<Vertex>> ans = new ArrayList<>();
        for (final Vertex leaf : leaves(g)) {
            final List<Vertex> path = new ArrayList<>();
            path.add(leaf);
            Vertex v = leaf;
            while (v.parent != null) {
                path.add(v.parent);
                v = v.parent;
            }
            ans.add(path);
        }
        return ans;
    }
    
    private boolean disjoint(final List<Vertex> path1, final List<Vertex> path2) {
        for (int j = 0; j < path1.size() - 1; j++) {
            if (path2.contains(path1.get(j))) {
                return false;
            }
        }
        return true;
    }
    
    private class Vertex {
        private int name;
        private int value;
        private final List<Vertex> neighbours = new ArrayList<>();
        private Vertex parent;
        
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Vertex [name=").append(name).append(", value=").append(value).append(", neighbours=")
                    .append(neighbours.stream().map(v -> v.name).collect(toList()))
                    .append(", parent=").append(Optional.ofNullable(parent).map(v -> v.name).orElse(null)).append("]");
            return builder.toString();
        }
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
            is = GoldMineChapter1.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new GoldMineChapter1(sample, is, out).solve();
        
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
                    = Paths.get(GoldMineChapter1.class.getResource("sample.out").toURI());
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
        
        @SuppressWarnings("unused")
        public int[] nextIntArray(final int n) {
            final int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = nextInt();
            }
            return a;
        }
        
        @SuppressWarnings("unused")
        public long nextLong() {
            return Long.parseLong(next());
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
