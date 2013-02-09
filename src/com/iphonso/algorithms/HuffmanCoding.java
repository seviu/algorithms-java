package com.iphonso.algorithms;

import java.io.IOException;
import java.util.*;

import com.iphonso.algorithms.io.BitOutputStream;

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
		public int weight; // if weight is < 0 then this node is an intermediate node
		
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
				if (currentNode.weight != -1) {
					ret.append(currentNode.c);
					currentNode = huffmanCodingTree;
				}
			}
		}
		return ret.toString();
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
		// It is probably not a good idea to get rid of the tree
		mTree = tree; // lets copy it (we want to decode sometimes)
		while (tree != null) {
			Node n0 = tree.child0;
			Node n1 = tree.child1;
			
			length++;
			val = val << 1;
			
			if (n0 != null && n0.weight > 0) {
				mEncodingTable[n0.c] = val;
				mEncodingLengthTable[n0.c] = length;
			}
			val = val | 1;
			if (n1 != null && n1.weight > 0) {
				mEncodingTable[n1.c] = val;
				mEncodingLengthTable[n1.c] = length;
			}
			tree = n1;
		}
		
		// Lets print the lookup table values for posterity
		//		for (int i = 0; i < 255; i++) {
		//			if (encodingTable[i] >= 0) {
		//				System.out.println("Char " + (char)i + " val " + Integer.toBinaryString(encodingTable[i]) + " length " + encodingLengthTable[i]);
		//			}
		//		}
		
		// We use the lookup table to encode the whole thing
		BitOutputStream bos = new BitOutputStream();
		for (int i = 0; i < e.length(); i++) {
			char c = e.charAt(i);
			int encoded = mEncodingTable[c] ;
			length = mEncodingLengthTable[c] ;
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
