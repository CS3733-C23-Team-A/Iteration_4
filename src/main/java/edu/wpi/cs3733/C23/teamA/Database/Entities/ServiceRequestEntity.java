package edu.wpi.cs3733.C23.teamA.Database.Entities;

import edu.wpi.cs3733.C23.teamA.enums.Status;
import edu.wpi.cs3733.C23.teamA.enums.UrgencyLevel;
import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "servicerequest")
@Inheritance(strategy = InheritanceType.JOINED)
public class ServiceRequestEntity {
  // @TableGenerator(name = "serviceseq", allocationSize = 1, initialValue = 0)
  @GeneratedValue(strategy = GenerationType.IDENTITY) // , generator = "serviceseq")
  @Id
  @Cascade(org.hibernate.annotations.CascadeType.ALL)
  @Column(name = "requestid")
  @Setter
  @Getter
  private int requestid;

  @Basic
  @Column(name = "name", nullable = false, length = -1)
  @Setter
  @Getter
  private String name;

  @ManyToOne
  @JoinColumn(
      name = "employeeid",
      foreignKey =
          @ForeignKey(
              name = "employeeid_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (employeeid) REFERENCES employee(employeeid) ON UPDATE CASCADE ON DELETE CASCADE"))
  @Setter
  @Getter
  private EmployeeEntity employee;

  @ManyToOne
  @JoinColumn(
      name = "location",
      foreignKey =
          @ForeignKey(
              name = "longname",
              foreignKeyDefinition =
                  "FOREIGN KEY (location) REFERENCES locationname(longname) ON UPDATE CASCADE ON DELETE SET NULL"))
  @Setter
  @Getter
  private LocationNameEntity location;

  @Basic
  @Column(name = "description", nullable = false, length = -1)
  @Setter
  @Getter
  private String description;

  @Basic
  @Column(name = "urgency", nullable = false, length = -1)
  @Setter
  @Getter
  @Enumerated(EnumType.STRING)
  private UrgencyLevel urgency;

  @Basic
  @Column(name = "requesttype", nullable = false, length = -1)
  @Setter
  @Getter
  @Enumerated(EnumType.STRING)
  private RequestType requestType;

  @Basic
  @Column(name = "status", nullable = false, length = -1)
  @Setter
  @Getter
  @Enumerated(EnumType.STRING)
  private Status status;

  @ManyToOne(optional = true)
  @JoinColumn(
      name = "employeeassignedid",
      foreignKey =
          @ForeignKey(
              name = "assignedemployee_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (employeeassignedid) REFERENCES employee(employeeid) ON UPDATE CASCADE ON DELETE SET NULL"),
      nullable = true)
  @Setter
  @Getter
  private EmployeeEntity employeeAssigned;

  @Setter
  @Getter
  @Column(nullable = false)
  @CreationTimestamp
  private Timestamp date;

  public enum RequestType {
    SECURITY("Security"),
    COMPUTER("Computer"),
    SANITATION("Sanitation"),
    PATIENT_TRANSPORT("Patient Transport"),
    AV("Audio\\Visual"),
    ACCESSIBILITY("Accessibility");

    // FILL OUT TOMORROW WITH ISABELLA
    @NonNull public final String requestType;

    RequestType(@NonNull String requestType) {
      this.requestType = requestType;
    }
  }

  public ServiceRequestEntity() {}

  public ServiceRequestEntity(
      int requestid,
      String name,
      EmployeeEntity employee,
      LocationNameEntity location,
      String description,
      UrgencyLevel urgency,
      RequestType requestType,
      Status status,
      EmployeeEntity employeeAssigned,
      Timestamp date) {
    this.requestid = requestid;
    this.name = name;
    this.employee = employee;
    this.location = location;
    this.description = description;
    this.urgency = urgency;
    this.requestType = requestType;
    this.status = status;
    this.employeeAssigned = employeeAssigned;
    this.date = date;
  }

  public ServiceRequestEntity(
      String name,
      EmployeeEntity employee,
      LocationNameEntity location,
      String description,
      UrgencyLevel urgency,
      RequestType requestType,
      Status status,
      EmployeeEntity employeeAssigned) {
    this.name = name;
    this.employee = employee;
    this.location = location;
    this.description = description;
    this.urgency = urgency;
    this.requestType = requestType;
    this.status = status;
    this.employeeAssigned = employeeAssigned;
  }
}
