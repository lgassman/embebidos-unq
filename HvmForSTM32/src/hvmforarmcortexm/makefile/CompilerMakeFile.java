package hvmforarmcortexm.makefile;

import hvmforarmcortexm.ui.HvmForArmCortexArmProperties;

public class CompilerMakeFile extends MakeFileColaborator{

	@Override
	public String token() {
		return "CC=";
	}

	@Override
	public String preference() {
		return HvmForArmCortexArmProperties.COMPILER;
	}

	@Override
	public String defaultValue() {
		return HvmForArmCortexArmProperties.COMPILER_DEFAULT;
	}

}
