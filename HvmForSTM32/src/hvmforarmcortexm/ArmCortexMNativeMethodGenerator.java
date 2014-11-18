package hvmforarmcortexm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.eclipse.core.runtime.Platform;

import extensions.NativeMethodCodeGenerator;

public class ArmCortexMNativeMethodGenerator implements NativeMethodCodeGenerator{
	
	private PrintWriter userNativeFunctionWriter;
	private String oldFileContent;

	
	@Override
	public void newUserNativeFunction(int methodNumber,
			String uniqueMethodIdentifier, Method javaMethod) {
		if (!exist(uniqueMethodIdentifier)) {
			System.out.println("Generating code for " + uniqueMethodIdentifier);
			userNativeFunctionWriter.println(getFunctionCode(
					uniqueMethodIdentifier, javaMethod));
			userNativeFunctionWriter.flush();
		}
		else {
			System.out.println("skip Generating code for " + uniqueMethodIdentifier);			
		}
	}

	protected boolean exist(String uniqueMethodIdentifier) {
		return oldFileContent != null
				&& oldFileContent
						.contains(getSignature(uniqueMethodIdentifier));
	}

	private String getFunctionCode(String uniqueMethodIdentifier,
			Method javaMethod) {
		StringBuffer function = new StringBuffer();
		function.append("/* " + javaMethod.getName() + "\n");
		function.append(" * param : "
				+ Arrays.toString(javaMethod.getArgumentTypes()) + "\n");
		function.append(" * return: " + javaMethod.getReturnType().toString()
				+ "\n");
		function.append(" */\n");
		String signature = getSignature(uniqueMethodIdentifier);
		function.append(signature + " {\n");

		int index = 0;
		if (!javaMethod.isStatic()) {
			function.append("pointer self = (pointer)sp[0];\n\n");
			;
			index = 1;
		}

		for (int j = 0; j < javaMethod.getArgumentTypes().length; j++) {
			Type key = javaMethod.getArgumentTypes()[j];
			HvmTypeConverter type = HvmTypeConverter.forJavaType(key);
			function.append(type.getHvmType());
			function.append(" arg");
			function.append(j);
			function.append(" = ");
			function.append(type.retriveFromSp(index++));
			function.append("\n");
		}

		function.append("\n");
		HvmTypeConverter returnType = HvmTypeConverter.forJavaType(javaMethod.getReturnType());
		
		function.append(returnType.asBody());		
		function.append("\nreturn -1;\n");
		function.append("}");
		return function.toString();
	}

	protected String getSignature(String uniqueMethodIdentifier) {
		return "int16 " + uniqueMethodIdentifier + "(int32 *sp)";
	}


	@Override
	public void startNativeFunctionAnalysis() {

		// TODO review default folder or throw exception
		String folderName = Platform.getPreferencesService().getString(
				"icecaptools", "ICECAP_OUTPUTFOLDER", ".", null);

		try {
			File file = new File(folderName);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(folderName + "/" + Activator.FOLDER);
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(folderName + "/" + Activator.FOLDER + "/user_natives.c");
			if (!file.exists()) {
				file.createNewFile();
				userNativeFunctionWriter = new PrintWriter(new BufferedWriter(
						new FileWriter(file, false)));
				userNativeFunctionWriter.println("#include \"../ostypes.h\"\n");
				userNativeFunctionWriter.println("#include \"../types.h\"\n");
			} else {
				oldFileContent = readFile(file);
				userNativeFunctionWriter = new PrintWriter(new BufferedWriter(
						new FileWriter(file, true)));
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String readFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		try {
			StringBuffer buf = new StringBuffer();
			String line = null;
			do {
				line = reader.readLine();
				if (line != null) {
					buf.append(line);
				}
			} while (line != null);
			return buf.toString();
		} finally {
			reader.close();
		}
	}

	@Override
	public void endNativeFunctionAnalysis() {
		userNativeFunctionWriter.flush();
		userNativeFunctionWriter.close();
	}
}
