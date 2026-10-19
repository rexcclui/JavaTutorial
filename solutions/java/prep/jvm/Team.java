package prep.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * A team of members, used to show the difference between a shallow copy and a deep copy.
 */
public class Team {
    public static class Member {
        public String name;
        public Member(String name) { this.name = name; }
    }

    public final String name;
    public final List<Member> members = new ArrayList<>();

    public Team(String name) { this.name = name; }

    /**
     * Returns an independent copy of the team.
     *
     * How it works:
     *   1. Create a new Team with the same name.
     *   2. For every member create a NEW Member object with the same data (deep copy). A shallow copy would reuse the same Member objects, so changing a member in the copy would also change the original.
     *   3. String is immutable, so sharing the name string between copies is safe.
     *
     * Complexity: O(members)
     */
    public Team copy() {
        Team t = new Team(name);
        for (Member m : members) t.members.add(new Member(m.name));
        return t;
    }
    // TWIST: why is a copy-constructor usually preferred over Cloneable/clone()?
}
