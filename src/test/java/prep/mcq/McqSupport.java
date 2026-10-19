package prep.mcq;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

/** Shared helpers for the multiple-choice question tests. */
public final class McqSupport {
    private McqSupport() {}

    /**
     * The "check my answer" step of every question.
     * Skipped until you replace '?' with 'A', 'B', 'C' or 'D'; fails if the answer is wrong.
     */
    public static void check(char mine, char correct) {
        assumeTrue(mine != '?', "Set MY_ANSWER to 'A', 'B', 'C' or 'D' first");
        if (Character.toUpperCase(mine) != correct) {
            // deliberately does not reveal the right answer
            throw new AssertionError("Your answer '" + mine + "' is not correct. "
                    + "Read the experiment tests: each one gathers evidence about one option.");
        }
    }

    /**
     * Compiles a Java source string in memory and returns the compiler's error messages
     * (empty list = it compiles). Lets a test prove "this code does not compile".
     */
    public static List<String> compileErrors(String className, String source) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assumeTrue(compiler != null, "needs a JDK (not just a JRE) to compile code in a test");
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaFileObject file = new SimpleJavaFileObject(
                URI.create("string:///" + className.replace('.', '/') + ".java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };
        try {
            Path out = Files.createTempDirectory("mcq");
            compiler.getTask(null, null, diagnostics, List.of("-proc:none", "-nowarn", "-d", out.toString()), null, List.of(file)).call();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return diagnostics.getDiagnostics().stream()
                .filter(d -> d.getKind() == Diagnostic.Kind.ERROR)
                .map(d -> d.getMessage(null))
                .toList();
    }
}
