package prep.jvm;

import java.util.ArrayList;
import java.util.List;

public class Team {
    public static class Member {
        public String name;
        public Member(String name) { this.name = name; }
    }

    public final String name;
    public final List<Member> members = new ArrayList<>();

    public Team(String name) { this.name = name; }

    /** Deep copy: new list AND new Member objects. (String is immutable so sharing it is safe.) */
    public Team copy() {
        Team t = new Team(name);
        for (Member m : members) t.members.add(new Member(m.name));
        return t;
    }
    // TWIST: why is a copy-constructor usually preferred over Cloneable/clone()?
}
