import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

/** Do not add or remove public or protected members, or modify the sigs of
 * any public methods.  You may make methods in Graph abstract, if you want
 * different implementations in DirectedGraph and UndirectedGraph.  You may
 * add bodies to abstract methods, modify existing bodies, or override
 * inherited methods.
 * @author Joshua Perline */

public abstract class Graph<VLabel, ELabel> {

    /** Represents one of my vertices. */
    public class Vertex {

        /** A new vertex with LABEL as the value of getLabel(). */
        Vertex(VLabel label) {
            _label = label;
        }

        /** Returns the label on this vertex. */
        public VLabel getLabel() {
            return _label;
        }

        /** The Fscore of this vertex. */
        private double _f;

        /** Set the current fScore F. */
        void setF(double f) {
            _f = f;
        }

        /** Returns the Vertex's fscore. */
        double getF() {
            return _f;
        }

        @Override
        public String toString() {
            return String.valueOf(_label);
        }

        /** The label on this vertex. */
        private final VLabel _label;

    }

    /** Represents one of my edges. */
    public class Edge {

        /** An edge (V0,V1) with label LABEL.  It is a directed edge (from
         *  V0 to V1) in a directed graph. */
        Edge(Vertex v0, Vertex v1, ELabel label) {
            _label = label;
            _v0 = v0;
            _v1 = v1;
        }

        /** Returns the label on this edge. */
        public ELabel getLabel() {
            return _label;
        }

        /** Return the vertex this edge exits. For an undirected edge, this is
         *  one of the incident vertices. */
        public Vertex getV0() {
            return _v0;
        }

        /** Return the vertex this edge enters. For an undirected edge, this is
         *  the incident vertices other than getV1(). */
        public Vertex getV1() {
            return _v1;
        }

        /** Returns the vertex at the other end of me from V.  */
        public final Vertex getV(Vertex v) {
            if (v == _v0) {
                return _v1;
            } else if (v == _v1) {
                return _v0;
            } else {
                throw new
                    IllegalArgumentException("vertex not incident to edge");
            }
        }

        @Override
        public String toString() {
            return String.format("(%s,%s):%s", _v0, _v1, _label);
        }

        /** Endpoints of this edge.  In directed edges, this edge exits _V0
         *  and enters _V1. */
        private final Vertex _v0, _v1;

        /** The label on this edge. */
        private final ELabel _label;

    }

    /*=====  Methods and variables of Graph =====*/

    /** All relevant information for the graph data structure. */
    protected HashMap<Vertex, ArrayList<Edge>> _graphMap
        = new HashMap<>();

    /** Returns the number of vertices in me. */
    public int vertexSize() {
        return _graphMap.size();
    }

    /** Returns the number of edges in me. */
    public int edgeSize() {
        Iterator it = _graphMap.entrySet().iterator();
        int count = 0;
        for (ArrayList<Edge> e: _graphMap.values()) {
            count += e.size();
        }
        return count / 2;
    }

    /** Returns true iff I am a directed graph. */
    public abstract boolean isDirected();

    /** Returns the number of outgoing edges incident to V. Assumes V is one of
     *  my vertices.  */
    public int outDegree(Vertex v) {
        int deg = 0;
        for (ArrayList<Edge> e: _graphMap.values()) {
            for (int i = 0; i < e.size(); i += 1) {
                if (e.get(i).getV0() == v) {
                    deg += 1;
                }
            }
        }
        return deg / 2;
    }

    /** Returns the number of incoming edges incident to V. Assumes V is one of
     *  my vertices. */
    public int inDegree(Vertex v) {
        int deg = 0;
        for (ArrayList<Edge> e: _graphMap.values()) {
            for (int i = 0; i < e.size(); i += 1) {
                if (e.get(i).getV1() == v) {
                    deg += 1;
                }
            }
        }
        return deg / 2;
    }

