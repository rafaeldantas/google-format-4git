package dantas.coiffeur.git;

import java.io.File;
import java.io.FileInputStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodPrinter {

    public static void main(String[] args) throws Exception {
        // creates an input stream for the file to be parsed
    	File file = new File("src/test/resources/com/test/Test.java");
        FileInputStream in = new FileInputStream("src/test/resources/com/test/Test.java");

        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
            System.out.println(cu.getPackage());
            System.out.println(cu.getImports());
        } finally {
            in.close();
        }

        // visit and print the methods names
        new MethodVisitor().visit(cu, null);

		new VoidVisitorAdapter<Object>() {
		    @Override
		    public void visit(MethodCallExpr n, Object arg) {
		        super.visit(n, arg);
		        System.out.println(" [L " + n.getRange() + "] " + n + ": comment: "+n.getComment());
		        System.out.println(n.getName());
		        System.out.println(n.getArgs());
		    }
		}.visit(JavaParser.parse(file), null);
    }

    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
    private static class MethodVisitor extends VoidVisitorAdapter<Object> {

        @Override
        public void visit(MethodDeclaration n, Object arg) {
            // here you can access the attributes of the method.
            // this method will be called for all methods in this
            // CompilationUnit, including inner class methods
        	System.out.println(n.getDeclarationAsString());
        	System.out.println(ModifierSet.getAccessSpecifier(n.getModifiers()));
            super.visit(n, arg);
        }
    }
}