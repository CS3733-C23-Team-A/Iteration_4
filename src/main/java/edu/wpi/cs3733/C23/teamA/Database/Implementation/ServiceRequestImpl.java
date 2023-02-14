package edu.wpi.cs3733.C23.teamA.Database.Implementation;

import static edu.wpi.cs3733.C23.teamA.Database.API.ADBSingletonClass.getSessionFactory;

import edu.wpi.cs3733.C23.teamA.Database.API.IDatabaseAPI;
import edu.wpi.cs3733.C23.teamA.Database.Entities.ServiceRequestEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ServiceRequestImpl implements IDatabaseAPI<ServiceRequestEntity, Integer> {
  private final List<ServiceRequestEntity> services;

  private Session session;

  public ServiceRequestImpl() {
    session = getSessionFactory().openSession();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<ServiceRequestEntity> criteria = builder.createQuery(ServiceRequestEntity.class);
    criteria.from(ServiceRequestEntity.class);
    services = session.createQuery(criteria).getResultList();
  }

  public List<ServiceRequestEntity> getAll() {
    return services;
  }

  public void exportToCSV(String filename) throws IOException {
    //    if (!filename[filename.length()-3, filename.length()].equals(".csv")){
    //      filename+=".csv";
    //    }

    File csvFile =
        new File("src/main/java/edu/wpi/cs3733/C23/teamA/Database/CSVBackup/servicerequest.csv");
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
    new ComputerRequestImpl().exportToCSV("computerrequest.csv");
    new SecurityRequestImpl().exportToCSV("securityrequest.csv");
    new SanitationRequestImpl().exportToCSV("sanitationrequest.csv");
  }

  public void importFromCSV(String filename) throws FileNotFoundException {}

  public void add(ServiceRequestEntity s) {
    Transaction tx = session.beginTransaction();
    session.persist(s);
    services.add(s);
    tx.commit();
    session.close();
  }

  public void delete(Integer s) {
    Transaction tx = session.beginTransaction();
    new ComputerRequestImpl().removeFromList(s);
    new SanitationRequestImpl().removeFromList(s);
    new SecurityRequestImpl().removeFromList(s);

    session.remove(get(s));
    ListIterator<ServiceRequestEntity> li = services.listIterator();
    while (li.hasNext()) {
      if (li.next().getRequestid() == s) {
        li.remove();
      }
    }
    tx.commit();
  }

  public void update(Integer ID, ServiceRequestEntity obj) {
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
      if (ser.getEmployee().getEmployeeid().equals(id)) {
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

  @Override
  public void closeSession() {
    session.close();
  }
}