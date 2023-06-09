import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DistanceVector {

	public final static int infinity = Integer.MAX_VALUE;

	static class Edge {
		private int source;
		private int dest;
		private int cost;

		public Edge(int source, int dest, int cost) {
			this.source = source;
			this.dest = dest;
			this.cost = cost;
		}

		public int getSource() {
			return source;
		}

		public int getDest() {
			return dest;
		}

		public int getCost() {
			return cost;
		}

		public void setSource(int source) {
			this.source = source;
		}

		public void setDest(int dest) {
			this.dest = dest;
		}

		public void setCost(int cost) {
			this.cost = cost;
		}

	}

	static class Graph {
		private int V;
		private List<Edge> edges;

		public Graph(int V) {
			this.V = V;
			edges = new ArrayList<Edge>();
		}

		public int getNodes() {
			return V;
		}

		public void setNodes(int V) {
			this.V = V;
		}

		public List<Edge> getEdges() {
			return edges;
		}
		
		public int getTotalEdges() {
			return edges.size();
		}

		public void setEdges(List<Edge> edges) {
			this.edges = edges;
		}

		public void addEdge(int source, int dest, int cost) {
			Edge edge = new Edge(source, dest, cost);
			Edge back = new Edge(dest, source, cost);
			edges.add(edge);
			edges.add(back);
		}
		
		public void removeEdge(int node, int source, int dest) {
			if((edges.get(node).getSource() == source && edges.get(node).getDest() == dest) || (edges.get(node).getSource() == dest && edges.get(node).getDest() == source)) {
				edges.remove(node);
				edges.remove(node);
			}
		}

	}

	public static void main(String[] args) throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
		System.setOut(out);
		Graph g = createGraph();
		printGraph(g);
		g = changeGraph(g);

	}

	private static Graph createGraph() throws IOException {
		Path path = Paths.get("topofile");
		int v = (int) Files.lines(path).count() + 1;
		Graph g = new Graph(v);
		File topofile = new File("topofile");
		Scanner read = new Scanner(topofile);
		while(read.hasNextLine()) {
			String line = read.nextLine();
			String[] data = line.split(" ", 3);
			int source = Integer.parseInt(data[0]);
			int dest = Integer.parseInt(data[1]);
			int cost = Integer.parseInt(data[2]);
			g.addEdge(source, dest, cost);
		}
		read.close();
		return g;
	}

	private static Graph changeGraph(Graph g) throws FileNotFoundException {
		File changesfile = new File("changesfile");
		Scanner read = new Scanner(changesfile);
		while(read.hasNextLine()) {
			String line = read.nextLine();
			String[] data = line.split(" ", 3);
			int source = Integer.parseInt(data[0]);
			int dest = Integer.parseInt(data[1]);
			int cost = Integer.parseInt(data[2]);
			if(cost == -999) {
				for(int i = 0; i < g.getTotalEdges(); i++)
					g.removeEdge(i, source, dest);
			} else {
				g.addEdge(source, dest, cost);
			}
			printGraph(g);
		}
		read.close();
		return g;
	}

	public static void printGraph(Graph g) throws FileNotFoundException {
		String hops = "";
		File messagefile = new File("messagefile");
		Scanner read = new Scanner(messagefile);
		int bestCost[][] = new int[g.getNodes()][g.getNodes()];
		for(int node = 1; node < bestCost.length; node++) {
			getShortestPaths(g, node, bestCost[node]);
			for(int i = 1; i < bestCost.length; i++) {
				if(bestCost[node][i] == infinity)
					continue;
				int nexthop = 0;
				if(bestCost[node][i] == 0)
					nexthop = i;
				//System.out.println(i + " " + nexthop + " " + bestCost[node][i]);
				System.out.println(i + " " + bestCost[node][i]);
			}
		}
		while(read.hasNextLine()) {
			String line = read.nextLine();
			String[] data = line.split(" ", 3);
			int source = Integer.parseInt(data[0]);
			int dest = Integer.parseInt(data[1]);
			String printCost = "" + bestCost[source][dest];
			if(bestCost[source][dest] == infinity) {
				printCost = "infinite";
				hops = "unreachable";
			} else {
				hops = "";
			}
			System.out.println("from " + source + " to " + dest + " cost " + printCost + " hops " + hops + " message " + data[2]);
		}
		read.close();

	}

	public static void getShortestPaths(Graph g, int source, int[] cost) {
		int V = g.getNodes();
		for(int i = 1; i < V; i++) {
			cost[i] = infinity;
		}
		cost[source] = 0;
		for(int i = 1; i < V; i++) {
			for(Edge edge: g.getEdges()) {
				int src = edge.getSource(), dst = edge.getDest(), cst = edge.getCost();
				if(cost[src] != infinity && cost[dst] > cost[src] + cst) {
					cost[dst] = cost[src] + cst;
				}
			}
		}
	}

}
