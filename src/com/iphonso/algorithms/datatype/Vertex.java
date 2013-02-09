package com.iphonso.algorithms.datatype;

import java.util.ArrayList;

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
	private boolean mVisited;
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
