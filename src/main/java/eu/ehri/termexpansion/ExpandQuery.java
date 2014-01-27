package eu.ehri.termexpansion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * Uses n-grams to find shingles in multi-word queries
 * n-grams are looked in geonames and thesaurus
 * maximal length of the shingle will be 7
 */

	public class ExpandQuery  {

	List<String> shinglesTh = new ArrayList<String>();
	List<Integer> indexToRemove = new ArrayList<Integer>();
	List<String> extendedTermList = new ArrayList<String>();

	InteractWithThesaurusNeo4j interactionTh = new InteractWithThesaurusNeo4j();
	
	
	
	public List<String> expandTerms(String input) {
		List<String> inputAsList = new ArrayList<String>();
		inputAsList = Arrays.asList(input.split(" "));
	
		//maximal size of the n-gram
		//we start with size 7 (maximal size in Thesaurus)
		int maxsize;
		if (inputAsList.size() > 7) {
			maxsize = 7;
		} else {
			maxsize = inputAsList.size();
		}
		//If the user input has more than just a word
		if (inputAsList.size() > 1) {
			//detect shingles in n-grams
			for (int ngramsize = maxsize; ngramsize > 1; ngramsize--) {
				for (int idx = 0; idx + ngramsize <= inputAsList.size(); idx++) {
					String shingle = "";
					// int min = 0;
					// int max = 0;
					for (int i = idx; i < idx + ngramsize - 1; i++) {
						shingle = shingle + inputAsList.get(i) + " ";
					}
					int min = idx;
					int max = idx + ngramsize;
					shingle = shingle + inputAsList.get(max - 1);
					
					//check whether the chunk produced by the n-gram is
					// in the thesaurus. If it is, it will be expanded.
					if (InteractWithThesaurusNeo4j.inThesaurus(shingle)) {
						// to expand we get the concept node and then we search the
						// nodes in a lower position in the hierarchy
						String node = InteractWithThesaurusNeo4j.getConceptNode(shingle);
						List<String> thesaurusOutput = InteractWithThesaurusNeo4j
								.expandWithThesaurus(node);
						AuxiliaryMethods.removeDuplicates(thesaurusOutput);
						// put in shingle list
						shinglesTh.addAll(thesaurusOutput);
						extendedTermList.addAll(thesaurusOutput);

						// introduce the indexes of found shingles
						// in index list to be removed from the original list
						for (int pos = min; pos <= max - 1; pos++) {
							indexToRemove.add(pos);
						}
					}



				}
			}
			AuxiliaryMethods.removeDuplicateIndexes(indexToRemove);

			List<String> inputAsListNew = new ArrayList<String>();
			inputAsListNew.addAll(inputAsList);

			//remove the expanded terms from the input list
			Collections.sort(indexToRemove);
			Collections.reverse(indexToRemove);
			for (int i = 0; i < indexToRemove.size(); i++) {
				int index = indexToRemove.get(i);
				inputAsListNew.remove(index);
			}

			// Expansion of individual terms (no part of shingles)
			for (int i = 0; i < inputAsListNew.size(); i++) {
				// check whether it is in the thesaurus, and if it is
				// expand it.
				if (InteractWithThesaurusNeo4j.inThesaurus(inputAsListNew.get(i))) {
				String node = InteractWithThesaurusNeo4j.getConceptNode(inputAsListNew.get(i));
				List<String> thesaurusOutput = new ArrayList<String>();
				thesaurusOutput = InteractWithThesaurusNeo4j
						.expandWithThesaurus(node);
				extendedTermList.addAll(thesaurusOutput);
				}

				extendedTermList.add(inputAsListNew.get(i));

			}
		} else {
			// If the input of the user is just 1 word
			// First of all we add the word to the list of expanded terms. 
			extendedTermList.add(input);
			// check wheter the word is a thesaurus term, 
			// and if it is expand it
			if (InteractWithThesaurusNeo4j.inThesaurus(input)) {
				String node = InteractWithThesaurusNeo4j.getConceptNode(input);
				List<String> thesaurusOutput = InteractWithThesaurusNeo4j
						.expandWithThesaurus(node);
				AuxiliaryMethods.removeDuplicates(thesaurusOutput);
				extendedTermList.addAll(thesaurusOutput);
			}


		}

		AuxiliaryMethods.removeDuplicates(extendedTermList);
		//return the expanded list
		return extendedTermList;
	}
}
