package hvmforarmcortexm.makefile;

import hvmforarmcortexm.ui.HvmForArmCortexArmProperties;

public class CompilerFlagMakeFile extends MakeFileColaborator{

	@Override
	public String token() {
		return "CFLAGS=";
	}

	@Override
	public String preference() {
		return HvmForArmCortexArmProperties.COMPILERFLAGS;
	}

	@Override
	public String defaultValue() {
		return HvmForArmCortexArmProperties.COMPILERFLAGS_DEFAULT;
	}

}
