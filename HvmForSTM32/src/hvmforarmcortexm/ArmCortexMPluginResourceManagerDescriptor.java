package hvmforarmcortexm;

import java.util.Arrays;

import hvmforarmcortexm.ui.HvmForArmCortexArmProperties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import extensions.BundleResourceManagerDescriptor;

public class ArmCortexMPluginResourceManagerDescriptor implements
		BundleResourceManagerDescriptor {

	public ArmCortexMPluginResourceManagerDescriptor() {
	}

	@Override
	public String getBundleName() {
		return Activator.PLUGIN_ID;
	}

	@Override
	public String[] getResourceNames(IProject project) {
		IEclipsePreferences node = new ProjectScope(project)
				.getNode(Activator.PLUGIN_ID);
		boolean useAlternativeMakeFile = node.getBoolean(
				HvmForArmCortexArmProperties.USE_ALTERNATIVE_MAKEFILE,
				HvmForArmCortexArmProperties.USE_ALTERNATIVE_MAKEFILE_DEFAULT);

		String[] out = { Activator.FOLDER + "/natives_armCortexM.c",
				Activator.FOLDER + "/stm32_flash.ld",
				Activator.FOLDER + "/armCortexM_interrupt.s" };

		if(!useAlternativeMakeFile) {
			out = Arrays.copyOf(out, out.length + 1);
			out[out.length - 1] = Activator.FOLDER + "/Makefile";
		}
		return out;
	}



}
