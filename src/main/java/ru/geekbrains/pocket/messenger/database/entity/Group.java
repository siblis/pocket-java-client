package ru.geekbrains.pocket.messenger.database.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "groups")
public class Group {

    @Id
    @Column
    private String id;

    @Column(name = "creator_id")
    private String creatorId;

    @Column
    private String space;

    @Column
    private String name;

    @Column
    private String description;

    @Column(name = "invitation_code")
    private String invitationCode;

    @Column(name = "is_public")
    private boolean isPublic;


    public Group(String id, String creatorId, String space, String name,
                 String description, String invitationCode, boolean isPublic) {
        this.id = id;
        this.creatorId = creatorId;
        this.space = space;
        this.name = name;
        this.description = description;
        this.invitationCode = invitationCode;
        this.isPublic = isPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return isPublic == group.isPublic &&
                id.equals(group.id) &&
                creatorId.equals(group.creatorId) &&
                Objects.equals(space, group.space) &&
                name.equals(group.name) &&
                Objects.equals(description, group.description) &&
                Objects.equals(invitationCode, group.invitationCode);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.creatorId);
        hash = 41 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", creatorId=" + creatorId +
                ", space=" + space +
                ", name=" + name +
                ", description=" + description +
                ", invitationCode=" + invitationCode +
                ", isPublic=" + isPublic +
                '}';
    }
}
