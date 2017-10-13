	.text
	_test:
	sw    $ra, 0($sp)	##Start of Prologue for test

	subu  $sp, $sp, 4
	sw    $fp, 0($sp)
	subu  $sp, $sp, 4
	addu  $fp, $sp, 8
	subu  $sp, $sp, 0
exit_test:
	lw    $ra, 0($fp)	##Start of Epilogue for test

	move  $t0, $fp
	lw    $fp, -4($fp)	#Restore frame pointer
	move  $sp, $t0
	jr    $ra		#Return jump
	.text
	.globl main
main:
__start:
	sw    $ra, 0($sp)	##Start of Prologue for main

	subu  $sp, $sp, 4
	sw    $fp, 0($sp)
	subu  $sp, $sp, 4
	addu  $fp, $sp, 8
	subu  $sp, $sp, 4
	la    $t0, -8($fp)	#Generate Address
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	li    $t0, 1
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	lw    $t1, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	sw    $t1, 0($t0)
	sw    $t1, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $t0, 1
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	bne   $t0, 1, .L1
	subu  $sp, $sp, 0
	.data
.L2:	.asciiz "hello world!\n"
	.text
	la    $t0, .L2
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 4
	syscall
	addu  $sp, $sp, 0
.L1:
	lw    $t0, -8($fp)
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 1
	syscall
exit_main:
	lw    $ra, 0($fp)	##Start of Epilogue for main

	move  $t0, $fp
	lw    $fp, -4($fp)	#Restore frame pointer
	move  $sp, $t0
	li    $v0, 10
	syscall
