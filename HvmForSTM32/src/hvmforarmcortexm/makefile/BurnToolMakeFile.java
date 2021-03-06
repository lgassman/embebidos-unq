package hvmforarmcortexm.makefile;

import hvmforarmcortexm.ui.HvmForArmCortexArmProperties;

public class BurnToolMakeFile extends MakeFileColaborator {

	@Override
	public String token() {
		return "BURN_TOOL=";
	}

	@Override
	public String preference() {
		return HvmForArmCortexArmProperties.BURN_TOOL;
	}

	@Override
	public String defaultValue() {
		return HvmForArmCortexArmProperties.BURN_TOOL_DEFAULT;
	}

}
