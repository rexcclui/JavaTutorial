package prep.jvm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import prep.jvm.Team.Member;

class TeamTest {
    @Test
    void copyHasSameContent() {
        Team a = new Team("core");
        a.members.add(new Member("ann"));
        Team b = a.copy();
        assertThat(b.name).isEqualTo("core");
        assertThat(b.members).extracting(m -> m.name).containsExactly("ann");
    }

    @Test
    void addingToCopyDoesNotAffectOriginal() {
        Team a = new Team("core");
        a.members.add(new Member("ann"));
        Team b = a.copy();
        b.members.add(new Member("bob"));
        assertThat(a.members).hasSize(1);
    }

    @Test
    void mutatingSharedMemberDoesNotLeak() {
        Team a = new Team("core");
        a.members.add(new Member("ann"));
        Team b = a.copy();
        b.members.get(0).name = "CHANGED";
        assertThat(a.members.get(0).name).isEqualTo("ann");   // fails with a shallow copy
    }
}
