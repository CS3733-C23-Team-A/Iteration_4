package edu.wpi.cs3733.C23.teamA.Database.Entities.Implementation;

import static edu.wpi.cs3733.C23.teamA.Database.API.ADBSingletonClass.getSessionFactory;

import edu.wpi.cs3733.C23.teamA.Database.API.IDatabaseAPI;
import edu.wpi.cs3733.C23.teamA.Database.API.Observable;
import edu.wpi.cs3733.C23.teamA.Database.Entities.EmployeeEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;

public class EmployeeImpl extends Observable implements IDatabaseAPI<EmployeeEntity, Integer> {
  private static final EmployeeImpl instance = new EmployeeImpl();

  private List<EmployeeEntity> employees;

  private EmployeeImpl() {
    Session session = getSessionFactory().openSession();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<EmployeeEntity> criteria = builder.createQuery(EmployeeEntity.class);
    criteria.from(EmployeeEntity.class);
    employees = session.createQuery(criteria).getResultList();
    session.close();
  }

  public List<EmployeeEntity> getAll() {
    return employees;
  }

  public void exportToCSV(String filename) throws IOException {
    filename += "/employee.csv";

    File csvFile = new File(filename);
    FileWriter fileWriter = new FileWriter(csvFile);
    fileWriter.write("employeeid,hospitalid,job,name,password,username\n");
    for (EmployeeEntity emp : employees) {
      fileWriter.write(
          emp.getEmployeeid()
              + ","
              + emp.getHospitalid()
              + ","
              + emp.getJob()
              + ","
              + emp.getName()
              + ","
              + emp.getPassword()
              + ","
              + emp.getUsername()
              + "\n");
    }
    fileWriter.close();
  }

  public void update(Integer ID, EmployeeEntity obj) {
    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();

    employees.stream()
        .filter(employee -> employee.getEmployeeid() == ID)
        .toList()
        .forEach(
            employee -> {
              employees.remove(employee);
            });

    EmployeeEntity emp = session.get(EmployeeEntity.class, ID);

    emp.setName(obj.getName());
    emp.setPassword(obj.getPassword());
    emp.setHospitalid(obj.getHospitalid());
    emp.setUsername(obj.getUsername());
    emp.setJob(obj.getJob());

    tx.commit();
    employees.add(session.get(EmployeeEntity.class, obj.getEmployeeid()));
    session.close();
    //    notifyAllObservers();
    Thread t =
        new Thread(
            () -> {
              SecurityRequestImpl.getInstance().refresh();
              ServiceRequestImpl.getInstance().refresh();
              SanitationRequestImpl.getInstance().refresh();
              PatientTransportimpl.getInstance().refresh();
              AccessabilityImpl.getInstance().refresh();
              AudioVisualImpl.getInstance().refresh();
              ComputerRequestImpl.getInstance().refresh();
            });
    t.start();
  }

  public ArrayList<String> checkPass(String user, String pass) {
    ArrayList<String> info = new ArrayList<>();
    for (EmployeeEntity emp : employees) {
      if (emp.getUsername().equals(user) && emp.getPassword().equals(pass)) {
        info.add(emp.getHospitalid());
        info.add(emp.getJob());
        info.add(emp.getName());
        info.add(Integer.toString(emp.getEmployeeid()));
        return info;
      }
    }
    info.add("");
    return info;
  }

  public void add(EmployeeEntity e) {
    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    session.persist(e);
    tx.commit();
    EmployeeImpl.getInstance().refresh();
    session.close();
    notifyAllObservers();
  }

  public void importFromCSV(String filename) throws FileNotFoundException {
    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    if (filename.length() > 4) {
      if (!filename.substring(filename.length() - 4).equals(".csv")) {
        filename += ".csv";
      }
    } else filename += ".csv";

    String hql = "delete from SecurityRequestEntity";
    MutationQuery q = session.createMutationQuery(hql);
    q.executeUpdate();

    hql = "delete from SanitationRequestEntity ";
    q = session.createMutationQuery(hql);
    q.executeUpdate();

    hql = "delete from ComputerRequestEntity ";
    q = session.createMutationQuery(hql);
    q.executeUpdate();

    hql = "delete from ServiceRequestEntity ";
    q = session.createMutationQuery(hql);
    q.executeUpdate();

    hql = "delete from AccessibilityRequestEntity ";
    q = session.createMutationQuery(hql);
    q.executeUpdate();

    hql = "delete from AudioVisualRequestEntity ";
    q = session.createMutationQuery(hql);
    q.executeUpdate();

    hql = "delete from PatientTransportRequestEntity ";
    q = session.createMutationQuery(hql);
    q.executeUpdate();

    employees.forEach(
        employee -> session.remove(session.get(EmployeeEntity.class, employee.getEmployeeid())));
    employees.clear();
    tx.commit();

    tx = session.beginTransaction();

    File emps = new File(filename);

    Scanner read = new Scanner(emps);
    int count = 1;
    read.nextLine();

    while (read.hasNextLine()) {
      String[] b = read.nextLine().split(",");
      session.persist(new EmployeeEntity(/*Integer.parseInt(b[0]),*/ b[1], b[5], b[4], b[2], b[3]));
      employees.add(session.get(EmployeeEntity.class, count));
      count++;
    }
    tx.commit();
    session.close();
  }

  /**
   * Deletes employees matchine id e
   *
   * @param e EmployeeId
   */
  public void delete(Integer e) {
    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    employees.stream()
        .filter(employee -> employee.getEmployeeid() == (e))
        .toList()
        .forEach(
            employee -> {
              session.remove(session.get(EmployeeEntity.class, employee.getEmployeeid()));
              employees.remove(employee);
            });
    tx.commit();
    session.close();
    notifyAllObservers();
  }

  public EmployeeEntity getByUsername(String user) {
    return employees.stream()
        .filter(employee -> employee.getUsername().equals(user))
        .findFirst()
        .orElseThrow();
  }

  public List<String> getListOfByJob(String job) {
    ArrayList<String> theList = new ArrayList<>();
    for (EmployeeEntity emp : employees) {
      if (emp.getJob().equals(job)) {
        theList.add(emp.getUsername());
      }
    }
    return theList;
  }

  public EmployeeEntity get(Integer ID) {
    //    return employees.stream()
    //        .filter(employee -> employee.getEmployeeid() == (ID))
    //        .findFirst()
    //        .orElseThrow();
    Session session = getSessionFactory().openSession();
    EmployeeEntity emp = session.get(EmployeeEntity.class, ID);
    session.close();
    return emp;
  }

  @Override
  public void refresh() {
    Session session = getSessionFactory().openSession();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<EmployeeEntity> criteria = builder.createQuery(EmployeeEntity.class);
    criteria.from(EmployeeEntity.class);
    employees = session.createQuery(criteria).getResultList();
    session.close();
  }

  public boolean usernameExists(String user) {
    for (EmployeeEntity e : employees) {
      if (e.getUsername().equals(user)) {
        return true;
      }
    }
    return false;
  }

  public static EmployeeImpl getInstance() {
    return instance;
  }
}
