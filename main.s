format_str_0:
	.string "Hello, World.\n"
	.align 16

.globl main
main:
	leaq format_str_0(%rip), %rdi
	call printf
	mov $0, %rax
	ret
	
