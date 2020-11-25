public class DES {
	
	public static void main(String[] args) {
		String key = "133457799BBCDFF1";
		String message = "EXAMPLE!";
		
		DES d = new DES();
		String[] keys = d.createTheKeys(key);
		d.encodeMessage(message, keys);	
	}
	
	
	private String[] createTheKeys(String key) {
		String binKey = createBinaryKey(key);
		String permutedKey = permuteKey(binKey);
		String split[] = split(permutedKey,28);
		String keys[] = createCandDBlocks(split[0], split[1]);
		String permutedKeys[] = permuteKeys(keys);
		return permutedKeys;
	}
	
	
	private void encodeMessage(String message, String[] permKeys) {
		String binMes = convertMessageToBin(message);
		String permutedMes = permuteMessage(binMes); 
		String split[] = split(permutedMes,32);
		String R16L16 = createLandRBlocks(split[0], split[1],permKeys);
		String finalPermutation = applyFinalPermutation(R16L16);
		convetToHex(finalPermutation);	
	}

	
	private String createBinaryKey(String key) {
		String strNum;
		String binKey = "";
		
		System.out.println("Original Key --> " + key);
		for (int i=0;i<16;i=i+2) {
			System.out.print("SubKey in hex: " + key.substring(i,i+2) + " ");//Getting the pairs
			strNum = Integer.toBinaryString(Integer.parseInt(key.substring(i,i+2), 16));//Convert from Hex to Binary
			while (strNum.length()<8)//Add 0s to complete 1 byte
					strNum = "0" + strNum;
			System.out.println("to binary --> " + strNum );
			binKey += strNum;
		}
		System.out.println("Binary Key --> " + binKey);
		return binKey;
	}
	
	
	private String permuteKey(String key) {
		int[][] pc1 = { {57,49,41,33,25,17,9}, 
				 {1,58,50,42,34,26,18},
				 {10,2,59,51,43,35,27},
				 {19,11,3,60,52,44,36},
				 {63,55,47,39,31,23,15},
				 {7,62,54,46,38,30,22},
				 {14,6,61,53,45,37,29},
				 {21,13,5,28,20,12,4}
		};
		return printPermutation(permutation(8, 7, pc1, key));
	}
	
	private String permutation(int row,int col, int[][] table, String str) {
		String permutedStr = "";
		for (int i=0;i<row;i++) 
			for (int j=0;j<col;j++)
				permutedStr += str.charAt(table[i][j] - 1);//Permutation
		return permutedStr;
	}
	
	private String printPermutation(String p) {
		System.out.println("After permutation --> " + p + "\n");
		return p;
	}

	private String[] split(String str, int n) {
		String[] split = new String[2];
		split[0] = "";
		split[1] = "";
		
		for (int i=0;i<n;i++)//First n bits
			split[0] += str.charAt(i);
		for (int i=n;i<2*n;i++)//Last n bits
			split[1] += str.charAt(i);	

		if (n==28) {
			System.out.println("C0 = " + split[0]);
			System.out.println("D0 = " + split[1] + "\n");
		}
		else {
			System.out.println("LEFT = " + split[0]);
			System.out.println("RIGHT = " + split[1] + "\n");
		}
		return split;
	}

	
	private String[] createCandDBlocks(String c, String d) {
		int[] numOfShifts = {0,1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};
		String[] cBlocks = new String[17];
		String[] dBlocks = new String[17];
		cBlocks[0] = c;
		dBlocks[0] = d;
		
		for (int i=1; i<17;i++) {
			cBlocks[i] = "";
			dBlocks[i] = "";
			
			for (int j = numOfShifts[i] ;j<28 ;j++) {//Copying the previous block
				cBlocks[i] += cBlocks[i-1].charAt(j);
				dBlocks[i] += dBlocks[i-1].charAt(j);
			}//Adding at the end the first bit of the previous block
			cBlocks[i] += cBlocks[i-1].charAt(0);
			dBlocks[i] += dBlocks[i-1].charAt(0);
			if (numOfShifts[i] == 2) {//Adding at the end the second bit of the previous block
				cBlocks[i] += cBlocks[i-1].charAt(1);
				dBlocks[i] += dBlocks[i-1].charAt(1);
			}
			System.out.println("C" + i + " = " + cBlocks[i]);
			System.out.println("D" + i + " = " + dBlocks[i]);
			System.out.println();
		}
		
		System.out.println("Concatenated pairs of Cn and Dn");
		String[] keys = new String[16];
		for (int i=0; i<16;i++) {
			keys[i] = cBlocks[i+1] + dBlocks[i+1]; //Concatenation
			System.out.println("Key " + (i+1) + " : " + keys[i]);
		}	
		return keys;
	}

	
	private String[] permuteKeys(String[] keys) {
		int[][] pc2 = { {14,17,11,24,1,5}, 
				 {3,28,15,6,21,10},
				 {23,19,12,4,26,8},
				 {16,7,27,20,13,2},
				 {41,52,31,37,47,55},
				 {30,40,51,45,33,48},
				 {44,49,39,56,34,53},
				 {46,42,50,36,29,32}
		};
		String[] permKeys = new String[16];
		System.out.println("\nPermutated keys of concatenated pairs");
		for (int i=0; i<16;i++) {//For each key
			permKeys[i] = permutation(8, 6, pc2, keys[i]);
			System.out.println("Key " + (i+1) + " : " + permKeys[i]);
		}
		return permKeys;
	}
	
	
	private String convertMessageToBin(String m) {
		String bin;
		String binMessage = "";
		System.out.println("\nConverting message to binary...");
		
		for(int i=0; i<m.length(); i++) {
			bin = Integer.toBinaryString(m.charAt(i));//Convert ASCII to Binary
			while (bin.length()<8)//Add 0s to complete 1 byte
				bin = "0" + bin;
			binMessage += bin;
			System.out.println("Letter: " + m.charAt(i) + " --> ASCII code: " + (int)m.charAt(i) + " --> to binary: " + bin);
		}
		System.out.println("Original message: " + m);
		System.out.println("Binary message: " + binMessage);
		return binMessage;	
	}
	
	
	private String permuteMessage(String binMes) {
		int[][] ip = { {58,50,42,34,26,18,10,2}, 
				 {60,52,44,36,28,20,12,4},
				 {62,54,46,38,30,22,14,6},
				 {64,56,48,40,32,24,16,8},
				 {57,49,41,33,25,17,9,1},
				 {59,51,43,35,27,19,11,3},
				 {61,53,45,37,29,21,13,5},
				 {63,55,47,39,31,23,15,7}
		};
		return printPermutation(permutation(8,8,ip,binMes));
	}

	
	private String createLandRBlocks(String l, String r, String[] permKeys) {
		String[] lBlocks = new String [17];
		String[] rBlocks = new String [17];
		lBlocks[0] = l;
		rBlocks[0] = r;
		
		for (int i = 1; i<17; i++){
			System.out.println("Iteration: " + i);
			lBlocks[i] = rBlocks[i-1]; //Creating Ln

			//Creating Rn
			String fFunctionResult = fFunction(rBlocks[i-1],permKeys[i-1]); 
			rBlocks[i] = "";
			for (int j=0;j<32;j++) 
				//XOR between Ln-1 block and the result of the f Function
				rBlocks[i] += ((lBlocks[i-1].charAt(j) + fFunctionResult.charAt(j))%2);
			System.out.println("L" + i + ": " + lBlocks[i]);
			System.out.println("R" + i + ": " + rBlocks[i] + "\n");	
		}
		String R16L16 = rBlocks[16] + lBlocks[16];
		return R16L16;
	}

	
	private String fFunction(String r,String k) {
		System.out.println("Calculating fFunction...");
		String temp = eFunction(r,k);
		String complete = sBoxCalculation(temp);
		String permuted = permutationOfsBoxResult(complete);
		return permuted;
	}


	private String eFunction(String r,String k) {
		int[][] eBitSelectionTable = { {32,1,2,3,4,5}, 
				 						{4,5,6,7,8,9},
				 						{8,9,10,11,12,13},
				 						{12,13,14,15,16,17},
				 						{16,17,18,19,20,21},
				 						{20,21,22,23,24,25},
				 						{24,25,26,27,28,29},
				 						{28,29,30,31,32,1}
		};	
		String permutedR = permutation(8, 6, eBitSelectionTable, r);
		String finalR = "";
		for(int i=0; i<48; i++) 
			finalR += ((permutedR.charAt(i) + k.charAt(i))%2);//XOR with the key
		
		System.out.println("eFunction result : " + finalR );
		return finalR;
	}
	

	private String sBoxCalculation(String block) {
		int[][] s1 = { {14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7}, 
				{0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
				{4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
				{15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}
		};
		int[][] s2 = { {15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10}, 
				{3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
				{0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
				{13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}
		};

		int[][] s3 = { {10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8}, 
				{13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
				{13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
				{1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}
		};

		int[][] s4 = { {7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15}, 
				{13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
				{10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
				{3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}
		};

		int[][] s5 = {{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
				{14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
				{4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
				{11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3},	
		};
		int[][] s6 = {{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
				{10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
				{9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
				{4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13},	
		};
		int[][] s7 = {{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
				{13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
				{1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
				{6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12},	
		};
		int[][] s8 = {{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
				{1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
				{7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
				{2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11},	
		};
		String partBlock,bin;
		String complete = "";
		int[][] sbox = new int[4][16];
		int i = 0;
		int row,col;
		
		System.out.println("Calculating sBox substitutions...");
		while (i<48){
			partBlock = block.substring(i,i+6);//Split to 6-bit blocks
			row = Integer.parseInt(partBlock.charAt(0) + "" + partBlock.charAt(5),2);//First and last bits to find the row
			col = Integer.parseInt(partBlock.substring(1,5),2);//4 middle bit to find the column
			
			//Choose the correct s table
			if (i == 0)
				sbox = s1;
			else if (i == 6)
				sbox = s2;
			else if (i == 12)
				sbox = s3;
			else if (i == 18)
				sbox = s4;
			else if (i == 24)
				sbox = s5;
			else if (i == 30)
				sbox = s6;
			else if (i == 36)
				sbox = s7;
			else if (i == 42)
				sbox = s8;
			
			//Get the element from the S box and convert it to binary
			bin = Integer.toBinaryString(sbox[row][col]);//C
			while (bin.length()<4)//Add 0s to complete 4 bits
				bin = "0" + bin;
			
			complete = complete + bin;//Adding the parts to the result
			i+=6;//Get the next 6-bit block
			System.out.println("Block of 6 bits: " + partBlock + ", Row: " + row + ", Col: " + col + ", "
					+"Decimal --> Binary : " + sbox[row][col] + " --> " + bin);	
		}	
		System.out.println("Result: " + complete);
		return complete;		
	}
	

	private String permutationOfsBoxResult(String str) {
		int[][] p = { {16,7,20,21},
				{29,12,28,17},
				{1,15,23,26},
				{5,18,31,10},
				{2,8,24,14},
				{32,27,3,9},
				{19,13,30,6},
				{22,11,4,25}
		};
		return printPermutation(permutation(8, 4, p, str));
	}
	
	
	private String applyFinalPermutation(String str) {
		int[][] ip_1 = { {40,8,48,16,56,24,64,32}, 
					{39,7,47,15,55,23,63,31},
					{38,6,46,14,54,22,62,30},
					{37,5,45,13,53,21,61,29},
					{36,4,44,12,52,20,60,28},
					{35,3,43,11,51,19,59,27},
					{34,2,42,10,50,18,58,26},
					{33,1,41,9,49,17,57,25}
		};
		System.out.println("R16L16: " + str);
		return printPermutation(permutation(8,8,ip_1, str));
	}

	
	private void convetToHex(String m) {
		int i = 0 ;
		String completeHex = "" ;
		String hex;
		
		System.out.println("Converting binary ciphertext to hex...");
		while(i<64) {				
			System.out.print("8 bit-block: " + m.substring(i, i+8)); //Split to 8-bit blocks
			System.out.print(" to decimal --> " + Integer.parseInt(m.substring(i, i+8),2));//Convert from binary to decimal
			hex = Integer.toHexString(Integer.parseInt(m.substring(i, i+8),2));//Convert from decimal to hex
			if (hex.length()<2)//Add 0 to complete a pair
				hex = "0" + hex;
			System.out.print(" to hex --> " + hex + "\n");
			completeHex += hex;//Add the parts for the complete result
			i+=8;
		}
		System.out.println("Ciphertext in hexadecimal form: " + completeHex.toUpperCase());
	}
}