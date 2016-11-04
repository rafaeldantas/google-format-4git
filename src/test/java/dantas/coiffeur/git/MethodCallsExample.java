package dantas.coiffeur.git;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Strings;


//https://github.com/javaparser/javaparser/wiki/Manual
public class MethodCallsExample {

    public static void listMethodCalls(File projectDir) {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            System.out.println(path);
            System.out.println(Strings.repeat("=", path.length()));
            try {
				new VoidVisitorAdapter<Object>() {
				    @Override
				    public void visit(MethodCallExpr n, Object arg) {
				        super.visit(n, arg);
				        System.out.println(" [L " + n.getRange() + "] " + n + ": parent: "+n.getComment());
				    }
				}.visit(JavaParser.parse(file), null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(); // empty line
        }).explore(projectDir);
    }

    public static void main(String[] args) {
        File projectDir = new File("/Users/rafael/projects/bb/6/CXP-CM/content-services");
        listMethodCalls(projectDir);
    }
}