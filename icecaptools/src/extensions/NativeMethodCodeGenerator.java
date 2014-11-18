package extensions;

import org.apache.bcel.classfile.Method;

public interface NativeMethodCodeGenerator {

	public void startNativeFunctionAnalysis();

	/**
	 * This method is invoked for each native method used
	 * @param methodNumber An index associated to this method by Icecap-tools
	 * @param uniqueMethodIdentifier an unique identifier, this is used as Native function name 
	 * @param method The Native Method identified
	 */
	public void newUserNativeFunction(int methodNumber,String uniqueMethodIdentifier, Method method);

	public void endNativeFunctionAnalysis();

}
