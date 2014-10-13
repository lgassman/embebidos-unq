package icecaptools.compiler;

import icecaptools.Activator;
import icecaptools.ConverterJob.IcecapEclipseProgressMonitor;
import icecaptools.IcecapProgressMonitor;
import icecaptools.IcecapTool;
import icecaptools.PluginResourceManager;
import icecaptools.conversion.ConversionConfiguration;
import icecaptools.extensions.ExtensionManager;

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

public class ArchitectureDependentCodeDetector extends ExtensionManager<ArchitectureDependentCodeGenerator>{

	private String currentFile;
	private OutputStream stream;
	private CharSequence token;
	int counter;


	public ArchitectureDependentCodeDetector(CharSequence token) {
		super();
		this.token = token;
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


	private void dispatchTokenDetectedEvent() {
		for (ArchitectureDependentCodeGenerator extension : this.getExtensions()) {
			try {
				extension.tokenDetected(this.currentFile, this.stream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	protected String getCodeGeneratorExtensionPoint() {
		return Activator.PLUGIN_ID
		+ ".ArchitectureDependentCodeGenerator";
	}

	@Override
	protected String getCodeGeneratorExtensionElement() {
		return "class";
	}

}
