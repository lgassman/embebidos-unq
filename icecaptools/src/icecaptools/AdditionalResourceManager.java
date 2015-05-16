package icecaptools;

import org.eclipse.core.resources.IProject;

import icecaptools.conversion.ConversionConfiguration;
import icecaptools.extensions.ExtensionManager;
import extensions.BundleResourceManagerDescriptor;
import extensions.ProjectResourcesManagerDescriptor;
import extensions.ProjectResourcesManagerExtension;

public class AdditionalResourceManager  {

	private PluginResourceManagerExtensions pluginsExtensions = new PluginResourceManagerExtensions();
	private ProjectResourcesManagerExtension projectExtension = new ProjectResourcesManagerExtension();
	

	public ResourceManager createResorceManager(ConversionConfiguration config) {
		PluginResourceManager manager = new PluginResourceManager();
		if(this.pluginsExtensions.getExtensions().isEmpty() &&
				this.projectExtension.getExtensions().isEmpty()) {
			return manager;
		}
		IProject project = (IProject) config.getProjectResource().getAdapter(IProject.class);
		CompositeResourceManager composite = new CompositeResourceManager();
		composite.addResourceManager(manager);
		
	    for(BundleResourceManagerDescriptor descriptor : this.pluginsExtensions.getExtensions()) {
			composite.addResourceManager(new PluginResourceManager(descriptor.getBundleName(), descriptor.getResourceNames(project), descriptor.getAlias()));
		}
	    
	    for(ProjectResourcesManagerDescriptor descriptor : this.projectExtension.getExtensions()) {
			composite.addResourceManager(new ProjectResourceManager(project, descriptor.getResourceNames(project)));
		}
	    
	    return composite;
	}
	
}