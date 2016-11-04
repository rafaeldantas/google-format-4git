package dantas.coiffeur.git;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandLineExecutor implements CommandExecutor {

	@Override
	public List<String> execute(String cmd) {
		List<String> files = new ArrayList<>();
		try {
			Process gitLs = Runtime.getRuntime().exec(cmd);
			int exitCode = gitLs.waitFor();
			if (exitCode != 0) {
				throw new CoiffeurException(String.format("Failed to execute command '%s'", cmd));
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(gitLs.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				files.add(line.trim());
			}
		} catch (IOException | InterruptedException e) {
			throw new CoiffeurException(String.format("Failed to execute command '%s' because '%s'", cmd, e.getMessage()),
					e);
		}
		return files;
	}

}
