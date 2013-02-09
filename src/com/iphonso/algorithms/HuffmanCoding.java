package com.iphonso.algorithms;

import java.io.IOException;
import java.util.*;

import com.iphonso.algorithms.io.BitOutputStream;

public class HuffmanCoding {
	private int distributionTable[];
	private int encodingTable[];
	private int encodingLengthTable[];
	
	private class NodeComparator implements Comparator<Node> {

		@Override
		public int compare(Node o1, Node o2) {
			return o1.weight - o2.weight;
		}
		
	}
	private class Node  {
		public char c;
		public int weight;
		
		public short encodedValue;
		public Node parent;
		public Node child0, child1;
		
		public Node() {
			this.c = 0;
			this.weight = -1;
		}

		public Node(Node c0, Node c1) {
			this();
			child0 = c0;
			child1 = c1;
		}

		public Node(char c, int weight) {
			this.c = c;
			this.weight = weight;
		}
	}
	
	
	public HuffmanCoding() {
		distributionTable = new int[256];
		encodingTable = new int[256];
		encodingLengthTable = new int[256];
	}
	
	public void encode(String e) throws IOException {
		
		System.out.println("Encode " + e.length() + "bytes");
		
		// we first compute the probability for each char to appear
		Arrays.fill(distributionTable, 0);
		Arrays.fill(encodingTable, -1);
		Arrays.fill(encodingLengthTable, -1);
		
		
		for (char i = 0; i < e.length(); i++) {
			distributionTable[e.charAt(i)]++;
		}
		
		// Now, using a priority queue, we insert all the elements in order (lower probability first)
		PriorityQueue<Node> nodes = new PriorityQueue<Node>(255, new NodeComparator());
		for (char i = 0; i < 255; i++) {
			if (distributionTable[i] > 0) {
				Node n = new Node(i, distributionTable[i]);
				nodes.add(n);
			}
		}
		
		if (nodes.size() == 0) {
			return;
		}
		
		Node tree;
		if (nodes.size() == 1) {
			tree = nodes.remove();
		} else {
			Node n0 = nodes.remove();
			Node n1 = nodes.remove();
			tree = new Node(n0, n1);
			while (!nodes.isEmpty()) {
				Node n = nodes.remove();
				tree = new Node(n, tree);
			}
		}
		
		// Now we have the encoding tree, lets make the lookup table;
		int val = 0;
		int length = 0;
		while (tree != null) {
			Node n0 = tree.child0;
			Node n1 = tree.child1;
			
			length++;
			val = val << 1;
			
			if (n0 != null && n0.weight > 0) {
				encodingTable[n0.c] = val;
				encodingLengthTable[n0.c] = length;
			}
			val = val | 1;
			if (n1 != null && n1.weight > 0) {
				encodingTable[n1.c] = val;
				encodingLengthTable[n1.c] = length;
			}
			tree = n1;
		}
		
		for (int i = 0; i < 255; i++) {
			if (encodingTable[i] >= 0) {
				System.out.println("Char " + (char)i + " val " + Integer.toBinaryString(encodingTable[i]) + " length " + encodingLengthTable[i]);
			}
		}
		BitOutputStream bos = new BitOutputStream();
		int iii = 0;
		for (int i = 0; i < e.length(); i++) {
			char c = e.charAt(i);
			int encoded = encodingTable[c] ;
			length = encodingLengthTable[c] ;
			iii += length;
			bos.write(encoded, length);
		}
		bos.flush();
		System.out.println("LENGTH IS " + iii);
		bos.print();
		
	}
	
	public static void main(String args[]) throws IOException {
		HuffmanCoding hc = new HuffmanCoding();
		hc.encode("el perro de san roque no tiene rabo");
	}
	
	
}
