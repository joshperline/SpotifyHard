import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/** Spotify Programming Lab: Level "HARD"
 *  "Cats vs Dogs"
 *  @author Joshua Perline
 */

public class CatVsDog {
    
    /** Reads from Standard Input, input files are IGNORED.
     *  Performs N test cases s.t. N = the integer on the first line of input. 
     *  Outputs the maximum number of satisfied voters given the test case.
     *  Treats each case as a Bipartite graph, with votes represented as Vertices
     *  Undirected edges exist to show an unsatisfied voter. */
    public static void main(String[] ignored) { 
	BufferedReader reader =
	     new BufferedReader(new InputStreamReader(System.in));
	int c = 0;
	int d = 0;
	int v = 0;
	resetData();
	_no = new NoLabel();
	try {
	    String read;		
	    int vote = 0;
	    int cases = 0;
	    int currCase = 0;
	    read = reader.readLine();
	    String[] firstLine = read.split("\\s+");
	    cases = Integer.parseInt(firstLine[0]);
	    String ans = "";
	    for (int i = 0; i < cases; i += 1) {
		read = reader.readLine();
		String[] line = read.split("\\s+");
		resetData();
		c = Integer.parseInt(line[0]);
		d = Integer.parseInt(line[1]);
		v = Integer.parseInt(line[2]);
		for (int k = 0; k < v; k += 1) {
		    read = reader.readLine();
		    addToGraph(read);
		}
		createEdges();
		ans += v - hopcroftKarp() + "\n";
	    }
	    System.out.print(ans);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /** Adds vertices to test cases, N's, bipartite graph. */
    private static void addToGraph(String entry) {	
	if (entry.substring(0, 1).equals("C")) {
	    _catVerts.add(_G.add(new Vote(entry)));
	} else {
	    _dogVerts.add(_G.add(new Vote(entry)));
	}
    }

    /** Adds an Undirected edge between vertices whose labels (votes)
     *  are contradictory, thus resulting in at least one, dissatisfied voter. */
    private static void createEdges() {
	for (Graph<Vote, NoLabel>.Vertex cat: _catVerts) {
	    for (Graph<Vote, NoLabel>.Vertex dog: _dogVerts) {
		if (cat.getLabel().isContradiction(dog.getLabel())) {
		    _G.add(cat, dog, _no);
		}
	    }
	}
    }


    /** Solves test cases by performing a Hopcroft-Karp search 
     *  in order to find the maximum number of satisfied voters s.t.
     *  Max(satisfied) = N voters - the total matchings. */
    private static int hopcroftKarp() {
	_nilMap = new HashMap<>();
	dist = new HashMap<>();
	for (Graph<Vote, NoLabel>.Vertex v : _G.vertices()) {
	    dist.put(v, Integer.MAX_VALUE);
	}
	int matching = 0;
	while (BFS()) {
	    for (Graph<Vote, NoLabel>.Vertex cat: _catVerts) {
		if (_nilMap.get(cat) == null) {
		    if (DFS(cat)) {
			matching += 1;
		    }
		}
	    }
	}
	return matching;
    }

    /** Performs BFS on Graph in order to partition the graph to be later
     *  traversed using DFS, as only unmatched edges may be traversed. */
    private static boolean BFS() {
	LinkedList<Graph<Vote, NoLabel>.Vertex> Q =
            new LinkedList<>();
	for (Graph<Vote, NoLabel>.Vertex cat : _catVerts) {
	    if (_nilMap.get(cat) == null) {
		dist.put(cat, 0);
		Q.add(cat);
	    } else {
		dist.put(cat, Integer.MAX_VALUE);
	    }
	}
	dist.put(null, Integer.MAX_VALUE);
	while (!Q.isEmpty()) {
	    Graph<Vote, NoLabel>.Vertex v = Q.poll();
	    if (v != null) {
		for (Graph<Vote, NoLabel>.Vertex u : _G.successors(v)) {
		    if (dist.get(_nilMap.get(u)) == Integer.MAX_VALUE) {
			dist.put(_nilMap.get(u), dist.get(v) + 1);
			Q.add(_nilMap.get(u));
		    }
		}
	    }
	}
	return dist.get(null) != Integer.MAX_VALUE;
    }

    /** Performs a DFS on vertex, V, in order to try and find if a
      * a contradictory set of votes exists. */
    private static boolean DFS(Graph<Vote, NoLabel>.Vertex v) {
	if (v != null) {
	    for (Graph<Vote, NoLabel>.Vertex u : _G.successors(v)) {
		if (dist.get(_nilMap.get(u)) == dist.get(v) + 1) {
		    if (DFS(_nilMap.get(u))) {
			_nilMap.put(u, v);
			_nilMap.put(v, u);
			return true;
		    }
		}
	    }
	    dist.put(v, Integer.MAX_VALUE);
	    return false;
	}
	return true;
    }

    /** The VLabel of the bipartite graph.
     *  Has methods for testing and for determining contradictory votes. */
    static class Vote {
	/** Contsrtuctor takes in ID value of the form:
	 *  "Cx Dy" || "Dx Cy" s.t x is a positive integer. */
	Vote(String id) {
	    _id = id;
	}

	/** The ID value of a pet. */
	private String _id;
	
	/** Returns the id of the pet. Useful for testing. */
	String getID() {
	    return _id;
	}

	/** Returns the opposite form of this vote.
	 *  i.e. opposite(C3 D5) --> D5 C3. */
	String getOpposite() {
	    if (_id.substring(0, 1).equals("C")) {
		return ("D" + _id.substring(4, 5)
			+ " " + "C" + _id.substring(1, 2));
	    } else {
	        return ("C" + _id.substring(4, 5)
			+ " " + "D" + _id.substring(1, 2));
	    }
	}

	/** Returns true iff VOTE */
	boolean isContradiction(Vote vote) {
	    return this.getID().equals(vote.getOpposite());
	}
    }
    
    /** Re-instantiates all instance variables and data structures. */
    private static void resetData() {
	_G = new UndirectedGraph<Vote, NoLabel>();
	_catVerts = new ArrayList<>();
	_dogVerts = new ArrayList<>();
	_nilMap = new HashMap<>();
    }

    
    /* ---------------------------- Data Structures ------------------------------ */


    /** The current graph. */
    private static UndirectedGraph<Vote, NoLabel> _G;

    /** Collects Vertices as partitions of the bipartite graph.
     *  _catVerts serves as G1
     *  _dogVerts serves as G2
     *  s.t. G = G1 U G2 U NIL. */
    private static ArrayList<Graph<Vote, NoLabel>.Vertex> _catVerts, _dogVerts;
   
    /** NoLabel placeholder object to be used in lieu of ELabels. */
    private static NoLabel _no;

    /** Determines if Vertex should be traversed over in G1 or G2. 
     *  Aides in representing the NIL partition of G by mapping Vertices to NULL. */
    private static HashMap<Graph<Vote, NoLabel>.Vertex, Graph<Vote, NoLabel>.Vertex>
	_nilMap;

    /** Maps Vertices to its heuristic distance value. */
    private static HashMap<Graph<Vote, NoLabel>.Vertex, Integer> dist;

}
