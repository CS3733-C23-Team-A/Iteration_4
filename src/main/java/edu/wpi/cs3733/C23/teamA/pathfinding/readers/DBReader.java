package edu.wpi.cs3733.C23.teamA.pathfinding.readers;

import edu.wpi.cs3733.C23.teamA.Database.Entities.EdgeEntity;
import edu.wpi.cs3733.C23.teamA.Database.Entities.LocationNameEntity;
import edu.wpi.cs3733.C23.teamA.Database.Entities.NodeEntity;
import edu.wpi.cs3733.C23.teamA.Database.Implementation.EdgeImpl;
import edu.wpi.cs3733.C23.teamA.Database.Implementation.MoveImpl;
import edu.wpi.cs3733.C23.teamA.Database.Implementation.NodeImpl;
import edu.wpi.cs3733.C23.teamA.pathfinding.Graph;
import edu.wpi.cs3733.C23.teamA.pathfinding.GraphNode;
import edu.wpi.cs3733.C23.teamA.pathfinding.enums.LocationType;
import java.sql.SQLException;
import java.util.List;

public class DBReader {

  /**
   * Reads the database using a Hibernate session to populate it with locational nodes and edges
   *
   * @param graph a Graph object
   */
  public static void readDB(Graph graph) throws SQLException {
    // Nodes
    LocationNameEntity locNameEnt;
    MoveImpl moveImpl = new MoveImpl();
    NodeImpl nodeImpl = new NodeImpl();
    List<NodeEntity> allNodes = nodeImpl.getAll(); // gets all the nodes in db's node table

    // loop through all the nodes, adding them to the graph specified
    for (NodeEntity n : allNodes) {
      locNameEnt = moveImpl.mostRecentLoc(n.getNodeid());
      GraphNode g;
      // create the nodes; if there's no LocationNameEntity, it's a node w/ no location attached
      if (locNameEnt != null) {
        g =
            new GraphNode(
                n.getNodeid(),
                n.getXcoord(),
                n.getYcoord(),
                locNameEnt.getLongname(),
                locNameEnt.getLocationtype(),
                n.getFloor());
      } else {
        g =
            new GraphNode(
                n.getNodeid(),
                n.getXcoord(),
                n.getYcoord(),
                "UNNAMED NODE",
                LocationType.UNKN.getTableString(), // what to do here?
                n.getFloor());
      }
      // create the graph and add the nodes (id, xcoord, ycoord, longName, locationType)
      graph.addNode(n.getNodeid(), g);
    }

    // close the sessions related to nodes
    moveImpl.closeSession();
    nodeImpl.closeSession();

    // Edges
    EdgeImpl edgeImpl = new EdgeImpl();

    /* read through edge columns and add edges to correct node (bidirectional) */
    List<EdgeEntity> allEdges =
        edgeImpl.getAll(); // Gets list of all edges from database's edge table
    for (EdgeEntity e : allEdges) {
      GraphNode node1 = graph.getNode(e.getNode1().getNodeid());
      GraphNode node2 = graph.getNode(e.getNode2().getNodeid());
      node1.addNeighbor(node2);
      node2.addNeighbor(node1);
    }

    // close the edge session
    // edgeImpl.closeSession(); // session was null, don't think it's ever started in EdgeImpl
    // anyway
  }
}