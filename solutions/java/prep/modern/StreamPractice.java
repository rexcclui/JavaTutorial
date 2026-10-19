package prep.modern;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Small problems solved with the Streams API instead of manual loops.
 */
public class StreamPractice {

    /**
     * Squares of the even numbers, keeping the original order.
     *
     * How it works:
     *   1. filter keeps only numbers where n % 2 == 0.
     *   2. map replaces each number with its square.
     *   3. toList collects the result.
     *
     * Example: [1, 2, 3, 4]  ->  [4, 16]
     *
     * Complexity: time O(n)
     */
    public static List<Integer> evenSquares(List<Integer> nums) {
        return nums.stream().filter(n -> n % 2 == 0).map(n -> n * n).toList();
    }

    /**
     * Groups the words into buckets by their length.
     *
     * How it works:
     *   1. Collectors.groupingBy(String::length) creates a Map from length to the list of words with that length.
     *   2. Words keep their original order inside each list.
     *
     * Example: [a, bb, cc, d]  ->  {1=[a, d], 2=[bb, cc]}
     *
     * Complexity: time O(n)
     */
    public static Map<Integer, List<String>> groupByLength(List<String> words) {
        return words.stream().collect(Collectors.groupingBy(String::length));
    }

    /**
     * The longest word; the first one wins on ties; empty Optional if the list is empty.
     *
     * How it works:
     *   1. reduce walks the words keeping the current best.
     *   2. A later word replaces the best only if it is strictly longer, so on a tie the earlier word stays.
     *
     * Example: [aa, bbb, ccc]  ->  Optional[bbb]
     *
     * Complexity: time O(n)
     */
    public static Optional<String> longest(List<String> words) {
        return words.stream().reduce((a, b) -> b.length() > a.length() ? b : a);
    }

    /**
     * Upper-cases every word and joins them with ", ".
     *
     * How it works:
     *   1. map(String::toUpperCase) converts each word.
     *   2. Collectors.joining(", ") glues them together.
     *
     * Example: [a, b]  ->  "A, B"
     *
     * Complexity: time O(n)
     */
    public static String joinUpper(List<String> words) {
        return words.stream().map(String::toUpperCase).collect(Collectors.joining(", "));
    }

    /**
     * Average of the numbers, or 0.0 for an empty list.
     *
     * How it works:
     *   1. mapToInt turns Stream<Integer> into an IntStream (no boxing).
     *   2. average() returns an OptionalDouble, which is empty for no elements; orElse(0.0) supplies the default.
     *
     * Example: [1, 2, 3, 4]  ->  2.5
     *
     * Complexity: time O(n)
     */
    public static double average(List<Integer> nums) {
        return nums.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    /**
     * Flattens a list of lists into one sorted list without duplicates.
     *
     * How it works:
     *   1. flatMap(List::stream) merges all inner lists into one stream.
     *   2. distinct removes duplicate values.
     *   3. sorted puts them in ascending order.
     *
     * Example: [[3, 1], [2, 3], []]  ->  [1, 2, 3]
     *
     * Complexity: time O(n log n)
     */
    public static List<Integer> flattenDistinctSorted(List<List<Integer>> lists) {
        return lists.stream().flatMap(List::stream).distinct().sorted(Comparator.naturalOrder()).toList();
        // TWIST: replace .stream() with .parallelStream() -- when does that help, and when does it hurt?
    }
}
