package prep.collections;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * LEARNING TEST (no exercise to solve): walks one sentence through every stage of
 *
 *     Map<String, Long> counts = Arrays.stream(text.toLowerCase().split("[^a-z]+"))
 *             .filter(w -> !w.isEmpty())
 *             .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
 *
 * Read the tests top to bottom; each one adds one more stage and asserts what that stage produces.
 * Run it with:  ./run.sh collections WordCountWalkthrough
 * To see the values printed as well:  ./run.sh --verbose collections WordCountWalkthrough
 */
class WordCountWalkthroughTest {
    private static final String TEXT = "!The cat, the HAT; and THE bat.";

    // PURPOSE: Stage 1 - toLowerCase() makes "The", "THE" and "the" the same word.
    @Test
    void stage1_toLowerCase() {
        String lower = TEXT.toLowerCase();
        System.out.println("1. lower   : " + lower);
        assertThat(lower).isEqualTo("!the cat, the hat; and the bat.");
    }

    // PURPOSE: Stage 2 - split("[^a-z]+") cuts the text at every run of non-letters. A leading "!" produces an empty first piece.
    @Test
    void stage2_splitOnNonLetters() {
        String[] pieces = TEXT.toLowerCase().split("[^a-z]+");
        System.out.println("2. split   : " + Arrays.toString(pieces));
        // The text starts with "!", so the first piece is "" (nothing before the first separator).
        // The "." at the very end does NOT add a trailing "": Java's split() drops trailing empty strings.
        assertThat(pieces).containsExactly("", "the", "cat", "the", "hat", "and", "the", "bat");
    }

    // PURPOSE: Stage 2b - why the "+" matters: ", " (comma + space) is ONE separator, not two. Without "+" we would get extra empty strings.
    @Test
    void stage2b_plusMeansOneOrMoreSeparatorCharacters() {
        assertThat("a, b".split("[^a-z]+")).containsExactly("a", "b");   // ", " is one separator
        assertThat("a, b".split("[^a-z]")).containsExactly("a", "", "b"); // "," and " " are two separators -> "" between them
    }

    // PURPOSE: Stage 3 - Arrays.stream() turns the array into a Stream, and filter() removes the empty piece.
    @Test
    void stage3_filterRemovesEmptyStrings() {
        List<String> words = Arrays.stream(TEXT.toLowerCase().split("[^a-z]+"))
                .filter(w -> !w.isEmpty())
                .toList();
        System.out.println("3. words   : " + words);
        assertThat(words).containsExactly("the", "cat", "the", "hat", "and", "the", "bat");
    }

    // PURPOSE: Stage 4a - groupingBy(w -> w) alone puts each word in a bucket: the key is the word, the value is a LIST of every occurrence.
    @Test
    void stage4a_groupingByWithoutCounting() {
        Map<String, List<String>> grouped = Arrays.stream(TEXT.toLowerCase().split("[^a-z]+"))
                .filter(w -> !w.isEmpty())
                .collect(Collectors.groupingBy(w -> w));
        System.out.println("4a. grouped: " + new java.util.TreeMap<>(grouped));
        assertThat(grouped.get("the")).containsExactly("the", "the", "the");
        assertThat(grouped.get("cat")).containsExactly("cat");
        assertThat(grouped).hasSize(5);   // the, cat, hat, and, bat
    }

    // PURPOSE: Stage 4b - adding Collectors.counting() as the second argument replaces each LIST with the number of items in it (a Long).
    @Test
    void stage4b_groupingByWithCounting() {
        Map<String, Long> counts = Arrays.stream(TEXT.toLowerCase().split("[^a-z]+"))
                .filter(w -> !w.isEmpty())
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
        System.out.println("4b. counts : " + new java.util.TreeMap<>(counts));
        assertThat(counts).containsEntry("the", 3L)   // 3L: counting() returns a Long, not an int
                .containsEntry("cat", 1L)
                .containsEntry("hat", 1L)
                .containsEntry("and", 1L)
                .containsEntry("bat", 1L)
                .hasSize(5);
    }

    // PURPOSE: The same result written with a plain loop, so you can see what groupingBy + counting does for you.
    @Test
    void sameResultWithAPlainLoop() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (String w : TEXT.toLowerCase().split("[^a-z]+")) {
            if (w.isEmpty()) continue;                        // the filter step
            counts.merge(w, 1L, Long::sum);                   // groupingBy + counting: add 1 to this word's count
        }
        assertThat(counts).containsEntry("the", 3L).hasSize(5);
    }

    // PURPOSE: Limits of the pattern: digits, apostrophes and accented letters count as separators because they are not a-z.
    @Test
    void whatThePatternDoesNotKeep() {
        assertThat("don't stop".split("[^a-z]+")).containsExactly("don", "t", "stop");
        assertThat("route66 café".split("[^a-z]+")).containsExactly("route", "caf");
        // Fix ideas: "[^a-z']+" keeps apostrophes; "[^\\p{L}]+" keeps letters from any language.
    }

    // YOUR EDGE CASES HERE (empty text? only punctuation? try changing TEXT and predict each stage first)
}
