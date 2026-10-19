package prep.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * EXERCISE: shallow vs deep copy (heap, references, aliasing).
 * copy() must return an INDEPENDENT Team: changing the copy's members (or their data) must never affect the original.
 */
public class Team {
    public static class Member {
        public String name;
        public Member(String name) { this.name = name; }
    }

    public final String name;
    public final List<Member> members = new ArrayList<>();

    public Team(String name) { this.name = name; }

    public Team copy() {
        Team t = new Team(name);
        t.members.addAll(members);       // TODO: this is a SHALLOW copy -- the Member objects are shared
        return t;
    }
}
