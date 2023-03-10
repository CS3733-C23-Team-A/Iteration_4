package edu.wpi.cs3733.C23.teamA.Database.Entities.Implementation;

import static edu.wpi.cs3733.C23.teamA.Database.API.ADBSingletonClass.getSessionFactory;

import edu.wpi.cs3733.C23.teamA.Database.API.IDatabaseAPI;
import edu.wpi.cs3733.C23.teamA.Database.API.Observable;
import edu.wpi.cs3733.C23.teamA.Database.Entities.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;

public class EdgeImpl extends Observable implements IDatabaseAPI<EdgeEntity, String> {
  private static final EdgeImpl instance = new EdgeImpl();

  private List<EdgeEntity> edges;

  private EdgeImpl() {
    Session session = getSessionFactory().openSession();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<EdgeEntity> criteria = builder.createQuery(EdgeEntity.class);
    criteria.from(EdgeEntity.class);
    List<EdgeEntity> records = session.createQuery(criteria).getResultList();
    edges = records;
    // collapseNode(session.get(NodeEntity.class, "AHALL001L2"));
    session.close();
  }

  public void refresh() {
    Session session = getSessionFactory().openSession();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<EdgeEntity> criteria = builder.createQuery(EdgeEntity.class);
    criteria.from(EdgeEntity.class);
    List<EdgeEntity> records = session.createQuery(criteria).getResultList();
    edges = records;
    // collapseNode(session.get(NodeEntity.class, "AHALL001L2"));
    session.close();
  }

  public List<EdgeEntity> getAll() {
    return edges;
  }

  /**
   * Finds connecting edges (edges that go to a certain node and all edges that come from that
   * nodea)
   *
   * @param e
   * @return Hashmap of all edges that go to a node (node2 is the node) and a List of Edges that
   *     come from the node (node1 is the node)
   */
  public HashMap<EdgeEntity, List<EdgeEntity>> nodeVectors(NodeEntity e) {
    Session session = getSessionFactory().openSession();
    HashMap<EdgeEntity, List<EdgeEntity>> vectors = new HashMap<>();
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<EdgeEntity> criteria = builder.createQuery(EdgeEntity.class);
    Root<EdgeEntity> item = criteria.from(EdgeEntity.class);

    // Find edge that goes to node e
    criteria.select(item).where(builder.equal(item.get("node2"), e));
    List<EdgeEntity> records = session.createQuery(criteria).getResultList();
    for (EdgeEntity r : records) {
      vectors.put(
          r,
          session
              .createQuery( // Find all edges that leave node e
                  criteria.select(item).where(builder.equal(item.get("node1"), e)))
              .getResultList());
    }
    session.close();
    return vectors;
  }

  public void exportToCSV(String filename) throws IOException {
    filename += "/edge.csv";

    File csvFile = new File(filename);
    FileWriter fileWriter = new FileWriter(csvFile);
    fileWriter.write("edgeid,node1,node2\n");
    for (EdgeEntity edge : edges) {
      fileWriter.write(
          edge.getEdgeid()
              + ","
              + edge.getNode1().getNodeid()
              + ","
              + edge.getNode2().getNodeid()
              + "\n");
    }
    fileWriter.close();
  }

  public void importFromCSV(String filename) throws FileNotFoundException {
    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    String hql = "delete from EdgeEntity ";
    MutationQuery q = session.createMutationQuery(hql);
    q.executeUpdate();
    edges.clear();
    tx.commit();
    if (filename.length() > 4) {
      if (!filename.substring(filename.length() - 4).equals(".csv")) {
        filename += ".csv";
      }
    } else filename += ".csv";

    File loc = new File(filename);

    tx = session.beginTransaction();
    Scanner read = new Scanner(loc);
    int count = 0;
    read.nextLine();

    while (read.hasNextLine()) {
      String[] b = read.nextLine().split(",");
      session.persist(
          new EdgeEntity(
              session.get(NodeEntity.class, b[1]), session.get(NodeEntity.class, b[2]), b[0]));
      edges.add(session.get(EdgeEntity.class, b[0]));

      count++;
      if (count % 20 == 0) {
        session.flush();
        session.clear();
      }
    }
    tx.commit();
    session.close();
  }

