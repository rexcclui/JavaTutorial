package prep.datastructures;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A stack that can also report its minimum in O(1).
 *
 * How it works:
 *   1. 'stack' holds the values; 'mins' holds the minimum at each depth, so mins.peek() is always the current minimum.
 *   2. Both stacks are pushed and popped together, so they always have the same size.
 */
public class MinStack {
    private final Deque<Integer> stack = new ArrayDeque<>();
    private final Deque<Integer> mins = new ArrayDeque<>();

    /**
     * Pushes a value.
     *
     * How it works:
     *   1. Push x on the main stack.
     *   2. Push min(x, current minimum) on the min stack.
     *
     * Complexity: O(1)
     */
    public void push(int x) {
        stack.push(x);
        mins.push(mins.isEmpty() ? x : Math.min(x, mins.peek()));
    }

    /**
     * Removes and returns the top value.
     *
     * How it works:
     *   1. Throw IllegalStateException if empty.
     *   2. Pop both stacks so the minimum goes back to what it was before this value.
     *
     * Complexity: O(1)
     */
    public int pop() {
        if (stack.isEmpty()) throw new IllegalStateException("empty");
        mins.pop();
        return stack.pop();
    }

    /**
     * Top value without removing it (IllegalStateException if empty).
     *
     * Complexity: O(1)
     */
    public int peek() {
        if (stack.isEmpty()) throw new IllegalStateException("empty");
        return stack.peek();
    }

    /**
     * Smallest value currently on the stack (IllegalStateException if empty).
     *
     * Complexity: O(1)
     */
    public int min() {
        if (mins.isEmpty()) throw new IllegalStateException("empty");
        return mins.peek();
    }

    /**
     * True when there are no values.
     *
     * Complexity: O(1)
     */
    public boolean isEmpty() { return stack.isEmpty(); }
}
