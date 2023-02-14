package edu.wpi.cs3733.C23.teamA.pathfinding;

import edu.wpi.cs3733.C23.teamA.Database.Entities.EdgeEntity;
import edu.wpi.cs3733.C23.teamA.Database.Entities.NodeEntity;
import edu.wpi.cs3733.C23.teamA.Database.Implementation.EdgeImpl;
import edu.wpi.cs3733.C23.teamA.Database.Implementation.NodeImpl;
import edu.wpi.cs3733.C23.teamA.pathfinding.readers.CSVReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;

public class Graph {
  @Getter private HashMap<String, GraphNode> graph; // NO SETTER!

  // Constructor
  public Graph() {
    // initialize a new empty <String, GraphNode> HashMap
    graph = new HashMap<>();
  }

  /**
   * Adds a node to the Graph's HashMap using the key (nodeID) and value (node) given. NOTE: this
   * will only be needed if the prepGraph_() functions remain outside of this class
   *
   * @param nodeID the ID of the node you want to add (key)
   * @param node the node you want to add (value)
   */
  public void addNode(String nodeID, GraphNode node) {
    graph.put(nodeID, node);
  }

  /**
   * @param key the key (nodeID) for the value (node) wanted
   * @return the GraphNode that corresponds to the given key
   */
  public GraphNode getNode(String key) {
    return graph.get(key);
  }

  /**
   * Resets the following properties of the GraphNodes inside the Graph: visited, parent,
   * costFromStart, heurCostToEnd, score
   */
  public void reset() {
    // iterate through the VALUES of the map (we don't need to edit the keys)
    for (GraphNode gNode : graph.values()) {
      // reset path stuff
      gNode.setVisited(false);
      gNode.setParent(null);

      // reset score stuff
      gNode.setCostFromStart(Double.POSITIVE_INFINITY);
      gNode.setHeurCostToEnd(Double.POSITIVE_INFINITY);
      gNode.setScore(Double.POSITIVE_INFINITY);
    }
  }

  /**
   * Creates GraphNodes from the Nodes in DB and makes a graph from them.
   *
   * @throws SQLException
   */
  public void prepGraphDB() throws SQLException {
    NodeImpl nodeI = new NodeImpl();
    EdgeImpl edgeI = new EdgeImpl();
    List<NodeEntity> nodes = nodeI.getAll();
    List<EdgeEntity> edges = edgeI.getAll();
    nodes.forEach(
        node ->
            addNode(
                node.getNodeid(),
                new GraphNode(
                    node.getNodeid(),
                    node.getXcoord(),
                    node.getYcoord(),
                    node.getFloor(),
                    node.getBuilding())));
    edges.forEach(
        edge -> {
          GraphNode node1 = graph.get(edge.getNode1().getNodeid());
          GraphNode node2 = graph.get(edge.getNode2().getNodeid());
          node1.addNeighbor(node2);
          node2.addNeighbor(node1);
        });
  }

  /**
   * Creates GraphNodes from nodes.csv and connects them in a graph using edges.csv.
   *
   * @throws IOException if the files cannot be read
   */
  public void prepGraphCSV() throws IOException {
    // add the CSV information to the graph
    CSVReader.readNodes("src/main/resources/edu/wpi/cs3733/C23/teamA/mapCSV/nodes.csv", this);
    CSVReader.readEdges("src/main/resources/edu/wpi/cs3733/C23/teamA/mapCSV/edges.csv", this);
  }
}
