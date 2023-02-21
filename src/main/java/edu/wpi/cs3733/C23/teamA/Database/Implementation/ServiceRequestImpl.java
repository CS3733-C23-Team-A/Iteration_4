package edu.wpi.cs3733.C23.teamA.Database.Implementation;

import static edu.wpi.cs3733.C23.teamA.Database.API.ADBSingletonClass.getSessionFactory;

import edu.wpi.cs3733.C23.teamA.Database.API.IDatabaseAPI;
import edu.wpi.cs3733.C23.teamA.Database.API.Observable;
import edu.wpi.cs3733.C23.teamA.Database.Entities.EmployeeEntity;
import edu.wpi.cs3733.C23.teamA.Database.Entities.ServiceRequestEntity;
import edu.wpi.cs3733.C23.teamA.enums.Status;
import edu.wpi.cs3733.C23.teamA.enums.UrgencyLevel;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ServiceRequestImpl extends Observable
    implements IDatabaseAPI<ServiceRequestEntity, Integer> {
  private List<ServiceRequestEntity> services;
  private static final ServiceRequestImpl instance = new ServiceRequestImpl();

  private ServiceRequestImpl() {
    Session session;
    session = getSessionFactory().openSession();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<ServiceRequestEntity> criteria = builder.createQuery(ServiceRequestEntity.class);
    criteria.from(ServiceRequestEntity.class);
    List<ServiceRequestEntity> records = session.createQuery(criteria).getResultList();
    session.close();
    services = records;
  }

  public void refresh() {
    Session session;
    session = getSessionFactory().openSession();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<ServiceRequestEntity> criteria = builder.createQuery(ServiceRequestEntity.class);
    criteria.from(ServiceRequestEntity.class);
    List<ServiceRequestEntity> records = session.createQuery(criteria).getResultList();
    session.close();
    services = records;
  }

  public List<ServiceRequestEntity> getAll() {
    return services;
  }

  public void exportToCSV(String filename) throws IOException {
    String file = filename + "/servicerequest.csv";

    File csvFile = new File(file);
    FileWriter fileWriter = new FileWriter(csvFile);
    fileWriter.write(
        "requestid,date,description,employeeassigned,name,requestype,status,urgency,employeeid,location\n");
    for (ServiceRequestEntity ser : services) {
      fileWriter.write(
          ser.getRequestid()
              + ","
              + ser.getDate()
              + ","
              + ser.getDescription()
              + ","
              + ser.getEmployeeAssigned()
              + ","
              + ser.getName()
              + ","
              + ser.getRequestType()
              + ","
              + ser.getStatus()
              + ","
              + ser.getUrgency()
              + ","
              + ser.getEmployee().getEmployeeid()
              + ","
              + ser.getLocation().getLongname()
              + "\n");
    }
    fileWriter.close();
    ComputerRequestImpl.getInstance().exportToCSV(filename);
    SecurityRequestImpl.getInstance().exportToCSV(filename);
    SanitationRequestImpl.getInstance().exportToCSV(filename);
    PatientTransportimpl.getInstance().exportToCSV(filename);
  }

  public void importFromCSV(String filename) throws FileNotFoundException {
    //    services.forEach(service -> session.remove(session.get(ServiceRequestEntity.class,
    // service.getRequestid())));
    //    services.clear();
    //
    //    File emps = new File("src/main/java/edu/wpi/cs3733/C23/teamA/Database/CSV/" + filename);
    //
    //    Transaction tx = session.beginTransaction();
    //    Scanner read = new Scanner(emps);
    //    int count = 0;
    //    read.nextLine();
    //
    //    while (read.hasNextLine()) {
    //      String[] b = read.nextLine().split(",");
    //      session.persist(new ServiceRequestEntity(b[0], b[4], b[3], b[1], b[2]));
    //      services.add(session.get(ServiceRequestEntity.class, b[0]));
    //    }
    //    tx.commit();
  }

  public void add(ServiceRequestEntity s) {
    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    session.persist(s);
    ServiceRequestImpl.getInstance().refresh();
    tx.commit();
    notifyAllObservers();
  }

  public void delete(Integer s) {
    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    ComputerRequestImpl.getInstance().removeFromList(s);
    SecurityRequestImpl.getInstance().removeFromList(s);
    PatientTransportimpl.getInstance().removeFromList(s);
    SanitationRequestImpl.getInstance().removeFromList(s);

    session.remove(get(s));
    ListIterator<ServiceRequestEntity> li = services.listIterator();
    while (li.hasNext()) {
      if (li.next().getRequestid() == s) {
        li.remove();
      }
    }
    tx.commit();
    session.close();
    notifyAllObservers();
  }

  public void update(Integer ID, ServiceRequestEntity obj) {
    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();

    ListIterator<ServiceRequestEntity> li = services.listIterator();
    while (li.hasNext()) {
      if (li.next().getRequestid() == ID) {
        li.remove();
      }
    }

    ServiceRequestEntity ser = session.get(ServiceRequestEntity.class, ID);
    ser.setName(obj.getName());
    ser.setDate(obj.getDate());
    ser.setDescription(obj.getDescription());
    ser.setLocation(obj.getLocation());
    ser.setEmployee(obj.getEmployee());
    ser.setEmployeeAssigned(obj.getEmployeeAssigned());
    ser.setRequestType(obj.getRequestType());
    ser.setUrgency(obj.getUrgency());
    ser.setStatus(obj.getStatus());

    services.add(ser);
    tx.commit();
    session.close();
    notifyAllObservers();
  }

  public void addToList(ServiceRequestEntity ser) {
    services.add(ser);
  }

  public void removeFromList(Integer ID) {
    ListIterator<ServiceRequestEntity> li = services.listIterator();
    while (li.hasNext()) {
      if (li.next().getRequestid() == ID) {
        li.remove();
      }
    }
  }

  public void updateList(Integer ID, ServiceRequestEntity ser) {
    ListIterator<ServiceRequestEntity> li = services.listIterator();
    while (li.hasNext()) {
      if (li.next().getRequestid() == ID) {
        li.remove();
        li.add(ser);
      }
    }
  }

  public ArrayList<ServiceRequestEntity> getAllByEmployee(String id) {
    ArrayList<ServiceRequestEntity> fin = new ArrayList<ServiceRequestEntity>();
    for (ServiceRequestEntity ser : services) {
      if (ser.getEmployee().getHospitalid().equals(id)) {
        fin.add(ser);
      }
    }
    return fin;
  }

  public ServiceRequestEntity get(Integer ID) {
    for (ServiceRequestEntity ser : services) {
      if (ser.getRequestid() == ID) return ser;
    }
    return null;
  }

  public ArrayList<ServiceRequestEntity> getServiceRequestByAssigned(String name) {
    ArrayList<ServiceRequestEntity> sers = new ArrayList<>();
    for (ServiceRequestEntity ser : services) {
      if (ser.getEmployeeAssigned().equals(name)) sers.add(ser);
    }
    return sers;
  }

  public ArrayList<ServiceRequestEntity> getServiceRequestByUnassigned() {
    ArrayList<ServiceRequestEntity> sers = new ArrayList<>();
    for (ServiceRequestEntity ser : services) {
      if (ser.getEmployeeAssigned().equals("Unassigned")) sers.add(ser);
    }
    return sers;
  }

  public void updateStatus(Status status, Integer ID) {
    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();

    ServiceRequestEntity serv = session.get(ServiceRequestEntity.class, ID);
    serv.setStatus(status);
    ListIterator<ServiceRequestEntity> li = services.listIterator();
    while (li.hasNext()) {
      if (li.next().getRequestid() == ID) {
        li.remove();
      }
    }
    services.add(serv);

    ComputerRequestImpl.getInstance().updateStatus(ID, status);
    PatientTransportimpl.getInstance().updateStatus(ID, status);
    SecurityRequestImpl.getInstance().updateStatus(ID, status);
    SecurityRequestImpl.getInstance().updateStatus(ID, status);
    AccessabilityImpl.getInstance().updateStatus(ID, status);

    tx.commit();
    session.close();
  }

  public void updateEmployee(EmployeeEntity employee, Integer ID) {
    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();

    ServiceRequestEntity serv = session.get(ServiceRequestEntity.class, ID);
    serv.setEmployeeAssigned(employee);
    ListIterator<ServiceRequestEntity> li = services.listIterator();
    while (li.hasNext()) {
      if (li.next().getRequestid() == ID) {
        li.remove();
      }
    }

    ComputerRequestImpl.getInstance().updateEmployee(ID, employee);
    PatientTransportimpl.getInstance().updateEmployee(ID, employee);
    SecurityRequestImpl.getInstance().updateEmployee(ID, employee);
    SecurityRequestImpl.getInstance().updateEmployee(ID, employee);
    AccessabilityImpl.getInstance().updateEmployee(ID, employee);
    services.add(serv);

    tx.commit();
    session.close();
  }

  public ArrayList<ServiceRequestEntity> getOutstandingRequests() {
    ArrayList<ServiceRequestEntity> fin = new ArrayList<>();

    for (ServiceRequestEntity ser : services) {
      if (Timestamp.from(Instant.now()).getDay() - ser.getDate().getDay() > 1
              && ser.getUrgency() == UrgencyLevel.EXTREMELY
          || Timestamp.from(Instant.now()).getDay() - ser.getDate().getDay() > 3
              && ser.getUrgency() == UrgencyLevel.HIGH
          || Timestamp.from(Instant.now()).getDay() - ser.getDate().getDay() > 5
              && ser.getUrgency() == UrgencyLevel.MEDIUM
          || Timestamp.from(Instant.now()).getDay() - ser.getDate().getDay() > 8
              && ser.getUrgency() == UrgencyLevel.LOW) {
        fin.add(ser);
      }
    }
    return fin;
  }

  public ArrayList<ServiceRequestEntity> getRequestAtLocation(String longname) {
    ArrayList<ServiceRequestEntity> fin = new ArrayList<>();

    for (ServiceRequestEntity ser : services) {
      if (ser.getLocation().getLongname().equals(longname)) {
        fin.add(ser);
      }
    }
    return fin;
  }

  public static ServiceRequestImpl getInstance() {
    return instance;
  }
}
