package com.github.pareronia.facebookhc;

import static java.lang.Boolean.FALSE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class TestBase<T> {
    
	protected final Class<T> klass;

	protected TestBase(final Class<T> klass) {
        this.klass = klass;
    }

    @TestFactory
    public Stream<DynamicTest> tests() throws URISyntaxException {
        return DynamicTest.stream(
                allFileTestCases(),
                fileTestCaseDisplayNameGenerator(),
                fileTestCaseExecutor());
    }
    
    private Stream<File> allInputFilesStream() throws URISyntaxException {
        final File folder
                = Paths.get(this.klass.getResource(".").toURI()).toFile();
        return Stream.of(folder.listFiles())
                .filter(f -> f.getName().endsWith("_input.txt")
                            && useFile(f.getName()));
    }
    
    private Iterator<FileTestCase> allFileTestCases() throws URISyntaxException {
        return allInputFilesStream()
                .flatMap(this::testCasesInFile)
                .iterator();
    }
    
    private Stream<FileTestCase> testCasesInFile(final File file) {
        try {
            final BufferedReader in = Files.newBufferedReader(file.toPath());
            final BufferedReader out =
                    Files.newBufferedReader(outputForInput(file.toPath()));
            final Integer count = Integer.valueOf(in.readLine());
            return Stream.iterate(1, i -> i <= count, i -> i + 1)
                    .map(i -> readTestCase(file, in, out, i));
        } catch (NumberFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

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
            final String s = first.split(" ")[0];
            final int lines = numberOfLinesPerTestCase()
                    .apply(StringUtils.isNumeric(s) ? Integer.valueOf(s) : -1);
            for (int j = 0; j < lines - 1; j++) {
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
    
    protected Function<Integer, Integer> numberOfLinesPerTestCase() {
        return t -> t + 1;
    }
    
    protected boolean useFile(final String fileName) {
        return true;
    }

    private Function<FileTestCase, String> fileTestCaseDisplayNameGenerator() {
        return testCase -> String.format(
                "%s %03d", testCase.getFileName(), testCase.getNumber());
    }
    
    private ThrowingConsumer<FileTestCase> fileTestCaseExecutor() {
        return testCase -> {
            final InputStream in = setUpInput(testCase.getInput());
            final List<String> result = run(in);
            assertThat(result, is(testCase.getExpectedOutput()));
        };
    }
    
    private Path outputForInput(final Path input) {
        final String outputFileName = StringUtils.substringBefore(
                        input.getFileName().toString(), "input.txt")
                + "output.txt";
        return input.getParent().resolve(outputFileName);
    }

    protected InputStream setUpInput(final String string) {
        return new ByteArrayInputStream(string.getBytes());
    }
    
    protected List<String> run(final InputStream in)
			throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
    	
	    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	invokeSolveMethod(in, baos);
        return List.of(baos.toString().split("\\r?\\n"));
    }

    private void invokeSolveMethod(
            final InputStream in,
            final ByteArrayOutputStream baos)
            throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        
        getSolveMethod().invoke(constructInstance(in, baos));
    }

    private Method getSolveMethod() throws NoSuchMethodException {
        return this.klass.getDeclaredMethod("solve");
    }

    private T constructInstance(
            final InputStream in,
            final ByteArrayOutputStream baos)
            throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        
        final Constructor<T> constructor
    			= this.klass.getDeclaredConstructor(
    			        Boolean.class, InputStream.class, PrintStream.class);
        final PrintStream out = new PrintStream(baos, true);
        return constructor.newInstance(FALSE, in, out);
    }

	@RequiredArgsConstructor
	protected static final class FileTestCase {
	    @Getter
	    private final String fileName;
	    @Getter
	    private final int number;
	    @Getter
	    private final String input;
	    @Getter
	    private final List<String> expectedOutput;
	}
}
