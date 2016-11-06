import java.util.*;
import java.io.*;
class BruteLO{
	public static ArrayList<String> positiveClauses;
	static ArrayList<String> posCone = new ArrayList<String>();// = {"a", "b", "aBBa", "bAAb"};
	//static String[] posCone;
	static boolean checkConjugates = false;
	static ArrayList<String> identities = new ArrayList<String>();
	static ArrayList<String> conjugates = new ArrayList<String>();
	static ArrayList<String> conjugInfo = new ArrayList<String>();
	static HashMap<String, ArrayList<Character>> conjugOptions = new HashMap<String, ArrayList<Character>>();
	static int[][] edges;
	static boolean[][][] visited;
	static boolean outputMode = false;
	static int maxPosElementLength = 0;
	
	public static int casenum = 0;
	public static void main(String[] args) throws java.io.FileNotFoundException{
		visited = new boolean[250][200][30];
		conjugates.add("");
		bruteList = new ArrayList<String>();
		bruteList.add("");
		Scanner identitiesin = new Scanner(new File("identities.txt"));
		while(identitiesin.hasNextLine()) {
			String thisLine = identitiesin.nextLine();
			if(thisLine.equals("") || thisLine.substring(0,2).equals("//")) {
				continue;
			}
			if(thisLine.substring(0,2).equals("/*")) {
				while(true) {
					if(!identitiesin.hasNextLine()) {
						System.err.println("MISSING CLOSING */ IN IDENTITIES.TXT");
						return;
					}
					if((thisLine=identitiesin.nextLine()).length()>=2 &&thisLine.substring(0,2).equals("*/")) {
						break;
					}
				}
				continue;
			}
			identities.add(thisLine);
		}
		
		Scanner filein = new Scanner(new File("poscone.txt"));
		while(filein.hasNextLine()) {
			String thisLine = filein.nextLine();
			if(thisLine.equals("") || (thisLine.length()>=2&&thisLine.substring(0,2).equals("//"))) {
				continue;
			}
			if(thisLine.length()>=2 && thisLine.substring(0,2).equals("/*")) {
				while(true) {
					if(!filein.hasNextLine()) {
						System.err.println("MISSING CLOSING */ IN POSCONE.TXT");
						return;
					}
					if((thisLine=filein.nextLine()).length()>=2 &&thisLine.substring(0,2).equals("*/")) {
						break;
					}
				}
				continue;
			}
			posCone.add(thisLine);
			if(thisLine.length()>maxPosElementLength) {
				maxPosElementLength = thisLine.length();
			}
		}
		if(!checkConjugates) {
			maxPosElementLength = 10000;
		}
		
		/*
		** set up edges between strings in positive cone
		*/
		System.out.println("Edge Map:");
		edges = new int[posCone.size()][posCone.size()];
		for(int i=0; i<posCone.size(); i++) {
			for(int j=0; j<posCone.size(); j++) {
				String string1 = posCone.get(i);
				String string2 = posCone.get(j);
				int index = 0;
				while(index<Math.min(string1.length(), string2.length())) {
					// if charater at index cancels, increment index, otherwise we're done
					if(Math.abs(string1.charAt(string1.length()-1-index) - string2.charAt(index)) == 'a'-'A') {
						index++;
					} else {
						System.out.println(string1+" "+string2);
						System.out.println(index);
						break;
					}
				}
				edges[i][j] = index;
				if(index>0) {
					String thisConjugate = string2.substring(0, index);
					if(!conjugates.contains(thisConjugate)) {
						conjugates.add(thisConjugate);
						conjugInfo.add(string1+" "+string2);
						conjugOptions.put(thisConjugate, new ArrayList<Character>());
					}
					ArrayList<Character> thisConjugOptions = conjugOptions.get(thisConjugate);
					if(checkConjugates) {
						for(Character thisConjugOption:getConjugOptions(j, thisConjugate.length())) {
							if(!thisConjugOptions.contains(thisConjugOption)) {
								thisConjugOptions.add(thisConjugOption);
							}
						}
					}
				}
			}
		}
		if(checkConjugates){
			System.out.println("conjugates: "+conjugates.toString());
			System.out.println("conjugInfo: "+conjugInfo.toString());
			System.out.println("conjugOptions: "+conjugOptions.toString());
		} else {
			conjugates = new ArrayList<String>();
			conjugates.add("");
		}
		//System.out.println(Arrays.deepToString(edges));
		
		if(theresAConetradiction()) {
			System.out.println("--THERES A CONETRADICTION!!!");
			return;
		} else {
			System.out.println("--No Conetradiction.");
		}
		
		for(String thisItem: identities) {
			int thisSign = stringSign(thisItem);
			System.out.println(thisSign + "  " + thisItem);
			if(thisSign!=0) {
				return;
			}
		}
		
		// USER INPUT FOR IDENTITIES
		Scanner console = new Scanner(System.in);
		while(true) {
			System.out.println("Next Thing to Check:");
			String searchString = console.nextLine();
			System.out.println();
			System.out.println(searchString);
			System.out.println("String sign: "+stringSign(searchString));
		}
		
		
		// USER INPUT FOR POSCONES
		/*
		Scanner console = new Scanner(System.in);
		while(true) {
			System.out.println("Enter case to test:");
			String searchString = console.nextLine();
			String invertedSearchString = invert(searchString);
			
			Boolean contra1 = anyContradiction(searchString);
			System.out.println();
			Boolean contra2 = anyContradiction(invertedSearchString);
			if(!contra1 && contra2) {
				posConePush(searchString);
				System.out.println(searchString+" added to positive cone");
			} else if(contra1 && !contra2){
				posConePush(invertedSearchString);
				System.out.println(invertedSearchString+" added to positive cone");
			} else if(!contra1 && !contra2) {
				System.out.println(searchString+", "+invertedSearchString+" Inconclusive");
			}
			if(contra1 && contra2) {
				System.out.println("---General Contradiction!");
			}
			
			
		}
		*/
		/*
		// BRUTE INPUT FOR POSCONES
		enlargeBruteList();
		enlargeBruteList();
		enlargeBruteList();
		enlargeBruteList();
		enlargeBruteList();
		while(true) {
			String searchString = bruteGetNextElement();
			String invertedSearchString = invert(searchString);
			System.out.println("Now Checking: "+searchString +" and " + invertedSearchString);
			
			Boolean contra1 = anyContradiction(searchString);
			//System.out.println();
			Boolean contra2 = anyContradiction(invertedSearchString);
			if(!contra1 && contra2) {
				if(bruteAddToPosCone(searchString)) {
					System.out.println("+ " + searchString+" added to positive cone");
					System.err.println(searchString);
				} else {
					System.out.println("(+) " + searchString+" positive and already a combination of things in the positive cone");
				}
			} else if(contra1 && !contra2){
				if(bruteAddToPosCone(invertedSearchString)) {
					System.out.println("+ " + invertedSearchString+" added to positive cone");
					System.err.println(invertedSearchString);
				} else {
					System.out.println("(+) " + invertedSearchString+" positive and already a combination of things in the positive cone");
				}
			} else if(!contra1 && !contra2) {
				bruteAddToInconclusive(searchString);
				System.out.println("? "+searchString+", "+invertedSearchString+" inconclusive");
			}
			if(contra1 && contra2) {
				System.out.println("! "+searchString+ " General Contradiction!");
				System.out.println("posCone: "+posCone.toString());
				return;
			}
		}
		*/
		
		
	}
	//returns +1 if positive, -1 if negative, 0 if unknown
	public static int stringSign(String searchString) {
		if(searchString.length()==0) {
			return 0;
		}
		if(isPositive(searchString)) {
			return +1;
		}
		if(isPositive(invert(searchString))) {
			return -1;
		}
		return 0;
	}
	
