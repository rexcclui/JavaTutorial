package prep.modern;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreamPractice {

    public static List<Integer> evenSquares(List<Integer> nums) {
        return nums.stream().filter(n -> n % 2 == 0).map(n -> n * n).toList();
    }

    public static Map<Integer, List<String>> groupByLength(List<String> words) {
        return words.stream().collect(Collectors.groupingBy(String::length));
    }

    /** max() returns the LAST maximal element for ties in some implementations, so use reduce for "first wins". */
    public static Optional<String> longest(List<String> words) {
        return words.stream().reduce((a, b) -> b.length() > a.length() ? b : a);
    }

    public static String joinUpper(List<String> words) {
        return words.stream().map(String::toUpperCase).collect(Collectors.joining(", "));
    }

    public static double average(List<Integer> nums) {
        return nums.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    public static List<Integer> flattenDistinctSorted(List<List<Integer>> lists) {
        return lists.stream().flatMap(List::stream).distinct().sorted(Comparator.naturalOrder()).toList();
        // TWIST: replace .stream() with .parallelStream() -- when does that help, and when does it hurt?
    }
}
