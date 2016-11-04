package dantas.coiffeur.git;

import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This whole implementation is kinda of a hack. Actually use a git client instead of the cli?
 *
 */
public class GitEngine implements Git {

    private static final String MODIFIED_LINE_PREFFIX = "0000000000000000000000000000000000000000";

    private final CommandExecutor commandExecutor;

    public GitEngine(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    @Override
    public List<String> listModified() {
        return commandExecutor.execute("git ls-files -m");
    }

    @Override
    public List<String> blame(String fileName) {
        return commandExecutor.execute("git blame --porcelain -n " + fileName);
    }

    private List<Integer> parseModifiedLineNumbers(List<String> lines) {
        List<Integer> modifiedLineNumbers = new ArrayList<>();

        for (String string : lines) {
            int modifiedLineNumber = parseModifiedLineNumber(string);
            if (modifiedLineNumber != 0) {
                modifiedLineNumbers.add(modifiedLineNumber);
            }
        }
        return modifiedLineNumbers;
    }

    private int parseModifiedLineNumber(String line) {
        if (line.startsWith(MODIFIED_LINE_PREFFIX)) {
            line = line.replace(MODIFIED_LINE_PREFFIX + " ", "");
            List<String> splitToList = Splitter.on(" ").splitToList(line);
            return Integer.parseInt(splitToList.get(0).trim());
        }
        return 0;
    }

    @Override
    public List<Integer> listModifiedLineNumbers(String fileName) {
        return parseModifiedLineNumbers(blame(fileName));
    }

    @Override
    public TreeRangeSet<Integer> listModifiedRanges(String fileName) {

        List<Integer> modifiedLineNumbers = listModifiedLineNumbers(fileName);

        TreeRangeSet<Integer> rangeSet = TreeRangeSet.create();

        if (modifiedLineNumbers.size() == 1) {
            rangeSet.add(Range.singleton(modifiedLineNumbers.stream().findFirst().get()));

        } else {

            int currentStart = modifiedLineNumbers.stream().findFirst().get();
            int previous = currentStart;
            Iterator<Integer> iterator = modifiedLineNumbers.iterator();
            while (iterator.hasNext()) {
                int lineNumber = iterator.next();

                if (!iterator.hasNext()) {
                    Range<Integer> range = Range.open(currentStart, lineNumber);
                    rangeSet.add(range);
                    break;
                }

                if (lineNumber - (previous + 1) > 0) {
                    if (currentStart == previous) {
                        Range<Integer> range = Range.singleton(currentStart);
                        rangeSet.add(range);
                    } else {
                        Range<Integer> range = Range.open(currentStart, previous);
                        rangeSet.add(range);
                    }
                    currentStart = lineNumber;
                }
                previous = lineNumber;
            }
        }
        return rangeSet;
    }
}
