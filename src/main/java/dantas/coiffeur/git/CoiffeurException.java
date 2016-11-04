package dantas.coiffeur.git;

public class CoiffeurException extends RuntimeException {

    private static final long serialVersionUID = -5088540314100453939L;

    public CoiffeurException(String msg) {
        super(msg);
    }

    public CoiffeurException(String msg, Exception e) {
        super(msg, e);
    }
}
