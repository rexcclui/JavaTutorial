package prep.datastructures;

import java.util.ArrayDeque;
import java.util.Deque;

/** Second stack tracks the running minimum. */
public class MinStack {
    private final Deque<Integer> stack = new ArrayDeque<>();
    private final Deque<Integer> mins = new ArrayDeque<>();

    public void push(int x) {
        stack.push(x);
        mins.push(mins.isEmpty() ? x : Math.min(x, mins.peek()));
    }

    public int pop() {
        if (stack.isEmpty()) throw new IllegalStateException("empty");
        mins.pop();
        return stack.pop();
    }

    public int peek() {
        if (stack.isEmpty()) throw new IllegalStateException("empty");
        return stack.peek();
    }

    public int min() {
        if (mins.isEmpty()) throw new IllegalStateException("empty");
        return mins.peek();
    }

    public boolean isEmpty() { return stack.isEmpty(); }
}
