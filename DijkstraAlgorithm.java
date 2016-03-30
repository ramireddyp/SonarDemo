/**
 * This class will find the shortest distances for the given all nodes using Dijskastra algorithm.
 * 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Vepagada
 */

public class DijkstraAlgorithm {
	private static Graph.Edge[] GRAPH = null;
	private static String start = "";
	public static String end = "";

	public static void main(String[] args) throws NumberFormatException, IOException {
		@SuppressWarnings("unused")
		int full = 0;
		ArrayList<Graph.Edge> al = new ArrayList<Graph.Edge>();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter Total Number of Nodes:");
		full = Integer.parseInt(br.readLine());
		System.out.println("Enter The topology values one by one(Ex: a b 2) in each line and at the end Enter Q:");
		String line = "";
		while (!"Q".equalsIgnoreCase((line = br.readLine().trim()))) {
			String[] entry = line.split(" ");
			al.add(new Graph.Edge(entry[0], entry[1], Integer.parseInt(entry[2])));
		}
		System.out.println("Enter Start Node:");
		start = br.readLine();
		System.out.println("Enter End Node:");
		end = br.readLine();
		GRAPH = new Graph.Edge[al.size()];
		GRAPH = (Graph.Edge[]) al.toArray(GRAPH);
		Graph g = new Graph(GRAPH);
		g.dijkstra(start);
		g.printPath(end);
		// g.printAllPaths();
	}
}

class Graph {
	public Map<String, Integer> m = new TreeMap<String, Integer>();
	private final Map<String, Vertex> graph; // mapping of vertex names to Vertex objects, built from a set of Edges

	/** One edge of the graph (only used by Graph constructor) */
	public static class Edge {
		public final String v1, v2;
		public final int dist;

		public Edge(String v1, String v2, int dist) {
			this.v1 = v1;
			this.v2 = v2;
			this.dist = dist;
		}
	}

	/** One vertex of the graph, complete with mappings to neighbouring vertices */
	public static class Vertex implements Comparable<Vertex> {
		public final String name;
		public int dist = Integer.MAX_VALUE; // MAX_VALUE assumed to be infinity
		public Vertex previous = null;
		public final Map<Vertex, Integer> neighbours = new HashMap<Vertex, Integer>();

		public Vertex(String name) {
			this.name = name;
		}

		private void printPath() {
			if (this == this.previous) {
				System.out.printf("%s", this.name);
			} else if (this.previous == null) {
				System.out.printf("%s(unreached)", this.name);
			} else {
				this.previous.printPath();
				System.out.printf(" -> %s(%d)", this.name, this.dist);
			}
		}

		public int compareTo(Vertex other) {
			return Integer.compare(dist, other.dist);
		}
	}

	/** Builds a graph from a set of edges */
	public Graph(Edge[] edges) {
		graph = new HashMap<String, Vertex>(edges.length);

		// one pass to find all vertices
		for (Edge e : edges) {
			if (!graph.containsKey(e.v1))
				graph.put(e.v1, new Vertex(e.v1));
			if (!graph.containsKey(e.v2))
				graph.put(e.v2, new Vertex(e.v2));
		}

		// another pass to set neighbouring vertices
		for (Edge e : edges) {
			graph.get(e.v1).neighbours.put(graph.get(e.v2), e.dist);
			graph.get(e.v2).neighbours.put(graph.get(e.v1), e.dist); // also do this for an undirected graph
		}

			for (Edge e : edges) {
			if (!m.containsKey(e.v1)) {
				m.put(e.v1, 999);
			}
			if (!m.containsKey(e.v2)) {
				m.put(e.v2, 999);
			}
		}

	}

	/** Runs dijkstra using a specified source vertex */
	public void dijkstra(String startName) {
		m.remove(startName);
		// System.out.println(m);
		if (!graph.containsKey(startName)) {
			System.err.printf("Graph doesn't contain start vertex \"%s\"\n", startName);
			return;
		}

		final Vertex source = graph.get(startName);
		NavigableSet<Vertex> q = new TreeSet<Vertex>();

		// set-up vertices
		for (Vertex v : graph.values()) {
			v.previous = v == source ? source : null;
			v.dist = v == source ? 0 : Integer.MAX_VALUE;
			q.add(v);
		}

		dijkstra(q);
	}

	/** Implementation of dijkstra's algorithm using a binary heap. */
	private void dijkstra(final NavigableSet<Vertex> q) {
		int i = 0, p = 0;
		String s = "";
		Vertex u, v;
		String keys[] = new String[m.size()];
		System.out.println("\n--------------------------------------------------------------------------------------");
		System.out.print("Step" + "\t" + "N" + "\t");
		for (String str : m.keySet()) {
			System.out.print(str + "\t");
			keys[p++] = str;
		}
		System.out.println("\n--------------------------------------------------------------------------------------");
		while (!q.isEmpty()) {

			u = q.pollFirst(); // vertex with shortest distance (first iteration will return source)
			// System.out.print(s=(s+u.name));
			s = (s + u.name);
			if (u.dist == Integer.MAX_VALUE)
				break; // we can ignore u (and any other remaining vertices) since they are unreachable

			// look at distances to each neighbour
			for (Map.Entry<Vertex, Integer> a : u.neighbours.entrySet()) {
				v = a.getKey(); // the neighbour in this iteration

				final int alternateDist = u.dist + a.getValue();
				if (m.get(v.name) != null && m.get(v.name) > alternateDist)
					m.put(v.name, alternateDist);
				if (alternateDist < v.dist) { // shorter path to neighbour found
					q.remove(v);
					v.dist = alternateDist;
					v.previous = u;
					q.add(v);
				}
			}
			m.remove(u.name);
			if (!s.contains(DijkstraAlgorithm.end)) {
				System.out.print((i++) + "\t" + s + "\t");
				
				for (int r = 0; r < keys.length; r++) {
					if (m.containsKey(keys[r]))
						System.out.print(m.get(keys[r]) + "\t");
					else
						System.out.print("\t");
				}

				System.out.println("\n--------------------------------------------------------------------------------------");
			}
		}
	}

	/** Prints a path from the source to the specified vertex */
	public void printPath(String endName) {
		if (!graph.containsKey(endName)) {
			System.err.printf("Graph doesn't contain end vertex \"%s\"\n",	endName);
			return;
		}

		System.out.println("\nFinal Path is :");
		graph.get(endName).printPath();
		System.out.println();
	}

	/**
	 * Prints the path from the source to every vertex (output order is not guaranteed)
	 */
	public void printAllPaths() {
		for (Vertex v : graph.values()) {
			v.printPath();
			System.out.println();
		}
	}
}