	/*Checks if a string is positive accounting for all rotations*/
	public static boolean isPositive(String searchString) {
		String cyclicString = searchString;
		//maxPosElementLength = 100000;
		for(int i=0; i<Math.min(cyclicString.length(), maxPosElementLength); i++) {
			if(outputMode) {
				System.out.println(cyclicString);
			}
		for(int k=0; k<conjugates.size(); k++) {
			String thisConjugator = conjugates.get(k);
			if(!thisConjugator.equals("") && !conjugOptions.get(thisConjugator).contains(cyclicString.charAt(0))) {
				continue;
			}
			String conjugatedString = conjugate(cyclicString, conjugates.get(k));
			//tried = new boolean[cyclicString.length()][posCone.size()];
			for(int j=0; j<posCone.size(); j++){
				clearVisitedArray();
				if(isPositive(conjugatedString, 0, j, 0)) {
					return true;
				}
			}
			cyclicString = cyclicString.substring(1)+cyclicString.charAt(0);
		}
		}
		return false;
	}
	/*Checks if a string is positive without accounting for rotations, and starting at index 'fromIndex'*/
	public static boolean isPositive(String searchString, int fromIndex, int posI, int posFromIndex) {
		if(outputMode) {
			System.out.println("isPositive("+searchString+", "+fromIndex+", "+posCone.get(posI)+", "+posFromIndex+")");
		}
		if(fromIndex>=searchString.length()){
			boolean returnable = (posFromIndex>=posCone.get(posI).length());
			if(returnable){System.out.print(posCone.get(posI)+", ");}
			return returnable;
		}
		
		if(visited[fromIndex][posI][posFromIndex]) {
			return false;
		}
		visited[fromIndex][posI][posFromIndex] = true;
		
		
		String posString = posCone.get(posI);
		//System.out.println(posString.length());
		if(posFromIndex<posString.length()) {
			if(outputMode) {
		System.out.println("match:"+(posString.charAt(posFromIndex)==searchString.charAt(fromIndex)));
			}
		}
		
		
		if(posFromIndex<posString.length() && posString.charAt(posFromIndex)==searchString.charAt(fromIndex)) {
			/*if(fromIndex+1>=searchString.length()){
				return true;
			}*/
			if(isPositive(searchString, fromIndex+1, posI, posFromIndex+1)) {
				System.out.print(posCone.get(posI)+", ");
				return true;
			}
		}
		
		//At this point either there was a character mismatch, or the posElement length has been reached
		int lengthLeft = posString.length()-posFromIndex; //this might be 0
		for(int i=0; i<posCone.size(); i++) {
			if(edges[posI][i]>=lengthLeft) {
				if(isPositive(searchString, fromIndex, i, lengthLeft)) {
					System.out.print(posCone.get(posI)+", ");
					return true;
				}
			}
		}
		return false;
		
		/*
		for (int j=0; j<posCone.size(); j++) {
			int searchIndex = searchString.indexOf(posCone[j], fromIndex);
			if(searchIndex==fromIndex) {
				if(isPositive(searchString, searchIndex+posCone[j].length())) {
					return true;
				}
			}
		}
		return false;
		*/
	}
	public static boolean theresAConetradiction() {
		for(String item:posCone) {
			String negativeItem = invert(item);
			for(int j=0; j<posCone.size(); j++){
				clearVisitedArray();
				if(isPositive(negativeItem, 0, j, 0)) {
					return true;
				}
			}
		}
		return false;
	}
	public static boolean anyContradiction(String searchString) {
		posConePush(searchString);
		if(theresAConetradiction()) {
			System.out.println("CoNeTrAdIcTiOn");
			posConePop();
			return true;
		}
		for(String thisItem: identities) {
			int thisSign = stringSign(thisItem);
			System.out.println(thisSign + "  " + thisItem);
			if(thisSign!=0) {
				posConePop();
				return true;
			}
		}
		posConePop();
		return false;
	}
	public static void posConePush(String pushable) {
		posCone.add(pushable);
		int[][] newEdges = new int[edges.length+1][edges.length+1];
		for(int i=0; i<edges.length+1; i++) {
			for(int j=0; j<edges.length+1; j++) {
				if(j<edges.length && i<edges.length) {
					newEdges[i][j] = edges[i][j];
				} else {
					String string1 = posCone.get(i);
					String string2 = posCone.get(j);
					int index = 0;
					while(index<Math.min(string1.length(), string2.length())) {
						// if charater at index cancels, increment index, otherwise we're done
						if(Math.abs(string1.charAt(string1.length()-1-index) - string2.charAt(index)) == 'a'-'A') {
							index++;
						} else {
							break;
						}
					}
					newEdges[i][j] = index;
				}
			}
		}
		edges = newEdges;
	}
	public static void posConePop() {
		int[][] newEdges = new int[edges.length-1][edges.length-1];
		for(int i=0; i<edges.length-1; i++) {
			for(int j=0; j<edges.length-1; j++) {
				newEdges[i][j] = edges[i][j];
			}
		}
		edges = newEdges;
		posCone.remove(posCone.size()-1);
	}
	public static void clearVisitedArray() {
		for(int i=0; i<visited.length; i++) {
			for(int j=0; j<visited[0].length; j++) {
				for(int k=0; k<visited[0][0].length; k++) {
					visited[i][j][k] = false;
				}
			}
		}
	}
	public static char invert(char thisChar) {
		return (char)((thisChar<'a')?thisChar+('a'-'A'):thisChar-('a'-'A'));
	}
	public static String invert(String thisString) {
		String tempString = "";
			for(int j=thisString.length()-1; j>=0; j--) {
				//turn uppercases into lowercases and vice versa, and add at the end
				char tempChar = thisString.charAt(j);
				tempString += invert(tempChar); //this uses invert(char)
			}
			return tempString;
	}
	/**
	* String conjugate(String thisString, String conjugator)
	*/
	public static String conjugate(String thisString, String conjugator) {
		String inverseConjugator = invert(conjugator);
		int index1 = 0;
		while(index1<thisString.length() && index1<conjugator.length() && thisString.charAt(index1)==inverseConjugator.charAt(index1)) {
			index1++;
		}
		int index2 = 0;
		while(index2<thisString.length() && index2<conjugator.length() && thisString.charAt(thisString.length()-1-index2)==conjugator.charAt(conjugator.length()-1-index2)) {
			index2++;
		}
		index2 = Math.min(index2, thisString.length()-index1);
		//System.out.println("conjugator.substring(0, "+(conjugator.length()-index1)+") + thisString.substring("+index1+", "+(thisString.length()-index2)+") + inverseConjugator.substring("+index2+", "+(inverseConjugator.length()-index2)+");");
		return conjugator.substring(0, conjugator.length()-index1) + thisString.substring(index1, thisString.length()-index2) + inverseConjugator.substring(index2);
	}
	
