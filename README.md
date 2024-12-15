<h1>Nand to Tetris</h1>

<p>This repo contains my own implementation of project based two part online course Nand to Tetris. The purpose of this course is to get familiar with basic computer architecture and working process of overlying system level softwares like compiler and OS in a comparatively easier project based approach.</p>

<h1>First Part:</h1>
<p>In the first part it is mostly digital logic design. By designing elementary logic gates using course specific hardware definition language,had to make intermediate digital circuits and counter,adder, mux demux, ram etc. and finally a simpler arithmatic logic unit which is capable of add, subtraction,reset, select etc. operation on 16 bit integer values only. As the final project it was required to build an assembler for the course specific assembly language. This course gives a good understandin of basic computer architecture.</p>

<p>First Part Course Link: <a href = "https://www.coursera.org/learn/build-a-computer"> From Nand to Tetris - Part I<a></p>
 
<h1>Second Part:</h1>
<p>Second part of this course has two parts, building a simple two tier compiler for course specific HACK programming language and building an very basic operating system. The compiler is a bit simpler version of other real world compilers for languages like JAVA or C#. This compiler uses an stack based virtual machine like JVM. To make this compiler, it was required to build a translator which translates the intermediate virtual machine language to course specific assembly language. And second part of the compiler requirement was to make a translator that translates course specific JACK programming language to intermediate virtual machine language. This two part completes the compiler. This course gives a good understanding of how basic compiler and operating system works. It also gives a basic understanding of virtual machine's stack based language how they works.</p>

<p>Second Part Course Link: <a href = "https://www.coursera.org/learn/nand2tetris2"> From Nand to Tetris - Part II<a></p>
 
<h1>Project Summary:</h1>

<h2>Week 1:</h2>
<p>&emsp; Implemented elementary logic gates in HDL along with Multiplexer and Demultiplexer.</p>

<h2>Week 2:</h2>
<p>&emsp; Implemented a HalfAdder, a FullAdder and the ALU.</p>

<h2>Week 3:</h2>
<p>&emsp; Implemented sequential logic circuitry using D Flip-Flops. Built Registers, a Counter and RAM chips.</p>

<h2>Week 4:</h2>
<p>&emsp; Written a few programs in the HACK assembly language.</p>

<h2>Week 5:</h2>
<p>&emsp; Implemented the CPU and the complete Memory chip. Assembled the complete HACK computer architecture together. The HACK computer is now able to execute instructions written in the HACK assembly language.</p>

	
<h2>Week 6- Assembler (written in C):</h2> 
<p>&emsp; Developed an assembler to convert project specific HACK assembly language to binary.</p>

<h2>Week 7- VMTranslator (written in JAVA):</h2>
<p>&emsp; Developed a translator to translate stack based virtual machine language to assembly language.</p>

<h2>Week 8- ShooterJack (written in JACK):</h2>
<p>&emsp; Developed very simple shooting game where a gun shoots a moving object written in course specific object oriented programming language JACK. Its purpose was to get familiar with JACK proogramming language.</p>

<h2>Week 9- Syntax Analyzer (Written in JAVA):</h2>
<p>&emsp; Developed a syntax analyzer which tokenize all the meaningful JACK programming syntax form any code written in JACK, and assign them to their specific types which are variables, datatype, integer, string, operator, array, expression, term etc. This is done as the first part of compilation process.</p>

<h2>Week 10- Compiler (written in JAVA):</h2>
<p>&emsp; Developed the full functional JACK language compiler based on the Syantax Analyzer that was built as the previous project. This compiler can now tokenize any JACK program and compile them into the Virtual Machine specific VM language.</p>

