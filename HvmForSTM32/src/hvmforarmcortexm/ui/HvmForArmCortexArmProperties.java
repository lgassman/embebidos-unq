package hvmforarmcortexm.ui;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

//TODO REFACTORME separate this file into dos: ApplicationModel and DomainModel
//TODO REFACTORME raificar en objetitos opciones para evitar logica duplicada

public class HvmForArmCortexArmProperties  extends Model implements Cloneable {

	private static String USE_ALTERNATIVE_MAKEFILE = "useAlternativeMakeFile";
	private static String ALTERNATIVE_MAKEFILE = "alternativeMakeFile";
	private static String SOURCE_FILES = "sourceFiles";
	private static String BURN_TOOL = "burnTool";
	private static String LIBRARIES = "libraries";

	private static boolean USE_ALTERNATIVE_MAKEFILE_DEFAULT = false;
	private static String ALTERNATIVE_MAKEFILE_DEFAULT = "";
	private static String SOURCE_FILES_DEFAULT = "sourceFiles.c";
	private static String BURN_TOOL_DEFAULT = "stvlink";
	private static String LIBRARIES_DEFAULT = "librariesDefault";

	
	private boolean useAlternativeMakeFile = false;
	private String alternativeMakeFile = "";
	private String sourceFiles = "";
	private String burnTool = "";
	private String libraries = "";

	public HvmForArmCortexArmProperties() {
	}

	public boolean isUseDefaultMakeFile() {
		return !this.isUseAlternativeMakeFile();
	}
	
	public void setIsUseDefaultMakeFile(boolean value) {
		this.setUseAlternativeMakeFile(!value);
	}
	
	public String getLibraries() {
		return libraries;
	}

	public void setLibraries(String libraries) {
		String old = this.libraries;
		this.libraries = libraries;
		this.firePropertyChange("libraries", old, this.libraries);
	}

	public String getAlternativeMakeFile() {
		return alternativeMakeFile;
	}

	public void setAlternativeMakeFile(String alternativeMakefile) {
		String old = this.alternativeMakeFile;
		this.alternativeMakeFile = alternativeMakefile;
		this.firePropertyChange("alternativeMakefile", old, this.alternativeMakeFile);
	}

	public String getSourceFiles() {
		return sourceFiles;
	}

	public void setSourceFiles(String sourceFiles) {
		String old = this.sourceFiles;
		this.sourceFiles = sourceFiles;
		this.firePropertyChange("sourceFiles", old, this.sourceFiles);

	}

	public String getBurnTool() {
		return burnTool;
	}

	public void setBurnTool(String burnTool) {
		String old = this.burnTool;
		this.burnTool = burnTool;
		this.firePropertyChange("burnTool", old, this.burnTool);

	}

	public boolean isUseAlternativeMakeFile() {
		return useAlternativeMakeFile;
	}

	public void setUseAlternativeMakeFile(boolean useAlternativeMakeFile) {
		boolean old = this.useAlternativeMakeFile;
		this.useAlternativeMakeFile = useAlternativeMakeFile;
		this.firePropertyChange("useAlternativeMakeFile", old, this.useAlternativeMakeFile);
		this.firePropertyChange("useDefaultMakeFile", !old, this.isUseDefaultMakeFile());
	}

	public void load(IEclipsePreferences preferences) {
		this.setUseAlternativeMakeFile(preferences.getBoolean(USE_ALTERNATIVE_MAKEFILE, USE_ALTERNATIVE_MAKEFILE_DEFAULT));
		this.setAlternativeMakeFile(preferences.get(ALTERNATIVE_MAKEFILE, ALTERNATIVE_MAKEFILE_DEFAULT));
		this.setSourceFiles(preferences.get(SOURCE_FILES, SOURCE_FILES_DEFAULT));
		this.setBurnTool(preferences.get(BURN_TOOL, BURN_TOOL_DEFAULT));
		this.setLibraries(preferences.get(LIBRARIES, LIBRARIES_DEFAULT)) ;
	}
		
	public void restoreDefault(IEclipsePreferences preferences) {
		this.setUseAlternativeMakeFile(USE_ALTERNATIVE_MAKEFILE_DEFAULT);
		this.setAlternativeMakeFile(ALTERNATIVE_MAKEFILE_DEFAULT);
		this.setSourceFiles(SOURCE_FILES_DEFAULT);
		this.setBurnTool(BURN_TOOL_DEFAULT);
		this.setLibraries(LIBRARIES_DEFAULT) ;
	}
	
	private void fillPreferences(IEclipsePreferences preferences) {
		preferences.putBoolean(USE_ALTERNATIVE_MAKEFILE, this.isUseAlternativeMakeFile());
		preferences.put(ALTERNATIVE_MAKEFILE, this.getAlternativeMakeFile());
		preferences.put(SOURCE_FILES, this.getSourceFiles());
		preferences.put(BURN_TOOL, this.getBurnTool());
		preferences.put(LIBRARIES, this.getLibraries()) ;
	}
	
	public void save(IEclipsePreferences preferences) {
		fillPreferences(preferences);
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			throw new RuntimeException(e);
		}
	}
		
	
}
