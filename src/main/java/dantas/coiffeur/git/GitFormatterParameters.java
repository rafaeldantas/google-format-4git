package dantas.coiffeur.git;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters
public class GitFormatterParameters {

	@Parameter(names = { "-g", "--g", "-git",
			"--git" }, description = "Use git staging area to detrmine the list of files to be formatted")
	boolean gitFlag = false;

	@Parameter(names = { "-m", "--m", "-modified-lines",
			"--modified-lines" }, description = "Format only modified lines")
	boolean gitModifiedOnlyFlag = false;

	@Parameter(names = { "-ml", "--ml", "-max-line-length",
			"--max-line-length" }, description = "Format only modified lines")
	int maxLineLength = 140;

	public int maxLineLength(){
		return maxLineLength;
	}

	public boolean gitFlag() {
		return gitFlag;
	}

	public boolean gitModifiedOnlyFlag() {
		return gitModifiedOnlyFlag;
	}

	public boolean useGit() {
		return gitFlag() || gitModifiedOnlyFlag();
	}

}
