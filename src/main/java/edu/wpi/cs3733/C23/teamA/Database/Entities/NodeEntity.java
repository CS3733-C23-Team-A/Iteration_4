package edu.wpi.cs3733.C23.teamA.Database.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

  public NodeEntity() {}

  public NodeEntity(String nodeid, Integer xcoord, Integer ycoord, String floor, String building) {
    this.nodeid = nodeid;
    this.xcoord = xcoord;
    this.ycoord = ycoord;
    this.floor = floor;
    this.building = building;
  }
}
