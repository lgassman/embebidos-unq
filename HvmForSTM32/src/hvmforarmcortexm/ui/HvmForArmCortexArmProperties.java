package hvmforarmcortexm.ui;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

//TODO REFACTORME separate this file into dos: ApplicationModel and DomainModel
//TODO REFACTORME raificar en objetitos opciones para evitar logica duplicada

public class HvmForArmCortexArmProperties  extends Model implements Cloneable {

	public static String USE_ALTERNATIVE_MAKEFILE = "useAlternativeMakeFile";
	public static String ALTERNATIVE_MAKEFILE = "alternativeMakeFile";
	public static String SOURCE_FILES = "sourceFiles";
	public static String BURN_TOOL = "burnTool";
	public static String LIBRARIES = "libraries";
	public static String COMPILER = "compiler";
	public static String OUTFILE = "outFile";
	public static String OBJCOPY = "objCopy";
	public static String COMPILERFLAGS = "compilerFlags";
	
	public static boolean USE_ALTERNATIVE_MAKEFILE_DEFAULT = false;
	public static String ALTERNATIVE_MAKEFILE_DEFAULT = "";
	public static String SOURCE_FILES_DEFAULT = "";
	public static String BURN_TOOL_DEFAULT = "st-flash write $(PROJ_NAME).bin 0x8000000";
	public static String LIBRARIES_DEFAULT = "-I$(STM_COMMON)/Utilities/STM32F4-Discovery -I$(STM_COMMON)/Libraries/CMSIS/Include -I$(STM_COMMON)/Libraries/CMSIS/ST/STM32F4xx/Include -I$(STM_COMMON)/Libraries/STM32F4xx_StdPeriph_Driver/inc";
	public static String COMPILER_DEFAULT = "arm-none-eabi-gcc";
	public static String OUTFILE_DEFAULT = "application";
	public static String OBJCOPY_DEFAULT = "arm-none-eabi-objcopy";
	public static String COMPILERFLAGS_DEFAULT = "-g -O2 -Wall -Wno-unused-variable -Tstm32_flash.ld -DJAVA_HEAP_SIZE=1000 -DJAVA_STACK_SIZE=1024 -DARMCORTEXM -mlittle-endian -mthumb -mcpu=cortex-m4 -mthumb-interwork -mfloat-abi=hard -mfpu=fpv4-sp-d16 -I.";
	
	private boolean useAlternativeMakeFile = false;
	private String alternativeMakeFile = "";
	private String sourceFiles = "";
	private String burnTool = "";
	private String libraries = "";
	private String compiler = "";
	private String outFile = "";
	private String objCopy = "";
	private String compilerFlags = "";

	public String getCompiler() {
		return compiler;
	}

	public void setCompiler(String compiler) {
		String old = this.compiler;
		this.compiler = compiler;
		this.firePropertyChange("compiler", old, this.compiler);
	}

	public String getOutFile() {
		return outFile;
	}

	public void setOutFile(String outFile) {
		String old = this.outFile;
		this.outFile = outFile;
		this.firePropertyChange("outFile", old, this.outFile);
	}

	public String getObjCopy() {
		return objCopy;
	}

	public void setObjCopy(String objCopy) {
		String old = this.objCopy;
		this.objCopy = objCopy;
		this.firePropertyChange("objCopy", old, this.objCopy);
	}

	public String getCompilerFlags() {
		return compilerFlags;
	}

	public void setCompilerFlags(String compilerFlag) {
		String old = this.compilerFlags;
		this.compilerFlags = compilerFlag;
		this.firePropertyChange("compilerFlags", old, this.compilerFlags);
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
		this.setCompiler(preferences.get(COMPILER, COMPILER_DEFAULT)) ;
		this.setCompilerFlags(preferences.get(COMPILERFLAGS, COMPILERFLAGS_DEFAULT)) ;
		this.setObjCopy(preferences.get(OBJCOPY, OBJCOPY_DEFAULT)) ;
		this.setOutFile(preferences.get(OUTFILE, OUTFILE_DEFAULT)) ;
	}
		
	public void restoreDefault(IEclipsePreferences preferences) {
		this.setUseAlternativeMakeFile(USE_ALTERNATIVE_MAKEFILE_DEFAULT);
		this.setAlternativeMakeFile(ALTERNATIVE_MAKEFILE_DEFAULT);
		this.setSourceFiles(SOURCE_FILES_DEFAULT);
		this.setBurnTool(BURN_TOOL_DEFAULT);
		this.setLibraries(LIBRARIES_DEFAULT) ;
		this.setCompiler(COMPILER_DEFAULT);
		this.setCompilerFlags(COMPILERFLAGS_DEFAULT);
		this.setObjCopy(OBJCOPY_DEFAULT);
		this.setOutFile(OUTFILE_DEFAULT);
	}
	
	private void fillPreferences(IEclipsePreferences preferences) {
		preferences.putBoolean(USE_ALTERNATIVE_MAKEFILE, this.isUseAlternativeMakeFile());
		preferences.put(ALTERNATIVE_MAKEFILE, this.getAlternativeMakeFile());
		preferences.put(SOURCE_FILES, this.getSourceFiles());
		preferences.put(BURN_TOOL, this.getBurnTool());
		preferences.put(LIBRARIES, this.getLibraries()) ;
		preferences.put(COMPILER, this.getCompiler()) ;
		preferences.put(COMPILERFLAGS, this.getCompilerFlags()) ;
		preferences.put(OBJCOPY, this.getObjCopy()) ;
		preferences.put(OUTFILE, this.getOutFile()) ;
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
