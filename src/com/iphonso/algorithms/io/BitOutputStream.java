package com.iphonso.algorithms.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream extends OutputStream {
   private static final int bitmask[] = {
        0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff,
        0x1ff,0x3ff,0x7ff,0xfff,0x1fff,0x3fff,0x7fff,0xffff,
        0x1ffff,0x3ffff,0x7ffff,0xfffff,0x1fffff,0x3fffff,
        0x7fffff,0xffffff,0x1ffffff,0x3ffffff,0x7ffffff,
        0xfffffff,0x1fffffff,0x3fffffff,0x7fffffff,0xffffffff
    };
   
   private static final int BITS_PER_BYTE = 8;
   
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();
	byte currentByte = 0;
	public int remainingBits = BITS_PER_BYTE;
	
	@Override
	public void write(int b) throws IOException {
		currentByte = (byte) (b >> BITS_PER_BYTE);
		bos.write(currentByte);
		currentByte = (byte) (b & bitmask[BITS_PER_BYTE]);
		bos.write(currentByte);
	}
	
	public void write(int i, int nbits) {
		i = i & bitmask[nbits];
		
		if (nbits <= remainingBits) {
			remainingBits = remainingBits - nbits;
			byte iShifted = (byte) (i << remainingBits);
			
			currentByte = (byte) (currentByte | iShifted);
			if (remainingBits == 0) {
				bos.write(currentByte);
				currentByte = 0;
				remainingBits = 8;
			}
		} else {
			// divide and conquer (divide by 2 and write)
			byte i0, i1;
			// left is of length remainingBits  (shift to the right nbits-remainingBits)
			int toShift = (nbits-remainingBits);
			
			i0 = (byte) (i >> toShift); // right part;
			write(i0, remainingBits); // call recursively (it will hit the end of recursion)
			
			i1 = (byte) (i & bitmask[toShift]); // left part
			write(i1, toShift); // write the remaining (which could be bigger= than 8)
		}
	}
	
	public void flush() {
		bos.write(currentByte);
		currentByte = 0;
	}
	
	public void close() throws IOException {
		flush();
		bos.close();
	}
	public static void print(ByteArrayOutputStream bos) {
		byte[] ba = bos.toByteArray();
			System.out.println("Number of bytes: " + ba.length);
		for (int i = 0; i < ba.length; i++) {
			System.out.print(Integer.toBinaryString(ba[i] & 0xff));
		}
	}

	public void print() {
		print(bos);
		// todo: this last print is not correct
		System.out.println("Remaining " + (8 - remainingBits) + " bits: " + Integer.toBinaryString(currentByte & 0xff));
	}

	public static void main(String args[]) {
		byte[] test = new byte[5];
		test[0] = (byte) 0xff; // 1111 1111
		test[1] = (byte) 0xda; // 1101 1010
		test[2] = (byte) 0xc1; // 1100 0001
		test[3] = (byte) 0x29; // 0010 1001
		test[4] = (byte) 0x72; // 0111 0010
		
		BitOutputStream bos = new BitOutputStream();
		for (int i = 0; i < test.length; i++) {
			bos.write(test[i], 6);
		}
		// 11111101101000001101001110010
		// 1111110110100000110100111001000

		// 111111010101001Remaining 6 bits: 11001000

		bos.print();
//		
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		FileBitArrayOutputStream fbos = new FileBitArrayOutputStream(baos);
//		for (int i = 0; i < test.length; i++) {
//			fbos.write(6, test[i]);
//		}
//		fbos.flush();
//		bos.print(baos);
//		
	}

}
