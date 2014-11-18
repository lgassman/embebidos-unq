package hvmforarmcortexm;
import extensions.ResourceManagerDescriptor;


public class ArmCortexMResourceManagerDescriptor extends ResourceManagerDescriptor {

	public ArmCortexMResourceManagerDescriptor() {
		super(Activator.PLUGIN_ID, Activator.FOLDER + "/natives_armCortexM.c",Activator.FOLDER + "/Makefile",Activator.FOLDER + "/stm32_flash.ld", Activator.FOLDER + "/armCortexM_interrupt.s");
	}


}
