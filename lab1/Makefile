###
# A simple Makefile for Lab #1
###

# define the java compiler to be used and the flags
JC = javac
FLAGS = -g -cp $(CP)
CP = /home/star/compiler/lab1/	

# define the rules


# testing 
test:Main.class
	java -cp $(CP) Main
Main.class:Main.java 
	$(JC) $(FLAGS) Main.java
Info.class:Info.java
	$(JC) $(FLAGS) Info.java
Word.class:Word.java
	$(JC) $(FLAGS) Word.java
WordCount.class:WordCount.java
	$(JC) $(FLAGS) WordCount.java
WordScanner.class:WordScanner.java
	$(JC) $(FLAGS) WordScanner.java
# clean up
clean:
	rm -f *~ *.class