  public void add(EdgeEntity e) {
    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    session.persist(e);
    edges.add(e);
    tx.commit();
    session.close();
  }

  public void delete(String e) {
    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    ListIterator<EdgeEntity> li = edges.listIterator();
    while (li.hasNext()) {
      if (li.next().getEdgeid().equals(e)) {
        li.remove();
      }
    }
    session.remove(session.get(EdgeEntity.class, e));
    tx.commit();
    session.close();
  }

  /**
   * Delete the node and link the edges involving the node back together. It functions by making new
   * edges from the node going to node e to the node going away from node e. Every edge that
   * connects to the node e's edge will be repaired like this.
   *
   * @param e NodeEntity that must be deleted.
   */
  public void collapseNode(NodeEntity e) {
    Session session = getSessionFactory().openSession();
    EdgeEntity newEdge;
    HashMap<EdgeEntity, List<EdgeEntity>> vec = nodeVectors(e);
    Transaction tx = session.beginTransaction();
    for (EdgeEntity n : vec.keySet()) { // n - > e
      List<EdgeEntity> edges = vec.get(n);
      for (EdgeEntity m : edges) { // e - > m
        newEdge = new EdgeEntity(n.getNode1(), m.getNode2());
        System.out.println(newEdge.getEdgeid());
        try {
          add(newEdge);
          delete(m.getEdgeid());
        } catch (Exception exp) {
        }
      }
      delete(n.getEdgeid());
    }
    tx.commit();
    session.close();
  }

  public void update(String s, EdgeEntity obj) {
    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();

    ListIterator<EdgeEntity> li = edges.listIterator();
    while (li.hasNext()) {
      if (li.next().getEdgeid().equals(s)) {
        li.remove();
      }
    }

    session
        .createMutationQuery(
            "UPDATE EdgeEntity edge SET "
                + "edge.node1= '"
                + obj.getNode1()
                + "', edge.node2 = '"
                + obj.getNode2()
                + "', edge.edgeid = '"
                + obj.getNode1().getNodeid()
                + "_"
                + obj.getNode1().getNodeid()
                + "' WHERE edge.edgeid = '"
                + s
                + "'")
        .executeUpdate();
    edges.add(session.get(EdgeEntity.class, s));

    tx.commit();
    session.close();
  }

  public EdgeEntity get(String ID) {

    for (EdgeEntity ser : edges) {
      if (ser.getEdgeid().equals(ID)) return ser;
    }
    return null;
  }

  public List<EdgeEntity> getEdgeOnFloor(String floor) {
    return edges.stream()
        .filter(
            edgeEntity ->
                edgeEntity.getNode1().getFloor().equals(floor)
                    && edgeEntity.getNode2().getFloor().equals(floor))
        .toList();
  }

  /**
   * Find all edges that either start or end with this node
   *
   * @param id node id
   * @return List of edges where node1 or node2 is equal to node of id
   */
  public List<EdgeEntity> nodeConnection(String id) {
    return Stream.concat(edgesFromStart(id).stream(), edgesFromEnd(id).stream()).toList();
  }

  /**
   * Find all edges that start with this node
   *
   * @param startid node id
   * @return List of edges where node1 is equal to node of startid
   */
  public List<EdgeEntity> edgesFromStart(String startid) {
    return edges.stream()
        .filter(edgeEntity -> edgeEntity.getNode1().getNodeid().equals(startid))
        .toList();
  }

  /**
   * Find all edges that end with this node
   *
   * @param endid node id
   * @return List of edges where node2 is equal to node of endid
   */
  public List<EdgeEntity> edgesFromEnd(String endid) {
    return edges.stream()
        .filter(edgeEntity -> edgeEntity.getNode2().getNodeid().equals(endid))
        .toList();
  }

  public static EdgeImpl getInstance() {
    return instance;
  }
}
