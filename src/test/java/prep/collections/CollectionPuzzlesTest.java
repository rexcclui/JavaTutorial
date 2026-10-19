package prep.collections;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CollectionPuzzlesTest {
    @Test
    void twoSum() {
        assertThat(CollectionPuzzles.twoSum(new int[] {2, 7, 11, 15}, 9)).containsExactly(0, 1);
        assertThat(CollectionPuzzles.twoSum(new int[] {3, 2, 4}, 6)).containsExactly(1, 2);
        assertThat(CollectionPuzzles.twoSum(new int[] {1, 2}, 100)).isEmpty();
    }

    @Test
    void topKWords() {
        String text = "the cat and the hat and THE bat";
        assertThat(CollectionPuzzles.topKWords(text, 2)).containsExactly("the", "and");
        assertThat(CollectionPuzzles.topKWords(text, 3)).containsExactly("the", "and", "bat");
        assertThat(CollectionPuzzles.topKWords("", 3)).isEmpty();
    }

    @Test
    void hasDuplicate() {
        assertThat(CollectionPuzzles.hasDuplicate(new int[] {1, 2, 3, 1})).isTrue();
        assertThat(CollectionPuzzles.hasDuplicate(new int[] {1, 2, 3})).isFalse();
        assertThat(CollectionPuzzles.hasDuplicate(new int[] {})).isFalse();
    }

    @Test
    void longestConsecutive() {
        assertThat(CollectionPuzzles.longestConsecutive(new int[] {100, 4, 200, 1, 3, 2})).isEqualTo(4);
        assertThat(CollectionPuzzles.longestConsecutive(new int[] {})).isEqualTo(0);
        assertThat(CollectionPuzzles.longestConsecutive(new int[] {1, 1, 1})).isEqualTo(1);
    }

    // YOUR EDGE CASES HERE (negatives? Integer.MIN_VALUE overflow in target - x?)
}
