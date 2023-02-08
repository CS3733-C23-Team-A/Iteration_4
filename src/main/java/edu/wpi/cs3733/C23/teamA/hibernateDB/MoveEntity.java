package edu.wpi.cs3733.C23.teamA.hibernateDB;

import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;

@Entity
@Table(name = "move")
// @IdClass(MoveEntityPK.class)
public class MoveEntity {

  @Id
  @Getter
  @Setter
  @ManyToOne
  @JoinColumn(
      name = "nodeid",
      foreignKey =
          @ForeignKey(
              name = "node_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (nodeid) REFERENCES node(nodeid) ON UPDATE CASCADE ON DELETE CASCADE"))
  private NodeEntity node;

  @Id
  @Getter
  @Setter
  @ManyToOne
  @JoinColumn(
      name = "longname",
      foreignKey =
          @ForeignKey(
              name = "longname_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (longname) REFERENCES locationname(longname) ON UPDATE CASCADE ON DELETE CASCADE"))
  private LocationnameEntity locationname;

  @Id
  @Column(name = "movedate", nullable = false)
  @Getter
  @Setter
  private Timestamp movedate;

  public MoveEntity() {}

  public MoveEntity(NodeEntity node, LocationnameEntity locationname, Timestamp movedate) {
    this.node = node;
    this.locationname = locationname;
    this.movedate = movedate;
  }

  public MoveEntity(NodeEntity node, LocationnameEntity locationname) {
    this.node = node;
    this.locationname = locationname;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (this == obj) return true;
    if (this.getClass() != obj.getClass()) return false;
    MoveEntity mo = (MoveEntity) obj;
    return (this.locationname.equals(mo.locationname)
        && this.movedate.equals(mo.movedate)
        && this.node.equals(mo.node));
  }

  public static String mostRecentLoc(String nodeID, Session session) {
    MoveEntity move =
        session
            .createQuery(
                "select mov from MoveEntity mov where mov.node = '"
                    + nodeID
                    + "' order by mov.movedate desc",
                MoveEntity.class)
            .setMaxResults(1)
            .getSingleResult();
    return move.getLocationname().getLongname();
  }

  @Override
  public int hashCode() {
    return node.getXcoord() * node.getYcoord() + movedate.getNanos();
  }
}
