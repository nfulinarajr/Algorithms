import java.util.Map;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

/**
 * @author napoleonfulinarajr
 */
public class VectorCover {

	Scanner scan;
	FileWriter writer;
	List<Integer> edges;
	FileReader fileReader;
	boolean graphHasLeaves;
	BufferedReader bufferedReader;
	Map<Integer, List<Integer>> graph;
	int edge, maxEdges, vertexWithMaxEdges;
	String readFile, writeFile, tempContainer;
	ArrayList<String> container1, container2, container3;
	List<Integer> vectorCover, verticesToRemove, children;

	/**
	 * Default Constructor
	 */
	public VectorCover() {
		
		readFile = "graph.txt";
		writeFile = "output_fulinara.txt";
	}

	/**
	 * Method: parse
	 * Description: Read in a file to extract vertices and connecting vertices.
	 * A hash map is created to store vertex id and a list of connecting vertices.
	 */
	public void parse() {
		
		container1 = new ArrayList<String>();
		container2 = new ArrayList<String>();
		container3 = new ArrayList<String>();
		graph = new HashMap<Integer, List<Integer>>();

		try {
			// Read file
			fileReader = new FileReader(readFile);
			bufferedReader = new BufferedReader(fileReader);

			// Delimit file by (x)
			scan = new Scanner(bufferedReader);
			scan.useDelimiter("x");

			while (scan.hasNext()) {
				container1.add(scan.next());
			}

			// Delimit file by (:)
			for (int j = 0; j < container1.size(); j++) {
				tempContainer = container1.get(j);

				scan = new Scanner(tempContainer);
				scan.useDelimiter(":");

				while (scan.hasNext()) {
					container2.add(scan.next());
					container3.add(scan.next());
				}
			}

			// Delimit file by (,)
			for (int k = 0; k < container2.size(); k++) {
				edges = new ArrayList<Integer>();
				tempContainer = container3.get(k);

				scan = new Scanner(tempContainer);
				scan.useDelimiter(",");

				while (scan.hasNext()) {
					edge = Integer.parseInt(scan.next());
					edges.add(edge);
				}
				graph.put(Integer.parseInt(container2.get(k)), edges);
			}

			// Cleanup
			container1.clear();
			container2.clear();
			container3.clear();
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}
	}

	/**
	 * Method: getVectorCover
	 * Description: Finds a reasonable minimum vertex cover.
	 */
	public void getVectorCover() {
		
		vectorCover = new ArrayList<Integer>();
		verticesToRemove = new ArrayList<Integer>();

		while (graph.size() > 0) {

			// Sweep for leaves
			checkForLeaves();

			if (graphHasLeaves == true) {
				for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
					if (entry.getValue().size() == 1) {
						if (!vectorCover.contains(entry.getKey())) {
							int currentVertex = entry.getKey();
							int parentVertex = entry.getValue().get(0);

							// Add parent to vertex cover
							vectorCover.add(parentVertex);

							// Create removal list
							verticesToRemove.add(parentVertex);
							verticesToRemove.add(currentVertex);

							// Remove parent vertex from all children
							for (int i = 0; i < graph.get(parentVertex).size(); i++) {
								for (int j = 0; j < graph.get(graph.get(parentVertex).get(i)).size(); j++) {
									if (graph.get(graph.get(parentVertex).get(i)).get(j) == parentVertex) {
										graph.get(graph.get(parentVertex).get(i)).remove(j);
									}
								}
							}
						}
					}
				}
			} else {

				// Find ideal vertex to remove
				getMaxEdges();
				vectorCover.add(vertexWithMaxEdges);
				verticesToRemove.add(vertexWithMaxEdges);

				for (int i = 0; i < graph.get(vertexWithMaxEdges).size(); i++) {
					for (int j = 0; j < graph.get(graph.get(vertexWithMaxEdges).get(i)).size(); j++) {
						if (graph.get(graph.get(vertexWithMaxEdges).get(i)).get(j) == vertexWithMaxEdges) {
							graph.get(graph.get(vertexWithMaxEdges).get(i)).remove(j);
						}
					}
				}
			}
			cleanup();
		}

		// Write to file
		try {
			writer = new FileWriter(writeFile);
			for (int i = 0; i < vectorCover.size(); i++) {
				writer.write(vectorCover.get(i) + "x");
			}

			writer.close();
		} catch (IOException e) {
			System.out.println(e);
		}

		// Goal: Low 28000's
		System.out.println("Vector Cover: " + vectorCover.size());
	}

	/**
	 * Method: cleanup
	 * Description: Removes all vertices within the verticesToRemove array list.
	 * This method avoids concurrency issues when scanning and removing vertices
	 * from the hash map.
	 */
	public void cleanup() {
		
		for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
			if (entry.getValue().size() == 0) {
				verticesToRemove.add(entry.getKey());
			}
		}

		for (int i = 0; i < verticesToRemove.size(); i++) {
			graph.remove(verticesToRemove.get(i));
		}
		verticesToRemove.clear();
	}

	/**
	 * Method: checkForLeaves
	 * Description: Iterate through the hash map to check
	 * for vertices with 1 connection and update boolean value.
	 */
	public void checkForLeaves() {
		
		graphHasLeaves = false;

		for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
			if (entry.getValue().size() == 1) {
				graphHasLeaves = true;
			}
		}
	}

	/**
	 * Method: getMaxEdges
	 * Description: Determines the optimal vertex that, when deleted,
	 * creates the maximum number of children with a single connection.
	 */
	public void getMaxEdges() {
		
		int counter = 0;
		maxEdges = 0;

		for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
			for (int i = 0; i < entry.getValue().size(); i++) {
				if (graph.get(entry.getValue().get(i)).size() == 2) {
					counter++;
				}
			}
			if (counter > maxEdges) {
				maxEdges = counter;
				vertexWithMaxEdges = entry.getKey();
			}
			counter = 0;
		}
	}

	/**
	 * Method: main
	 * Description: Simple interface that creates a vertex cover
	 * object to find a reasonable minimum vertex cover.
	 */
	public static void main(String[] args) {
		
		VectorCover vc = new VectorCover();

		vc.parse();
		vc.getVectorCover();
	}
}