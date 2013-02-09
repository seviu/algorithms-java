package com.iphonso.algorithms.datatype;


public class Connection  {
	private Vertex mVertex;
	private int mWeight;
	public Connection(Vertex v, int weight) {
		setVertex(v);
		mWeight = weight;
	}

	public final int getWeight() {
		return mWeight;
	}

	public final Vertex getVertex() {
		return mVertex;
	}

	public final void setVertex(Vertex vertex) {
		this.mVertex = vertex;
	}
}
