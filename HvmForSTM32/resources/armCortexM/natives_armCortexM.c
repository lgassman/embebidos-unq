#include "../ostypes.h"
/*This function gets called as the
first thing upon entry into
main. It has been used on some platforms to
copy initialized data into the correct segments. It can be left empty. It is
only called once
*/
void init_compiler_specifics(void) {
}

/*
This function gets called before starting the
VM. It is recalled if the VM is restarted. It can be left empty
*/
void initNatives(void) {
}

int32 java_stack[1024];
/*This function gets called be-
fore entering the VM. It should return a pointer to a RAM memory area
that is used as the Java stack
*/
int32* get_java_stack_base(int16 size) {
   return java_stack;
}

/*Refer
to Section 5.2. These functions start a timer updating the
volatile
uint8 systemTick
global variable. It prevents to eager scheduling if a
scheduler has been started. Only pertains to SCJ programs
*/
void start_system_tick(void) {
}

void stop_system_tick(void) {
}

/*. Only used by the
regression testing system. Can be left empty
*/
void mark_error(void) {
}

void mark_success(void) {
}
/*
This function is used for implementing hardware objects. It
must be implemented to write lsb to address + offset. The offset
is in bits. On most architectures this can be very easily implemented
as a normal pointer deference. But on some architectures special pur-
pose instructions must be executed to read/write to IO space. The other
read/writeXXXToIO
are similar but for other data types
*/
void writeByteToIO(pointer address, unsigned short offset, unsigned char lsb) {
}

/*Should block until the volatile uint8 systemTick_global_variable_gets updated. Can
either be a busy loop or more intelligently implemented using some special
purpose power saving instructions

*/
int16 nvm_RealtimeClock_awaitNextTick(int32 *sp) {
	return 0;
}

/*Must return the resolution of the system tick timer as started in the function
void start_system_tick(void). The number is returned as an
uint32 containing the number of nanoseconds between two system ticks. Refer to
Section 4.2 on how to implement and return values from native methods*/
int16 n_vm_RealtimeClock_getNativeResolution(int32 *sp) {
	return 0;
}

/*
Refer to the PC
host implementation in
nativescj.c*/
int16 n_vm_RealtimeClock_getNativeTime(int32 *sp){
	return 0;
}

/*Must return the value of the stack
pointer as it was when calling the function. Refer to existing implemen-
tations*/
/*pointer* get_stack_pointer(void) {
	return 0;
}*/

/*
Must set the value of the stack pointer
to the value of the global variable
stackPointer
and return to the caller.
Refer to existing implementations
*/
/*void set_stack_pointer(void) {
}
*/
/*Prints a byte. Used to print mes-
sages the console. Can be left empty. If a UART is available this can be
used for printing. On platforms supporting
printf
this can simply print
the byte as a char on
stdout*/
void sendbyte(unsigned char byte) {
}

/*
 If interrupts can occur
while allocating memory using
new
these functions must implement a mu-
tex around memory allocation. Can be left empty for programs not using
interrupts or if none of the interrupt handlers allocate memory (which
they are not supposed to do)
*/

void init_memory_lock(void) {}

void lock_memory(void){}

void unlock_memory(void){}

/*Estas funciones no son obligatorias*/
void n_devices_System_blink() {
}





