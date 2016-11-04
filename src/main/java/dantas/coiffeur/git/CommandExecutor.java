package dantas.coiffeur.git;

import java.util.List;

public interface CommandExecutor {

    List<String> execute(String cmd);
}
