
# Put your source files here (or *.c, etc)
#SRCS=main.c system_stm32f4xx.c
SRCS=../classes.c ../icecapvm.c ../methodinterpreter.c ../methods.c ../gc.c ../natives_allOS.c  ../allocation_point.c ../print.c natives_armCortexM.c  user_natives.c armCortexM_interrupt.s

# Binaries will be generated with this name (.elf, .bin, .hex, etc)
PROJ_NAME=hvmAppArmCortexM


# Normally you shouldn't need to change anything below this line!
#######################################################################################

CC=arm-none-eabi-gcc
OBJCOPY=arm-none-eabi-objcopy

#Estos son los flags del documento de referencia de hvm
# -Wall -pedantic -Werror -g -Os -DJAVA_HEAP_SIZE=1000 -DJAVA_STACK_SIZE=1024

CFLAGS  = -g -O2 -Wall -Wno-unused-variable -Tstm32_flash.ld -DJAVA_HEAP_SIZE=1000 -DJAVA_STACK_SIZE=1024 -DARMCORTEXM
CFLAGS += -mlittle-endian -mthumb -mcpu=cortex-m4 -mthumb-interwork
CFLAGS += -mfloat-abi=hard -mfpu=fpv4-sp-d16
CFLAGS += -I.

# Include files from STM libraries
CFLAGS += -I$(STM_COMMON)/Utilities/STM32F4-Discovery
CFLAGS += -I$(STM_COMMON)/Libraries/CMSIS/Include -I$(STM_COMMON)/Libraries/CMSIS/ST/STM32F4xx/Include
CFLAGS += -I$(STM_COMMON)/Libraries/STM32F4xx_StdPeriph_Driver/inc

# add startup file to build
OBJS = $(SRCS:.c=.o)


.PHONY: proj

all: proj

proj: $(PROJ_NAME).elf

$(PROJ_NAME).elf: $(SRCS)
	$(CC) $(CFLAGS) $^ -o $@ 
	$(OBJCOPY) -O ihex $(PROJ_NAME).elf $(PROJ_NAME).hex
	$(OBJCOPY) -O binary $(PROJ_NAME).elf $(PROJ_NAME).bin

clean:
	rm -f *.o $(PROJ_NAME).elf $(PROJ_NAME).hex $(PROJ_NAME).bin

# Flash the STM32F4
burn: proj
	st-flash write $(PROJ_NAME).bin 0x8000000