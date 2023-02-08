package edu.wpi.cs3733.C23.teamA.hibernateDB;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "node")
public class NodeEntity {
  @Id
  @Column(name = "nodeid", nullable = false, length = -1)
  @Getter
  @Setter
  private String nodeid;

  @Basic
  @Column(name = "xcoord", nullable = false)
  @Getter
  @Setter
  private Integer xcoord;

  @Basic
  @Column(name = "ycoord", nullable = false)
  @Getter
  @Setter
  private Integer ycoord;

  @Basic
  @Column(name = "floor", nullable = false, length = -1)
  @Getter
  @Setter
  private String floor;

  @Basic
  @Column(name = "building", nullable = false, length = -1)
  @Getter
  @Setter
  private String building;

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (this == obj) return true;
    if (this.getClass() != obj.getClass()) return false;
    NodeEntity other = (NodeEntity) obj;
    return nodeid.equals(other.getNodeid());
  }

  public static List<NodeEntity> getNodeOnFloor(String floor, Session session){
    String hql = "select nod from NodeEntity nod where nod.floor = '" + floor + "'";
    Query query = session.createQuery(hql);
    return query.getResultList();

  }
}
