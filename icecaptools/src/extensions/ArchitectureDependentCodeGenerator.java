package extensions;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.bcel.classfile.Method;


public interface ArchitectureDependentCodeGenerator {

	/** Token to include architecture dependent code detected*/
	public void tokenDetected(String file, OutputStream Stream) throws IOException;
	
	public void startNativeFunctionAnalysis();
	public void newUserNativeFunction(int methodNumber,String uniqueMethodIdentifier, Method method);
	public void endNativeFunctionAnalysis();

}
