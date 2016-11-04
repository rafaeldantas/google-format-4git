package dantas.coiffeur.git;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;

public class GitParseTest {

	private static final String README = "README.md";

	private Git git;

	private CommandExecutor mock;

	@Before
	public void setup() {
		mock = mock(CommandExecutor.class);
		git = new GitEngine(mock);
	}

	@Test
	public void listModifiedTest() {
		List<String> modifiedFiles = Arrays.asList("README.md", "Something.java", "/some/ugly/Class.java");
		when(git.listModified()).thenReturn(modifiedFiles);

		assertEquals(modifiedFiles, git.listModified());
	}

	@Test
	public void blameTest() {
		List<String> modifiedLines = uncommittedBlameLines("13", "14", "15");
		Mockito.when(git.blame(README)).thenReturn(modifiedLines);

		assertEquals(modifiedLines, git.blame(README));
	}

	@Test
	public void listModifiedLineNumbersTest() {
		List<String> modifiedLines = uncommittedBlameLines("13", "14", "15");
		Mockito.when(git.blame(README)).thenReturn(modifiedLines);

		assertEquals(Arrays.asList(13, 14, 15), git.listModifiedLineNumbers(README));
	}

	@Test
	public void listRangesTest() {
		List<String> modifiedLines = uncommittedBlameLines("10", "11", "15", "16");
		Mockito.when(git.blame(README)).thenReturn(modifiedLines);
		TreeRangeSet<Integer> ranges = git.listModifiedRanges(README);
		Set<Range<Integer>> rangeSet = new HashSet<>();

		rangeSet.add(Range.open(10, 11));
		rangeSet.add(Range.open(15, 16));

		assertEquals(rangeSet, ranges.asRanges());
	}

	@Test
	public void listRangesWithSingleRangeTest() {
		List<String> modifiedLines = uncommittedBlameLines("10");
		Mockito.when(git.blame(README)).thenReturn(modifiedLines);
		Set<Range<Integer>> ranges = git.listModifiedRanges(README).asRanges();

		assertEquals(1, ranges.size());
		assertEquals(Range.singleton(10), ranges.iterator().next());
	}

	private List<String> uncommittedBlameLines(String... lineNumbers) {
		List<String> blame = new ArrayList<>();
		for (String lineNumber : lineNumbers) {
			blame.add(String.format("00000000 (Not Committed Yet 2016-10-26 21:29:23 +0200 %s) Hi", lineNumber));
		}

		return blame;
	}
}
