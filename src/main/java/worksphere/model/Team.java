package worksphere.model;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private String name;
    private List<TeamMember> members;

    public Team(String name) {
        this.name = name;
        this.members = new ArrayList<>(); // ✅ ArrayList
    }

    public String getName() {
        return name;
    }

    public List<TeamMember> getMembers() {
        return members;
    }

    // Optional helper methods (recommended)
    public void addMember(TeamMember member) {
        members.add(member);
    }

    public void removeMember(TeamMember member) {
        members.remove(member);
    }
}
