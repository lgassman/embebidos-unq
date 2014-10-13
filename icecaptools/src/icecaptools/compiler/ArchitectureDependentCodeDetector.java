package icecaptools.compiler;

import icecaptools.Activator;
import icecaptools.ConverterJob.IcecapEclipseProgressMonitor;
import icecaptools.IcecapProgressMonitor;
import icecaptools.IcecapTool;
import icecaptools.PluginResourceManager;
import icecaptools.conversion.ConversionConfiguration;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.Method;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.actions.WorkspaceAction;

import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaMethod;

import extensions.ArchitectureDependentCodeGenerator;

public class ArchitectureDependentCodeDetector {

	public static String CodeGeneratorExtensionPoint = Activator.PLUGIN_ID
			+ ".CodeGenerator";
	public static String CodeGeneratorElement = "class";

	private String currentFile;
	private OutputStream stream;
	private CharSequence token;
	int counter;

	private List<ArchitectureDependentCodeGenerator> extensions;

	public ArchitectureDependentCodeDetector(CharSequence token) {
		this.token = token;
		this.createExtensions();
	}

	public ArchitectureDependentCodeDetector() {
		this("/* Insert architecture dependent code here */");
	}

	public void fileStart(String file, OutputStream stream) {
		currentFile = file;
		this.stream = stream;
		counter = 0;
	}

	public void newRead(char character) {
		counter = token.charAt(counter) == character ? counter + 1 : 0;
		if (counter == token.length()) {
			dispatchTokenDetectedEvent();
			counter = 0;
		}
	}

	protected void createExtensions() {
		this.extensions = new ArrayList<ArchitectureDependentCodeGenerator>();

		IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						CodeGeneratorExtensionPoint);
		for (IConfigurationElement element : configurationElements) {
			try {
				Object executableExtension = element
						.createExecutableExtension(CodeGeneratorElement);
				this.extensions
						.add(((ArchitectureDependentCodeGenerator) executableExtension));
			} catch (CoreException e) {
				// TODO Skip and proccess another element?;
				e.printStackTrace();
			}
		}
	}

	private void dispatchTokenDetectedEvent() {
		for (ArchitectureDependentCodeGenerator extension : extensions) {
			try {
				extension.tokenDetected(this.currentFile, this.stream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void startAnalysis() {
		for (ArchitectureDependentCodeGenerator extension : extensions) {
			extension.startNativeFunctionAnalysis();;
		}
	}
	
	public void endAnalysis() {
		for (ArchitectureDependentCodeGenerator extension : extensions) {
			extension.endNativeFunctionAnalysis();
		}
	}

	public void newUserDefinedNativeMehtod(int methodNumber,
			String uniqueMethodIdentifier, Method javaMethod) {

		for (ArchitectureDependentCodeGenerator extension : extensions) {
			extension.newUserNativeFunction(methodNumber,
					uniqueMethodIdentifier, javaMethod);
		}

	}

}
