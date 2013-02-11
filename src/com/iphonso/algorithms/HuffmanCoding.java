package com.iphonso.algorithms;

import java.io.IOException;
import java.util.*;
import com.iphonso.algorithms.io.*;

/*
 * todo: intermediate nodes are also part of the encoding table and have to be considered (and put in the queue when discovered)
 */
public class HuffmanCoding {
	private int mDistributionTable[];
	private int mEncodingTable[];
	private int mEncodingLengthTable[];
	private Node mTree;
	
	public class HuffmanCodingData {
		public Node huffmanCodingTree; // the tree used to decode / encode
		public byte[] encodedData; // the data we might want to decode later
		public int trailingBits; // bits at the end of the encoded data array which we do not use
	}
	
	private class NodeComparator implements Comparator<Node> {

		@Override
		public int compare(Node o1, Node o2) {
			return o1.weight - o2.weight;
		}
		
	}
	private class Node  {
		public char c;
		public int weight; // weight is its weight or the sum of the weight of the two children (for an intermediate node)
		public boolean isIntermediateNode;
		
		public Node child0, child1;
		
		public Node() {
			this.c = 0;
			this.weight = 0;
			this.isIntermediateNode = true;
		}

		public Node(Node c0, Node c1) {
			this();
			child0 = c0;
			child1 = c1;
			this.weight = c0.weight + c1.weight;
		}

		public Node(char c, int weight) {
			this.c = c;
			this.weight = weight;
			this.isIntermediateNode = false;
		}
	}
	
	
	public HuffmanCoding() {
		mDistributionTable = new int[256];
		mEncodingTable = new int[256];
		mEncodingLengthTable = new int[256];
	}
	
	
	public String decode(HuffmanCodingData data) {
		final int bmask[] = {1,2,4,8,16,32,64,128};
		
		Node huffmanCodingTree = data.huffmanCodingTree;
		byte[] info = data.encodedData;
		int trailingBits = data.trailingBits;
		
		StringBuffer ret = new StringBuffer(); // maybe an output stream would be better
		Node currentNode = huffmanCodingTree; // current node is our state right now
		for (int i = 0; i < info.length; i++) {
			byte b = info[i];
			int bitsToIgnore = 0;
			if (i == (info.length-1)) {
				bitsToIgnore = trailingBits;
			}
			for (int j = 7; j >= bitsToIgnore; j--) {
				int masked = b & bmask[j];
				if (masked != 0) {
					currentNode = currentNode.child1;
				} else {
					currentNode = currentNode.child0;
				}
				if (!currentNode.isIntermediateNode) {
					ret.append(currentNode.c);
					currentNode = huffmanCodingTree;
				}
			}
		}
		return ret.toString();
	}
	
	private void buildHuffmanCodingTable(Node tree, int val, int length) {
		if (tree == null) {
			return;
		}
		if (!tree.isIntermediateNode) {
			mEncodingTable[tree.c] = val;
			mEncodingLengthTable[tree.c] = length;
		}

		Node n0 = tree.child0;
		Node n1 = tree.child1;
		length++;
		val = val << 1;
		buildHuffmanCodingTable(n0, val, length);
		val = val | 1;
		buildHuffmanCodingTable(n1, val, length);
	}

	public HuffmanCodingData encode(String e) throws IOException {
		
		System.out.println("Encode " + e.length() + "bytes");
		
		// we first compute the probability for each char to appear
		Arrays.fill(mDistributionTable, 0);
		Arrays.fill(mEncodingTable, -1);
		Arrays.fill(mEncodingLengthTable, -1);
		
		
		for (char i = 0; i < e.length(); i++) {
			mDistributionTable[e.charAt(i)]++;
		}
		
		// Now, using a priority queue, we insert all the elements in order (lower probability first)
		PriorityQueue<Node> nodes = new PriorityQueue<Node>(255, new NodeComparator());
		for (char i = 0; i < 255; i++) {
			if (mDistributionTable[i] > 0) {
				Node n = new Node(i, mDistributionTable[i]);
				nodes.add(n);
			}
		}
		
		if (nodes.size() == 0) {
			return null;
		}
		
		Node tree = null;
		if (nodes.size() == 1) {
			tree = nodes.remove();
		} else {
			while (!nodes.isEmpty()) {
				Node n0 = nodes.remove();
				if (!nodes.isEmpty()) {
					Node n1 = nodes.remove();
					tree = new Node(n0, n1);
					nodes.add(tree);
				} else {
					tree = n0;
				}
			}
		}
		
		// It is probably not a good idea to get rid of the tree
		mTree = tree; // lets copy it (we want to decode sometimes)
		buildHuffmanCodingTable(tree, 0, 0);

		// Lets print the lookup table values for posterity
		for (int i = 0; i < 255; i++) {
			if (mEncodingTable[i] >= 0) {
				System.out.println("Char " + (char)i + " val " + Integer.toBinaryString(mEncodingTable[i]) + " length " + mEncodingTable[i]);
			}
		}
		
		// We use the lookup table to encode the whole thing
		BitOutputStream bos = new BitOutputStream();
		for (int i = 0; i < e.length(); i++) {
			char c = e.charAt(i);
			int encoded = mEncodingTable[c] ;
			int length = mEncodingLengthTable[c] ;
			bos.write(encoded, length);
		}
		bos.close();
		
		HuffmanCodingData ret = new HuffmanCodingData();
		ret.trailingBits = bos.remainingBits;
		ret.encodedData = bos.toByteArray();
		ret.huffmanCodingTree = mTree;
		return ret;
	}
	
	public static void main(String args[]) throws IOException {
		HuffmanCoding hc = new HuffmanCoding();
		HuffmanCodingData data = hc.encode("el perro de san roque no tiene rabo");
		String decoded = hc.decode(data);
	}
}
