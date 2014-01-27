package eu.ehri.termexpansion;

import java.util.HashSet;
import java.util.List;


/* 
 * Auxiliary methods for processing of lists.
 */
public class AuxiliaryMethods {


	/*
	 * remove duplicate members of a list of strings
	 */
	static List<String> removeDuplicates(List<String> arrayList) {
		HashSet<String> set = new HashSet<String>(arrayList);
		arrayList.clear();
		arrayList.addAll(set);
		return arrayList;
			}
	
	/*
	 * remove duplicate members of a list of integers
	 */
	static List<Integer> removeDuplicateIndexes(List<Integer> arrayList) {
		HashSet<Integer> set = new HashSet<Integer>(arrayList);
		arrayList.clear();
		arrayList.addAll(set);
		return arrayList;
	}
	
	/*
	 * transform a list of strings in a string. 
	 * Members of the list are in "" and separated by space
	 */
	static String listToString (List<String> arrayList){
		String string = "";
		for (int i = 0; i < arrayList.size() - 1; i++){
			string = string + "\"" + arrayList.get(i) + "\"" + " ";			
		}
		string = string + "\"" + arrayList.get(arrayList.size()-1) + "\"";	
		return string;
	}
	
}
