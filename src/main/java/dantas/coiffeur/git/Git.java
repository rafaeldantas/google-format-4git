package dantas.coiffeur.git;

import com.google.common.collect.TreeRangeSet;
import java.util.List;

public interface Git {

    List<String> listModified();

    List<String> blame(String fileName);

    List<Integer> listModifiedLineNumbers(String fileName);

    TreeRangeSet<Integer> listModifiedRanges(String fileName);
}