	/**
	* String getConjugOptions
	*/
	public static ArrayList<Character> getConjugOptions(int posI, int posFromIndex) {
		ArrayList<Character> returnable = new ArrayList<Character>();
		String thisPositive = posCone.get(posI);
		if(posFromIndex<thisPositive.length()) {
			returnable.add(thisPositive.charAt(posFromIndex));
		}
		int lengthLeft = thisPositive.length()-posFromIndex;
		for(int i=0; i<posCone.size(); i++) {
			if(edges[posI][i]>=lengthLeft) {
				returnable.addAll(getConjugOptions(i, lengthLeft));
			}
		}
		return returnable;
	}
	
	
	
	
	public static ArrayList<String> bruteList;
	public static LinkedList<String> inconclusiveList = new LinkedList<String>();
	public static char[] charList = {'A', 'B', 'C', 'D', 'a', 'b', 'c', 'd'};
	public static int bruteListIndex = 0;
	public static int inconclusiveListCounter = 0;
	public static void enlargeBruteList() {
		ArrayList<String> newList = new ArrayList<String>();
		for(String oldElement:bruteList) {
			for(char thisChar:charList) {
				if(oldElement.equals("") || !(oldElement.charAt(oldElement.length()-1)==invert(thisChar))) {
					newList.add(oldElement+thisChar);
				}
			}
		}
		bruteList = newList;
	}
	public static boolean bruteAddToPosCone(String element) {
		boolean elementIsPositive = false;
		for(int j=0; j<posCone.size(); j++){
			clearVisitedArray();
			if(isPositive(element, 0, j, 0)) {
				elementIsPositive = true;
				break;
			}
		}
		if(!elementIsPositive) {
			posConePush(element);
			freshenInconclusiveList();
			return true;
		} else {
			return false;
		}
	}
	public static String bruteGetNextElement() {
		if(bruteList.size()>0 && bruteListIndex<bruteList.size()) {
			String returnable = bruteList.get(bruteListIndex++);
			String inverseReturnable = invert(returnable);
			for(int i=0; i<bruteListIndex-1; i++) {
				if(bruteList.get(i).equals(inverseReturnable)) {
					//already considered inverse. Ignore this element and get next one
					return bruteGetNextElement();
				}
			}
			boolean elementIsPositive = false;
			for(int j=0; j<posCone.size(); j++){
				clearVisitedArray();
				if(isPositive(returnable, 0, j, 0)) {
					elementIsPositive = true;
					break;
				}
			}
			boolean reverseElementIsPositive = false;
			for(int j=0; j<posCone.size(); j++){
				clearVisitedArray();
				if(isPositive(inverseReturnable, 0, j, 0)) {
					reverseElementIsPositive = true;
					break;
				}
			}
			if((elementIsPositive) == (reverseElementIsPositive)) {
				if(elementIsPositive) {
					System.out.println("WHOA GENERAL CONTRADICTION!!!!!!!!!"+returnable);
				}
				return returnable;
			} else { //already in the positive cone, skip this element
				System.out.println();
				System.out.println("() " + returnable + " or " + inverseReturnable + " already positive, not checking it");
				return bruteGetNextElement();
			}
		} else if(inconclusiveList.size()>0 && !isInconclusiveListStale()) {
			inconclusiveListCounter++;
			return inconclusiveList.remove();
		} else {
			enlargeBruteList();
			bruteListIndex = 0;
			return bruteList.get(bruteListIndex++);
		}
	}
	public static boolean isInconclusiveListStale() {
		return inconclusiveListCounter>=inconclusiveList.size();
	}
	public static void freshenInconclusiveList() {
		inconclusiveListCounter = 0;
	}
	public static void bruteAddToInconclusive(String element) {
		inconclusiveList.add(element);
	}
}