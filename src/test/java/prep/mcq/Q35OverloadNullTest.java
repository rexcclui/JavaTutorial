package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * QUESTION 35 - overload resolution with null
 *
 *     static String f(Object o) { return "Object"; }
 *     static String f(String s) { return "String"; }
 *
 *     System.out.println(f(null));
 *
 * What is printed?
 *
 *   A. Object
 *   B. String
 *   C. Compile error: the call is ambiguous.
 *   D. NullPointerException
 *
 * ANSWER: B
 *
 * WHY:
 *   null fits both parameter types, so Java picks the MOST SPECIFIC applicable method. String is a subtype of Object, so f(String) is more specific.
 *   C. Ambiguity (a compile error) only happens when neither candidate is more specific, for example f(String) and f(Integer) with a null argument.
 *   D. Nothing is dereferenced, so no NullPointerException.
 *   The overload is chosen at COMPILE time from the static type of the argument; to force the other one write f((Object) null).
 *
 * See it run:  ./run.sh mcq Q35
 */
class Q35OverloadNullTest {
    static String f(Object o) { return "Object"; }
    static String f(String s) { return "String"; }

    // PURPOSE: Demo - which overload does each call pick?
    @Test
    void demo_whichOverloadIsChosen() {
        Object asObject = "hello";
        McqSupport.show("f(null)               -> %s   (most specific applicable method)", f(null));
        McqSupport.show("f((Object) null)      -> %s", f((Object) null));
        McqSupport.show("f(\"hello\")            -> %s", f("hello"));
        McqSupport.show("Object o = \"hello\"; f(o) -> %s   (chosen by the DECLARED type Object, not the runtime type String)", f(asObject));
        assertThat(f(null)).isEqualTo("String");
        assertThat(f((Object) null)).isEqualTo("Object");
        assertThat(f(asObject)).isEqualTo("Object");
    }

    // PURPOSE: Option C is WRONG here - but it IS the answer when the two candidates are unrelated (String and Integer): the real compiler says "ambiguous".
    @Test
    void optionC_wrong_butAmbiguousWhenUnrelated() {
        String source = """
                public class Snippet {
                    static String f(String s) { return "String"; }
                    static String f(Integer i) { return "Integer"; }
                    static String run() { return f(null); }
                }
                """;
        var errors = McqSupport.compileErrors("Snippet", source);
        McqSupport.show("f(String) / f(Integer) with f(null) -> %s", errors);
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).contains("ambiguous");
    }

    // PURPOSE: Option D is WRONG - passing null is fine; nothing is dereferenced.
    @Test
    void optionD_wrong_noNullPointerException() {
        McqSupport.attempt("f(null)", () -> f(null));
    }

    // PURPOSE: Related puzzle - primitive widening beats boxing: f(5) with f(long) and f(Integer) picks f(long).
    @Test
    void related_wideningBeatsBoxing() {
        assertThat(g(5)).isEqualTo("long");
        assertThat(g(Integer.valueOf(5))).isEqualTo("Integer");
        McqSupport.show("g(5) -> %s   g(Integer.valueOf(5)) -> %s   (phase 1 allows widening but not boxing)", g(5), g(Integer.valueOf(5)));
    }

    static String g(long x) { return "long"; }
    static String g(Integer x) { return "Integer"; }
}
