import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.StringTokenizer;


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
        try {
	    BufferedReader reader =
		new BufferedReader(new InputStreamReader(System.in));
	    StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
	    final int cases = Integer.parseInt(tokenizer.nextToken());
	    for (int i = 0; i < cases; i += 1) {
		tokenizer = new StringTokenizer(reader.readLine());
		tokenizer.nextToken();
	        tokenizer.nextToken();
		int v = Integer.parseInt(tokenizer.nextToken());
		resetData(v);
		for (int k = 0; k < v; k += 1) {
		    Vote vote = new Vote(reader.readLine());
		    votes[k] = vote;
		}
		createGraph();
		System.out.println(v - hopcroftKarp());
	    }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Adds an undirected edge between vertices whose labels (VOTES)
     *  are contradictory, thus resulting in at least one, dissatisfied voter. */
    private static void createGraph(){
        for (int i = 0; i < votes.length; i += 1) {
            Vote vote = votes[i];
            for (int k = i + 1; k < votes.length; k += 1) {
                Vote nextVote = votes[k];
                if (vote.isContradiction(nextVote)){
		    vote.addEdge(nextVote);
                    nextVote.addEdge(vote);
		    if (vote.isCatLover()) {
                        catLovers.add(vote);
                    } else {
                        catLovers.add(nextVote);
                    }
                    dist.put(vote, Integer.MAX_VALUE);
                    dist.put(nextVote, Integer.MAX_VALUE);
                }
            }
        }
    }
    
    /** Solves test cases by performing a Hopcroft-Karp search 
     *  in order to find the maximum number of satisfied voters s.t.
     *  Max(satisfied) = N voters - the total matchings. */
    static int hopcroftKarp(){
        int matching = 0;
        while (bredthFirstSearch()) {
            for (Vote v : catLovers) {
                if (pairs.get(v) == null) {
                    if (depthFirstSearch(v)) {
                        matching += 1;
                    }
                }
            }
        }
        return matching;
    }

    /** Performs BFS on Graph in order to partition the graph to be later
     *  traversed using DFS, as only unmatched edges may be traversed. */
    static boolean bredthFirstSearch() {
        Queue<Vote> Q = new LinkedList<>();
        for (Vote v : catLovers) {
            if (pairs.get(v) == null) {
                dist.put(v, 0);
                Q.add(v);
            } else {
                dist.put(v, Integer.MAX_VALUE);
            }
        }
        dist.put(null, Integer.MAX_VALUE); 
        while (!Q.isEmpty()) {
            Vote v = Q.poll();
            if (v != null) {
                for (Vote u : v.getEdges()) {
                    if (dist.get(pairs.get(u)) == Integer.MAX_VALUE) {
                        dist.put(pairs.get(u), dist.get(v) + 1);
                        Q.add(pairs.get(u));
                    }
                }
            }
        }
        return dist.get(null) != Integer.MAX_VALUE;
    }
    
    
    /** Performs a DFS on vertex, V, in order to try and find if a
      * a contradictory set of votes exists. */
    static boolean depthFirstSearch(Vote v) {
        if (v != null) {
            for (Vote u : v.getEdges()) {                
                if (dist.get(pairs.get(u)) == dist.get(v) + 1) {
                    if (depthFirstSearch(pairs.get(u))) {
                        pairs.put(v, u);
                        pairs.put(u, v);
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

	/** Takes in a LINE and finds the pet the voter wants to KEEP
	 *  and a pet the voter wants to THROWAWAY. */
	Vote(String line) {
	    StringTokenizer t = new StringTokenizer(line);
	    this.keep = t.nextToken();
	    this.throwAway = t.nextToken();
            this.edges = new ArrayList<Vote>();
        }

	/** Returns true iff VOTE2 is contradicts this vote. */
	boolean isContradiction(Vote vote2) {
	    return (this.getKeep().equals(vote2.getThrowAway())
		    || vote2.getKeep().equals(this.getThrowAway()));
	}

	/** Returns true iff this voter wants to keep a cat on the show. */
	boolean isCatLover() {
	    return (this.getKeep().charAt(0) == 'C');
	}

	/** Returns the pet this voter wants to keep. */
        String getKeep() {
            return keep;
        }

	/** Returns the pet this voter wants to vote out. */
        String getThrowAway() {
            return throwAway;
        }
        
	/** Connects this Vote in G1 to a Vote in G2 iff the votes
	 *  are contradictory. */
	void addEdge(Vote vote){
            this.edges.add(vote);
        }    
        
	/** Returns the Votes at the end of the edges from this Vote. */
	ArrayList<Vote> getEdges(){
            return edges;
        }

	/** Pet this voter wants to keep. */
        private String keep;
	
	/** Pet this voter wants to vote out. */
        private String throwAway;
	
	/** List of edges from this vertex in the bipartite graph. */
        private ArrayList<Vote> edges;

    }

    /* ---------------------------- Data Structures ------------------------------ */


    /** Re-instantiates all instance variables and data structures. */
    private static void resetData(int v) {
	votes = new Vote[v];
	catLovers = new HashSet<Vote>();
        pairs = new HashMap<Vote, Vote>();
        dist = new HashMap<Vote, Integer>();
    }

    /** Serves as the bipartite graph G = G1 U G2 U NIL. 
     *  G1 is the partition of voters who are cat lovers.
     *  G2 is the partition of voters who are dog lovers. */
    private static Vote[] votes;

    /** Determines if Vertex should be traversed over in G1 or G2. 
     *  Aides in representing the NIL partition of G by mapping Vertices to NULL. */
    private static HashMap<Vote, Vote> pairs;

    /** Maps Vertices to its heuristic distance value. */
    private static HashMap<Vote, Integer> dist;

    /** G = All Votes, G1 = catLovers. */
    private static HashSet<Vote> catLovers;

}
