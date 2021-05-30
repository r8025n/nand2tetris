<h1>Nand to Tetris</h1>

This repo contains my own implementation of project based two part online course Nand to Tetris. The purpose of this course is to get familiar with basic computer architecture and working process of overlying system level softwares like compiler and OS in a comparatively easier project based approach.

<h1>First Part:</h1>
In the first part it is mostly digital logic design. By designing logic gates using course specific hardware definition language,had to make an basic logic gates, counter,adder, mux demux, ram etc. and finally a simpler arithmatic logic unit which is capable of add, subtraction,reset, select etc. operation on 16 bit integer values only. As the final project it was required to build an assembler for the course specific assembly language. This course gives a good understandin of basic computer architecture.
 
 <h1>Second Part:</h1>
 Second part of this course has two parts, building a simple two tier compiler for course specific HACK programming language and building an very basic operating system. The compiler is a bit simpler version of other real world compilers for languages like JAVA or C#. This compiler uses an stack based virtual machine like JVM. To make this compiler, it was required to build a translator which translates the intermediate virtual machine language to course specific assembly language. And second part of the compiler requirement was to make a translator that translates course specific JACK programming language to intermediate virtual machine language. This two part completes the compiler. This course gives a good understanding of how basic compiler and operating system works. It also gives a basic understanding of virtual machine's stack based language how they works.
 
 <h1>What I have done so far:</h1>
 <h3>1. Assembler (written in C):</h3> 
 	An assembler to convert project specific HACK assembly language to binary.
	<h3>2. VMTranslator (written in JAVA):</h3>
 		A translator to translate stack based virtual machine language to assembly language.
	<h3>3. ShooterJack (written in JACK):</h3>
 		A very simple shooting game where a gun shoots a moving object written in course specific object oriented programming language JACK. Its purpose was to get familiar with JACK proogramming language.
 <h3>4. Syntax Analyzer (Written in JAVA):</h3>
 		A syntax analyzer which tokenize all the meaningful JACK programming syntax form any code written in JACK, and assign them to their specific types which are variables, datatype, integer, string, operator, array, expression, term etc. This is done as the first part of compilation process.
	<h3>5. Compiler (yet to complete):</h3>
 		In this part the compiler will be completed.
 <h3>6. Operating System (yet to complete):</h3>
