package hvmforarmcortexm.makefile;

import hvmforarmcortexm.ui.HvmForArmCortexArmProperties;

public class OutputNameMakefile extends MakeFileColaborator {

	@Override
	public String token() {
		return "PROJ_NAME=";
	}

	@Override
	public String preference() {
		return HvmForArmCortexArmProperties.OUTFILE;
	}

	@Override
	public String defaultValue() {
		return HvmForArmCortexArmProperties.OUTFILE_DEFAULT;
	}

}
