package hvmforstm32;
import extensions.ResourceManagerDescriptor;


public class Stm32ResourceManagerDescriptor extends ResourceManagerDescriptor {

	public Stm32ResourceManagerDescriptor() {
		super(Activator.PLUGIN_ID, "stm32/natives_stm32.c","stm32/Makefile","stm32/stm32_flash.ld");
	}


}
