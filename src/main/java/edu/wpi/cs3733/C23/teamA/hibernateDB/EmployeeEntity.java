package edu.wpi.cs3733.C23.teamA.hibernateDB;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Entity
@Table(
    name = "employee",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_emp_username",
            columnNames = {"username"}))
public class EmployeeEntity {

  @Id
  @Column(name = "employeeid", nullable = false, length = -1)
  @Getter
  @Setter
  private String employeeid;

  @Basic
  @Column(name = "username", nullable = false, length = -1)
  @Getter
  @Setter
  private String username;

  @Basic
  @Column(name = "password", nullable = false, length = -1)
  @Getter
  @Setter
  private String password;

  @Basic
  @Column(name = "job", nullable = false, length = -1)
  @Getter
  @Setter
  private String job;

  @Basic
  @Column(name = "name", nullable = false, length = -1)
  @Getter
  @Setter
  private String name;

  //  @Column(name = "requests", nullable = false)
  //  @Getter
  //  @Setter
  //  @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
  //  private Set<ServicerequestEntity> requests;
  //
  //  public EmployeeEntity addServiceRequest(ServicerequestEntity req) {
  //    this.requests.add(req);
  //    return this;
  //  }

  public EmployeeEntity(
      String employeeid, String username, String password, String job, String name) {
    this.employeeid = employeeid;
    this.username = username;
    this.password = password;
    this.job = job;
    this.name = name;
  }

  public EmployeeEntity() {}

  public static String checkPass(String user, String pass, Session session) {
    Transaction tx = session.beginTransaction();
    String hql = "select emp from EmployeeEntity emp where emp.username = '" + user + "'";
    Query query = session.createQuery(hql);
    final List<EmployeeEntity> emps = query.getResultList();
    for (EmployeeEntity emp : emps) {
      if (emp.getUsername().equals(user) && emp.getPassword().equals(pass)) {
        return emp.getEmployeeid();
      }
    }
    return "";
  }
}
