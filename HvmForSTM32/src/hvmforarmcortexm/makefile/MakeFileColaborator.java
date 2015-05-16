package hvmforarmcortexm.makefile;

import hvmforarmcortexm.ui.HvmForArmCortexArmProperties;
import hvmforstm32.Activator;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import extensions.ArchitectureDependentCodeGenerator;

public abstract class MakeFileColaborator implements
		ArchitectureDependentCodeGenerator {

	private IEclipsePreferences node;
	private String value;
	private boolean useAlternativeWorkspace;

	@Override
	public void tokenDetected(IProject project, String file, OutputStream stream)
			throws IOException {

		if (node == null) {
			node = new ProjectScope(project).getNode(Activator.PLUGIN_ID);
			value = getValue();
			useAlternativeWorkspace = node
					.getBoolean(
							HvmForArmCortexArmProperties.USE_ALTERNATIVE_MAKEFILE,
							HvmForArmCortexArmProperties.USE_ALTERNATIVE_MAKEFILE_DEFAULT);
		}

		if (!useAlternativeWorkspace && file.contains("Makefile")) {
			stream.write(value.getBytes(), 0, value.getBytes().length);
		}

	}

	protected String getValue() {
		return node.get(this.preference(), this.defaultValue());
	}
	
	protected IEclipsePreferences getPreferences() {
		return this.node;
	}

	@Override
	public abstract String token();

	public abstract String preference();

	public abstract String defaultValue();

}