    /** Returns outDegree(V). This is simply a synonym, intended for
     *  use in undirected graphs. */
    public final int degree(Vertex v) {
        return outDegree(v);
    }
    /** Returns true iff there is an edge (U, V) in me with any label. */
    public boolean contains(Vertex u, Vertex v) {
        if (u != v) {
            if (isDirected()) {
                for (Edge e: _graphMap.get(u)) {
                    for (Edge e2: _graphMap.get(v)) {
                        if (e == e2 && e.getV0() == u
                            && e.getV1() == v) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /** Returns true iff there is an edge (U, V) in me with label LABEL. */
    public boolean contains(Vertex u, Vertex v,
                            ELabel label) {
        if (u != v) {
            if (isDirected()) {
                for (Edge e: _graphMap.get(u)) {
                    for (Edge e2: _graphMap.get(v)) {
                        if (e == e2 && e.getV0() == u
                            && e.getV1() == v) {
                            if (e.getLabel() == label
                                && e2.getLabel() == label) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /** Returns a new vertex labeled LABEL, and adds it to me with no
     *  incident edges. */
    public Vertex add(VLabel label) {
        Vertex v = new Vertex(label);
        ArrayList<Edge> empty = new ArrayList<Edge>();
        _graphMap.put(v, empty);
        return v;
    }

    /** Returns an edge incident on FROM and TO, labeled with LABEL
     *  and adds it to this graph. If I am directed, the edge is directed
     *  (leaves FROM and enters TO). */
    public Edge add(Vertex from,
                    Vertex to,
                    ELabel label) {
        Edge e = new Edge(from, to, label);
        if (_graphMap.containsKey(from)) {
            _graphMap.get(from).add(e);
        } else {
            throw new IllegalArgumentException("ERROR: FROM Vertex"
                                               + " doesn't exist.");
        }
        if (_graphMap.containsKey(to)) {
            _graphMap.get(to).add(e);
        } else {
            throw new IllegalArgumentException("ERROR: TO Vertex"
                                               + " doesn't exist.");
        }
        _edges.add(e);
        return e;
    }

    /** A list of all of my edges. */
    protected ArrayList<Edge> _edges = new ArrayList<Edge>();

    /** Returns an edge incident on FROM and TO with a null label
     *  and adds it to this graph. If I am directed, the edge is directed
     *  (leaves FROM and enters TO). */
    public Edge add(Vertex from,
                    Vertex to) {
        Edge e = new Edge(from, to, null);
        if (_graphMap.containsKey(from)) {
            _graphMap.get(from).add(e);
        } else {
            throw new IllegalArgumentException("ERROR: FROM Vertex"
                                               + " doesn't exist.");
        }
        if (_graphMap.containsKey(to)) {
            _graphMap.get(to).add(e);
        } else {
            throw new IllegalArgumentException("ERROR: TO Vertex"
                                               + " doesn't exist.");
        }
        return e;
    }

    /** Remove V and all adjacent edges, if present. */
    public void remove(Vertex v) {
        ArrayList<Edge> edges = _graphMap.remove(v);
        for (Edge e: edges) {
            _graphMap.get(e.getV(v)).remove(e);
            _edges.remove(e);
        }
    }

    /** Remove E from me, if present.  E must be between my vertices,
     *  or the result is undefined.  */
    public void remove(Edge e) {
        for (Vertex v : _graphMap.keySet()) {
            if (_graphMap.get(v).contains(e)) {
                _graphMap.get(v).remove(e);
                _edges.remove(e);
            }
        }
    }

    /** Remove all edges from V1 to V2 from me, if present.  The result is
     *  undefined if V1 and V2 are not among my vertices.  */
    public void remove(Vertex v1, Vertex v2) {
        if (_graphMap.containsKey(v1)
            && _graphMap.containsKey(v2)) {
            for (int i = _graphMap.get(v1).size() - 1; i > -1; i++) {
                if (_graphMap.get(v1).get(i).getV(v1) == v2) {
                    _graphMap.get(v1).remove(_graphMap.get(v1).get(i));
                    _edges.remove(_graphMap.get(v1).get(i));
                }
            }
        }
    }


    /** Returns an Iterator over all vertices in arbitrary order. */
    public Iteration<Vertex> vertices() {
        Iterator<Vertex> vertexIter =
            _graphMap.keySet().iterator();
        return Iteration.iteration(vertexIter);
    }

    /** Returns an iterator over all successors of V. */
    public Iteration<Vertex> successors(Vertex v) {
        ArrayList<Vertex> success = new ArrayList<Vertex>();
        for (int i = 0; i < _graphMap.get(v).size(); i += 1) {
            if (_graphMap.get(v).get(i).getV0() == v) {
                success.add(_graphMap.get(v).get(i).getV1());
            }
        }
        Iterator<Vertex> successIter = success.iterator();
        return Iteration.iteration(successIter);
    }

    /** Returns an iterator over all predecessors of V. */
    public Iteration<Vertex> predecessors(Vertex v) {
        ArrayList<Vertex> success = new ArrayList<Vertex>();
        for (int i = 0; i < _graphMap.get(v).size(); i += 1) {
            if (_graphMap.get(v).get(i).getV1() == v) {
                success.add(_graphMap.get(v).get(i).getV0());
            }
        }
        Iterator<Vertex> successIter = success.iterator();
        return Iteration.iteration(successIter);
    }

    /** Returns successors(V).  This is a synonym typically used on
     *  undirected graphs. */
    public final Iteration<Vertex> neighbors(Vertex v) {
        return successors(v);
    }

    /** Returns an iterator over all edges in me. */
    public Iteration<Edge> edges() {
        Iterator<Edge> edgeIter = _edges.iterator();
        return Iteration.iteration(edgeIter);
    }

    /** Returns iterator over all outgoing edges from V. */
    public Iteration<Edge> outEdges(Vertex v) {
        ArrayList<Edge> out = new ArrayList<Edge>();
        for (int i = 0; i < _graphMap.get(v).size(); i += 1) {
            if (_graphMap.get(v).get(i).getV0() == v) {
                out.add(_graphMap.get(v).get(i));
            }
        }
        Iterator<Edge> outIter = out.iterator();
        return Iteration.iteration(outIter);
    }

    /** Returns iterator over all incoming edges to V. */
    public Iteration<Edge> inEdges(Vertex v) {
        ArrayList<Edge> in = new ArrayList<Edge>();
        for (int i = 0; i < _graphMap.get(v).size(); i += 1) {
            if (_graphMap.get(v).get(i).getV1() == v) {
                in.add(_graphMap.get(v).get(i));
            }
        }
        Iterator<Edge> inIter = in.iterator();
        return Iteration.iteration(inIter);
    }

    /** Returns outEdges(V). This is a synonym typically used
     *  on undirected graphs. */
    public final Iteration<Edge> edges(Vertex v) {
        return outEdges(v);
    }

    /** Returns the natural ordering on T, as a Comparator.  For
     *  example, if intComp = Graph.<Integer>naturalOrder(), then
     *  intComp.compare(x1, y1) is <0 if x1<y1, ==0 if x1=y1, and >0
     *  otherwise. */
    public static <T extends Comparable<? super T>> Comparator<T> naturalOrder()
    {
        return new Comparator<T>() {
            @Override
            public int compare(T x1, T x2) {
                return x1.compareTo(x2);
            }
        };
    }

    /** Cause subsequent calls to edges() to visit or deliver
     *  edges in sorted order, according to COMPARATOR. Subsequent
     *  addition of edges may cause the edges to be reordered
     *  arbitrarily.  */
    public void orderEdges(Comparator<ELabel> comparator) {
        final Comparator<ELabel> order = comparator;
        Comparator<Graph<VLabel, ELabel>.Edge> comp
            = new Comparator<Graph<VLabel, ELabel>.Edge>() {
                public int compare(Graph<VLabel, ELabel>.Edge e1,
                                   Graph<VLabel, ELabel>.Edge e2) {
                    return order.compare(e1.getLabel(), e2.getLabel());
                }
            };
        Collections.sort(_edges, comp);
    }

}
