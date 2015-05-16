package hvmforstm32;

import icecapspluginlib.AbstractNativeMethodGenerator;

public class STMNativeMethodGenerator extends AbstractNativeMethodGenerator {

	@Override
	protected String getFolderName() {
		return Activator.FOLDER;
	}
}
