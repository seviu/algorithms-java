package com.iphonso.algorithms;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;


public class Dijkstra {
	/*
	 * Data structure:
	 * Array of edges (from 0 to n)
	 * Edge is a class with:
	 * 		Array of connections
	 * Connection (inner class of Edge):
	 * 		Edge nr.
	 * 		Weight
	 */
	public class Vertex {
		private final ArrayList<Connection> mConnections = new ArrayList<Connection>();
		private boolean mVisited, mProcessed;
		private int mWeight;
		private final int mNr;
		private Vertex mParent;

		public Vertex(int nr) {
			mWeight = Integer.MAX_VALUE;
			mNr = nr;
		}
		
		public int getNr() {
			return mNr;
		}

		public int getWeight() {
			return mWeight;
		}

		public void setWeight(int weight) {
			this.mWeight = weight;
		}

		public ArrayList<Connection> getConnections() {
			return mConnections;
		}

		public boolean isVisited() {
			return mVisited;
		}

		public void setVisited(boolean visited) {
			this.mVisited = visited;
		}

		public void setParent(Vertex currentVertex) {
			mParent = currentVertex;
		}

		public Vertex getParent() {
			return mParent;
		}

	}

	public class Connection  {
		Vertex vertex;
		int mWeight;
		public Connection(Vertex v, int weight) {
			vertex = v;
			mWeight = weight;
		}

		public int getWeight() {
			return mWeight;
		}
	}
	
	public final Vertex vertexs[]; // Our graph is a list of edges at the end
	private final int mOriginIndex, mEndIndex;
	private final int mEdgesNr, mVertexNr;
	
	/*
	 * Structure vertex and edges
	 * O D (give me the shortest path from origin vertex to destiny vertex)
	 * K N (k edges n vertex)
	 * K times:
	 * E1 E2 W (Edge, Edge, Weight of the path)
	 * i.e.
	 * shortest path from 1 to 6
	 * 1 6 
	 * 5 6
	 * 1 2 1
	 * 2 3 2
	 * 3 4 5
	 * 4 6 2
	 * 2 6 3
	 * 
	 */
	public Dijkstra(InputStream input) {
		Scanner s = new Scanner(input);
		
		mOriginIndex = s.nextInt() - 1;
		mEndIndex = s.nextInt() - 1;
		mEdgesNr = s.nextInt();
		mVertexNr = s.nextInt();
		vertexs = new Vertex[mVertexNr];
		for (int i = 0; i < mVertexNr; i++) {
			vertexs[i] = new Vertex(i);
		}
		
		for (int i = 0; i < mEdgesNr; i++) {
			int e1, e2, w;
			e1 = s.nextInt() - 1;
			e2 = s.nextInt() - 1;
			w = s.nextInt();
			Connection c = new Connection(vertexs[e2], w);
			vertexs[e1].getConnections().add(c);
			
			// Bidirectional graph
			c = new Connection(vertexs[e1], w);
			vertexs[e2].getConnections().add(c);
		}
		s.close();
		Vertex initVertex = vertexs[mOriginIndex];
		initVertex.setWeight(0);
		findShortestPath(initVertex);
	}

	public void findShortestPath(Vertex currentVertex) {
		if (currentVertex == null || currentVertex.isVisited()) {
			return;
		}
		currentVertex.setVisited(true);
		for (int i = 0; i < currentVertex.getConnections().size(); i++) {
			Connection c = currentVertex.getConnections().get(i);
			Vertex visitedVertex = c.vertex;
			int weight = currentVertex.getWeight() + c.getWeight();

			if (currentVertex.getNr() == 7) {
				int a = 0;
				a++;
			}
			if (visitedVertex.getWeight() > weight) {
				visitedVertex.setWeight(weight);
				visitedVertex.setParent(currentVertex);
			}
			System.out.println("Visiting ("+ (currentVertex.getNr()+1) + "," + (visitedVertex.getNr()+1) + ") - (" 
					+ currentVertex.getWeight() + "," + visitedVertex.getWeight() +")" );
		}

		Vertex minWeightNonVisitedVertex = null;
		for (int i = 0; i < mVertexNr; i++) {
			Vertex v = vertexs[i];
			if (!v.isVisited() &&
					(minWeightNonVisitedVertex == null ||
					minWeightNonVisitedVertex.getWeight() > v.getWeight())) {
				minWeightNonVisitedVertex = v;
			}
				
		}
		findShortestPath(minWeightNonVisitedVertex);
	}
	// We pass We pass /Users/sebastianvieira/Documents/workspace/Dijkstra/graph1.txt

	public static void main(String[] args) {
		Dijkstra solution = new Dijkstra(System.in);
		System.out.println("Solution:");
		for (int i = 0; i < solution.vertexs.length; i++) {
			Vertex v = solution.vertexs[i];
			if (v.getParent() != null) {
				System.out.println("Vertex:"+ (v.getNr()+1) + " Parent:" + (v.getParent().getNr()+1));
			} else {
				System.out.println("Vertex:"+ (v.getNr()+1) + " Parent:None");
			}
			
		}
	}
}
