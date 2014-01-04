import java.util.Iterator;
import java.util.ArrayList;

/** An undirected graph with vertices labeled with VLABEL and edges
 *  labeled with ELABEL.
 *  @author Joshua Perline
 */
public class UndirectedGraph<VLabel, ELabel> extends Graph<VLabel, ELabel> {

    /** An empty graph. */
    public UndirectedGraph() {
    }

    @Override
    public boolean isDirected() {
        return false;
    }


/** Returns true iff there is an edge (U, V) in me with any label. */
    @Override
    public boolean contains(Vertex u, Vertex v) {
        if (u != v) {
            for (Edge e: _graphMap.get(u)) {
                for (Edge e2: _graphMap.get(v)) {
                    if (e == e2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Returns true iff there is an edge (U, V) in me with label LABEL. */
    @Override
    public boolean contains(Vertex u, Vertex v,
                            ELabel label) {
        if (u != v) {
            for (Edge e: _graphMap.get(u)) {
                for (Edge e2: _graphMap.get(v)) {
                    if (e.getLabel() == label
                        && e2.getLabel() == label) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Returns the number of outgoing edges incident to V. Assumes V is one of
     *  my vertices.  */
    @Override
    public int outDegree(Vertex v) {
        return _graphMap.get(v).size();
    }

    /** Returns the number of incoming edges incident to V. Assumes V is one of
     *  my vertices. */
    @Override
    public int inDegree(Vertex v) {
        return outDegree(v);
    }

    @Override
    /** Returns an iterator over all successors of V. */
    public Iteration<Vertex> successors(Vertex v) {
        ArrayList<Vertex> success = new ArrayList<Vertex>();
        for (Edge edge: _graphMap.get(v)) {
            success.add(edge.getV(v));
        }
        Iterator<Vertex> successIter = success.iterator();
        return Iteration.iteration(successIter);
    }

    /** Returns iterator over all outgoing edges from V. */
    @Override
    public Iteration<Edge> outEdges(Vertex v) {
        Iterator<Edge> outIter = _graphMap.get(v).iterator();
        return Iteration.iteration(outIter);
    }

    /** Returns iterator over all incoming edges to V. */
    public Iteration<Edge> inEdges(Vertex v) {
        return edges(v);
    }

}
