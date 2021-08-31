package com.github.pareronia.facebookhc._2021.qual.a2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.github.pareronia.facebookhc.TestBase;

class ConsistencyChapter2TestCase extends TestBase<ConsistencyChapter2> {

    protected ConsistencyChapter2TestCase() {
        super(ConsistencyChapter2.class);
    }

    @Override
    protected FileTestCase readTestCase(
            final File file,
            final BufferedReader in,
            final BufferedReader out,
            final Integer i
            ) {
        try {
            final StringBuilder sb = new StringBuilder();
            sb.append("1").append(System.lineSeparator());
            final String first = in.readLine();
            sb.append(first).append(System.lineSeparator());
            final String second = in.readLine();
            sb.append(second).append(System.lineSeparator());
            final int lines = Integer.parseInt(second);
            for (int j = 0; j < lines; j++) {
                sb.append(in.readLine()).append(System.lineSeparator());
            }
            final String input = sb.toString();
            final List<String> expectedOutput =
                    List.of(out.readLine().replaceAll("Case #[0-9]+", "Case #1"));
            return new FileTestCase(file.getName(), i, input, expectedOutput);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
