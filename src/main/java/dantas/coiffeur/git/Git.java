package dantas.coiffeur.git;

import java.util.List;

import com.google.common.collect.TreeRangeSet;

public interface Git {

	List<String> listModified();

	List<String> blame(String fileName);

	List<Integer> listModifiedLineNumbers(String fileName);

	TreeRangeSet<Integer> listModifiedRanges(String fileName);
}
