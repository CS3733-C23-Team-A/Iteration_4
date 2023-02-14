package edu.wpi.cs3733.C23.teamA.Database.Implementation;

import static edu.wpi.cs3733.C23.teamA.Database.API.ADBSingletonClass.getSessionFactory;

import edu.wpi.cs3733.C23.teamA.Database.API.IDatabaseAPI;
import edu.wpi.cs3733.C23.teamA.Database.Entities.SanitationRequestEntity;
import edu.wpi.cs3733.C23.teamA.Database.Entities.ServiceRequestEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SanitationRequestImpl implements IDatabaseAPI<SanitationRequestEntity, Integer> {

  private List<SanitationRequestEntity> sanrequests;

  Session session;

  public SanitationRequestImpl() {
    session = getSessionFactory().openSession();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<SanitationRequestEntity> criteria =
        builder.createQuery(SanitationRequestEntity.class);
    criteria.from(SanitationRequestEntity.class);
    sanrequests = session.createQuery(criteria).getResultList();
  }

  public List<SanitationRequestEntity> getAll() {
    return sanrequests;
  }

  public void exportToCSV(String filename) throws IOException {
    if (filename.length() > 4) {
      if (!filename.substring(filename.length() - 4).equals(".csv")) {
        filename += ".csv";
      }
    } else filename += ".csv";
    File csvFile =
        new File("src/main/java/edu/wpi/cs3733/C23/teamA/Database/CSVBackup/" + filename);
    FileWriter fileWriter = new FileWriter(csvFile);
    fileWriter.write("category,requestid\n");
    for (SanitationRequestEntity ser : sanrequests) {
      fileWriter.write(ser.getCategory() + "," + ser.getRequestid() + "\n");
    }
    fileWriter.close();
  }

  public void importFromCSV(String filename) throws FileNotFoundException {
    if (filename.length() > 4) {
      if (!filename.substring(filename.length() - 4).equals(".csv")) {
        filename += ".csv";
      }
    } else filename += ".csv";
  }

  public void add(SanitationRequestEntity c) {
    ServiceRequestImpl serv = new ServiceRequestImpl();
    Transaction tx = session.beginTransaction();
    session.persist(c);
    tx.commit();
    sanrequests.add(c);
    serv.addToList(c);
    serv.closeSession();
  }

  public void delete(Integer c) {
    Transaction tx = session.beginTransaction();
    session.remove(get(c));
    ListIterator<SanitationRequestEntity> li = sanrequests.listIterator();
    while (li.hasNext()) {
      if (li.next().getRequestid() == c) {
        li.remove();
      }
    }
    ServiceRequestImpl servI = new ServiceRequestImpl();
    servI.removeFromList(c);
    servI.closeSession();
    tx.commit();
  }

  public void update(Integer ID, SanitationRequestEntity obj) {
    Transaction tx = session.beginTransaction();

    ListIterator<SanitationRequestEntity> li = sanrequests.listIterator();
    while (li.hasNext()) {
      if (li.next().getRequestid() == ID) {
        li.remove();
      }
    }

    SanitationRequestEntity c = get(ID);

    c.setCategory(obj.getCategory());
    c.setName(obj.getName());
    c.setDate(obj.getDate());
    c.setDescription(obj.getDescription());
    c.setLocation(obj.getLocation());
    c.setEmployee(obj.getEmployee());
    c.setEmployeeAssigned(obj.getEmployeeAssigned());
    c.setRequestType(obj.getRequestType());
    c.setUrgency(obj.getUrgency());
    c.setStatus(obj.getStatus());

    ServiceRequestEntity ser =
        new ServiceRequestEntity(
            ID,
            obj.getName(),
            obj.getEmployee(),
            obj.getLocation(),
            obj.getDescription(),
            obj.getUrgency(),
            obj.getRequestType(),
            obj.getStatus(),
            obj.getEmployeeAssigned(),
            obj.getDate());
    ServiceRequestImpl serv = new ServiceRequestImpl();
    serv.update(ID, ser);
    serv.closeSession();
    sanrequests.add(c);

    tx.commit();
  }

  public void removeFromList(Integer s) {
    ListIterator<SanitationRequestEntity> li = sanrequests.listIterator();
    while (li.hasNext()) {
      if (li.next().getRequestid() == s) {
        li.remove();
      }
    }
  }

  public SanitationRequestEntity get(Integer ID) {

    for (SanitationRequestEntity ser : sanrequests) {
      if (ser.getRequestid() == ID) return ser;
    }
    return null;
  }

  @Override
  public void closeSession() {}
}
