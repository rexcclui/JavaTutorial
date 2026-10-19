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

    /** Prints one line of demo output. run.sh shows it under the test, so you can watch what the code does. */
    public static void show(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    /** Runs an action and prints whether it worked or which exception it threw. Handy to "see" what is allowed. */
    public static void attempt(String label, Runnable action) {
        try {
            action.run();
            show("  %-36s -> OK", label);
        } catch (RuntimeException e) {
            show("  %-36s -> throws %s", label, e.getClass().getSimpleName());
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
