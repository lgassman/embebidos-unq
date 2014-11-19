package hvmforarmcortexm;

import hvmforarmcortexm.ui.HvmForArmCortexArmProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import extensions.ProjectResourcesManagerDescriptor;

public class ArmCortexMPorjectResourceManagerDescriptor implements
		ProjectResourcesManagerDescriptor {

	@Override
	public Map<String, String> getResourceNames(IProject project) {
		IEclipsePreferences node = new ProjectScope(project)
				.getNode(Activator.PLUGIN_ID);

		boolean useAlternativeMakeFile = node.getBoolean(
				HvmForArmCortexArmProperties.USE_ALTERNATIVE_MAKEFILE,
				HvmForArmCortexArmProperties.USE_ALTERNATIVE_MAKEFILE_DEFAULT);

		Map<String, String> resources = new HashMap<String, String>();
		if (useAlternativeMakeFile) {
			resources.put(node.get(
					HvmForArmCortexArmProperties.ALTERNATIVE_MAKEFILE,
					HvmForArmCortexArmProperties.ALTERNATIVE_MAKEFILE_DEFAULT),
					Activator.FOLDER + "/Makefile");

		}
		return resources;

	}
}
