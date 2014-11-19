package extensions;

import org.eclipse.core.resources.IProject;


public interface BundleResourceManagerDescriptor {

	public String getBundleName();

	public String[] getResourceNames(IProject project);

}