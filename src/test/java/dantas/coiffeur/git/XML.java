package dantas.coiffeur.git;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XML {
	public static void main(String[] args) throws TransformerException {

		// Instantiate transformer input
		Source xmlInput = new StreamSource(
				new StringReader("<!-- Document comment --><aaa><bbb>lalala</bbb><ccc/></aaa>"));
		StreamResult xmlOutput = new StreamResult(new StringWriter());

		// Configure transformer
		Transformer transformer = TransformerFactory.newInstance().newTransformer(); // An
																						// identity
																						// transformer
		// transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
		// "testing.dtd");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(xmlInput, xmlOutput);

		System.out.println(xmlOutput.getWriter().toString());
	}
}
