package hvmforstm32;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.eclipse.core.runtime.Platform;

import extensions.ArchitectureDependentCodeGenerator;

public class Stm32OsTypes implements ArchitectureDependentCodeGenerator {

	private PrintWriter userNativeFunctionWriter;
	private String oldFileContent;

	public static final Map<Type, String> types = new HashMap<Type, String>();

	static {
		// TODO, review types
		types.put(Type.BOOLEAN, "int8");
		types.put(Type.BYTE, "int8");
		types.put(Type.CHAR, "int8");
		types.put(Type.DOUBLE, "double");
		types.put(Type.FLOAT, "float");
		types.put(Type.INT, "int32");
		types.put(Type.LONG, "int64");
		types.put(Type.OBJECT, "Object");
		types.put(Type.SHORT, "int16");
	}

	@Override
	public void tokenDetected(String file, OutputStream stream)
			throws IOException {
		if (file.equals("ostypes.h")) {
			stream.write("\n#if defined(STM32)\n".getBytes());
			stream.write("#define DATAMEM\n".getBytes());
			stream.write("#define PROGMEM\n".getBytes());
			stream.write("#define RANGE\n".getBytes());
			stream.write("#define pgm_read_byte(x) *((unsigned char*)x)\n"
					.getBytes());
			stream.write("#define pgm_read_word(x) *((unsigned short*)x)\n"
					.getBytes());
			stream.write("#define pgm_read_pointer(x, typeofx) *((typeofx)x)\n"
					.getBytes());
			stream.write("#define pgm_read_dword(x) pgm_read_pointer(x, uint32*)\n"
					.getBytes());
			stream.write("typedef int int32;\n".getBytes());
			stream.write("typedef unsigned int uint32;\n".getBytes());
			stream.write("typedef unsigned int pointer;\n".getBytes());
			stream.write("#endif\n".getBytes());
		}

	}

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
			function.append("Object this = (Object)*((int32 *)(pointer)(sp));\n\n");
			;
			index = 1;
		}

		for (int j = 0; j < javaMethod.getArgumentTypes().length; j++) {
			Type key = javaMethod.getArgumentTypes()[j];
			String type = getCType(key);
			function.append(type);
			function.append(" arg");
			function.append(j);
			function.append(" = ");
			function.append("(");
			function.append(type);
			function.append(")*((int32 *)(pointer)(sp + ");
			function.append(index++);
			function.append("));\n");
		}

		function.append("\nreturn -1;\n");
		function.append("}");
		return function.toString();
	}

	protected String getSignature(String uniqueMethodIdentifier) {
		return "int16 " + uniqueMethodIdentifier + "(int32 *sp)";
	}

	private String getCType(Type key) {
		String ctype = types.get(key);
		if (ctype == null) {
			return "Object";
		}
		return ctype;
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
			file = new File(folderName + "/stm32");
			if (!file.exists()) {
				file.mkdir();
			}
			file = new File(folderName + "/stm32/user_natives.c");
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
