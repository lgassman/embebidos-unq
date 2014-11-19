package extensions;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.resources.IProject;


public interface ArchitectureDependentCodeGenerator {

	/** 
	 * This method is invoked when the token: 
	 * "Token to include architecture dependent code detected" is detected
	 * into a file while this file is being copied to output folder
	 * @param file File Name
	 * @param Stream Output stream ready to use in the exact place where 
	 * 		  the token was detected. You don't must close this stream
	 * */
	public void tokenDetected(IProject project, String file, OutputStream Stream) throws IOException;
	public String token();

}
