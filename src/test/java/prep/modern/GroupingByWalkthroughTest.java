package prep.modern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;

/**
 * LEARNING TEST (no exercise to solve): everything Collectors.groupingBy can do, one form at a time.
 * Run:  ./run.sh modern GroupingBy        (add --verbose to see the printed maps)
 *
 * Shape of the call:
 *     stream.collect( groupingBy( classifier, mapFactory, downstream ) )
 *       classifier : Person -> key        decides which bucket each element goes to
 *       mapFactory : () -> Map            (optional) which Map to build, default HashMap
 *       downstream : Collector            (optional) what to do with the elements inside each bucket, default toList()
 */
class GroupingByWalkthroughTest {
    record Person(String name, String city, int age) {}

    private static final List<Person> PEOPLE = List.of(
            new Person("Ann", "Paris", 30),
            new Person("Bob", "Rome", 25),
            new Person("Cid", "Paris", 40),
            new Person("Dee", "Rome", 35),
            new Person("Eve", "Oslo", 28));

    // PURPOSE: Form 1 - groupingBy(classifier): key = what the classifier returns, value = List of the elements that got that key.
    @Test
    void form1_classifierOnly_valuesAreLists() {
        Map<String, List<Person>> byCity = PEOPLE.stream().collect(groupingBy(Person::city));
        System.out.println("form1: " + new TreeMap<>(byCity));
        assertThat(byCity.keySet()).containsExactlyInAnyOrder("Paris", "Rome", "Oslo");
        assertThat(byCity.get("Paris")).extracting(Person::name).containsExactly("Ann", "Cid");   // encounter order kept inside a bucket
        assertThat(byCity.get("Oslo")).extracting(Person::name).containsExactly("Eve");
    }

    // PURPOSE: Form 2 - groupingBy(classifier, downstream): the downstream collector reduces each bucket. counting() gives a Long per key.
    @Test
    void form2_downstreamCounting() {
        Map<String, Long> countByCity = PEOPLE.stream().collect(groupingBy(Person::city, counting()));
        System.out.println("form2: " + new TreeMap<>(countByCity));
        assertThat(countByCity).containsEntry("Paris", 2L).containsEntry("Rome", 2L).containsEntry("Oslo", 1L);
    }

    // PURPOSE: Other useful downstream collectors: mapping (transform before collecting), summingInt, averagingInt, maxBy, toSet.
    @Test
    void form2b_otherDownstreamCollectors() {
        Map<String, List<String>> names = PEOPLE.stream()
                .collect(groupingBy(Person::city, mapping(Person::name, toList())));
        assertThat(names.get("Rome")).containsExactly("Bob", "Dee");

        Map<String, Integer> totalAge = PEOPLE.stream()
                .collect(groupingBy(Person::city, summingInt(Person::age)));
        assertThat(totalAge).containsEntry("Paris", 70).containsEntry("Rome", 60);

        Map<String, Double> avgAge = PEOPLE.stream()
                .collect(groupingBy(Person::city, averagingInt(Person::age)));
        assertThat(avgAge).containsEntry("Paris", 35.0).containsEntry("Oslo", 28.0);

        Map<String, Optional<Person>> oldest = PEOPLE.stream()
                .collect(groupingBy(Person::city, maxBy(java.util.Comparator.comparingInt(Person::age))));
        assertThat(oldest.get("Rome").get().name()).isEqualTo("Dee");

        Map<String, Set<Integer>> ages = PEOPLE.stream()
                .collect(groupingBy(Person::city, mapping(Person::age, toSet())));
        assertThat(ages.get("Paris")).containsExactlyInAnyOrder(30, 40);
    }

    // PURPOSE: Form 3 - groupingBy(classifier, mapFactory, downstream): choose the Map type. TreeMap gives keys in sorted order (default HashMap has no order).
    @Test
    void form3_mapFactory_sortedKeys() {
        Map<String, Long> sorted = PEOPLE.stream().collect(groupingBy(Person::city, TreeMap::new, counting()));
        assertThat(sorted).isInstanceOf(TreeMap.class);
        assertThat(sorted.keySet()).containsExactly("Oslo", "Paris", "Rome");   // alphabetical
    }

    // PURPOSE: Nesting - a groupingBy inside a groupingBy makes a two-level map (city -> age group -> names).
    @Test
    void form4_nestedGrouping() {
        Map<String, Map<String, List<String>>> nested = PEOPLE.stream().collect(
                groupingBy(Person::city,
                        groupingBy(p -> p.age() >= 30 ? "30+" : "under30",
                                mapping(Person::name, toList()))));
        System.out.println("form4: " + new TreeMap<>(nested));
        assertThat(nested.get("Paris").get("30+")).containsExactly("Ann", "Cid");
        assertThat(nested.get("Rome").get("under30")).containsExactly("Bob");
        assertThat(nested.get("Rome").get("30+")).containsExactly("Dee");
    }

    // PURPOSE: The classifier can be any function, not only a getter - here we group numbers by even/odd and words by first letter.
    @Test
    void classifierCanBeAnyFunction() {
        Map<String, List<Integer>> parity = List.of(1, 2, 3, 4, 5).stream()
                .collect(groupingBy(n -> n % 2 == 0 ? "even" : "odd"));
        assertThat(parity.get("even")).containsExactly(2, 4);
        assertThat(parity.get("odd")).containsExactly(1, 3, 5);

        Map<Character, List<String>> byInitial = List.of("apple", "avocado", "banana").stream()
                .collect(groupingBy(w -> w.charAt(0)));
        assertThat(byInitial.get('a')).containsExactly("apple", "avocado");
    }

    // PURPOSE: Rule - the classifier must NOT return null: groupingBy throws NullPointerException ("element cannot be mapped to a null key").
    @Test
    void classifierMustNotReturnNull() {
        assertThatThrownBy(() -> List.of("a", "b").stream().collect(groupingBy(s -> (String) null)))
                .isInstanceOf(NullPointerException.class);
    }

    // PURPOSE: Difference from partitioningBy: partitioningBy always has exactly two keys, true and false, even when a side is empty; groupingBy only has keys that occurred.
    @Test
    void groupingByVersusPartitioningBy() {
        Map<Boolean, List<Integer>> parts = List.of(2, 4).stream().collect(partitioningBy(n -> n % 2 == 1));
        assertThat(parts).containsOnlyKeys(true, false);
        assertThat(parts.get(true)).isEmpty();                       // key exists, bucket empty

        Map<Boolean, List<Integer>> groups = List.of(2, 4).stream().collect(groupingBy(n -> n % 2 == 1));
        assertThat(groups).containsOnlyKeys(false);                  // only the key that occurred
    }

    // PURPOSE: Difference from toMap: toMap needs a unique key per element (duplicate key = IllegalStateException unless you pass a merge function); groupingBy is built for duplicates.
    @Test
    void groupingByVersusToMap() {
        assertThatThrownBy(() -> PEOPLE.stream().collect(toMap(Person::city, Person::name)))
                .isInstanceOf(IllegalStateException.class);          // "Duplicate key Paris"
        Map<String, String> merged = PEOPLE.stream()
                .collect(toMap(Person::city, Person::name, (a, b) -> a + "+" + b));
        assertThat(merged).containsEntry("Paris", "Ann+Cid");
    }

    // YOUR EDGE CASES HERE (group by age decade? by name length? count words by length?)
}
