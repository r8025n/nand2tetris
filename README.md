Nand to Tetris

This repo contains my own implementation of project based two part online course Nand to Tetris.

First Part:
In the first part it is mostly digital logic design. By designing logic gates using course specific hardware definition language,had to make an basic logic gates, counter, mux demux, ram and finally very simple arithmatic logic unit which is capable of add and subtraction operation on 16 bit integer only values. As the final project it was required to build an simple assembler for the course specific assembly language. This course gives a good understandin of basic computer architecture.
 
 Second Part:
 Second part of this course has two parts, building a simple two tier compiler for course specific HACK programming language and building an very basic operating system. The compiler is a basic version of other real world compilers for languages like JAVA or C#. This compiler uses an stack based virtual machine like JVM. To make this compiler, it was required to build a translator which translates the intermediate virtual machine language to course specific assembly language. And second part of the compiler requirement was to make a translator that translates course specific JACK programming language to intermediate virtual machine language. This two part completes the compiler. This course gives a good understanding of how basic compiler and operating system works. It also gives a basic understanding of virtual machine's stack based language how they works.
 
 This repository includes:
 1. Assembler (written in C)- A simple assembler build using C programming language.
 2. VMTranslator (written in JAVA)- A translator to translate virtual machine language to assembly language.
 3. ShooterJack (written in JACK)- A very simple game written in course specific object oriented programming language JACK. Its purpose was to get familiar with      JACK proogramming language.
 4. Syntax Analyzer (Written in JAVA)- A syntax analyzer to tokenize all the meaningful JACK programming syntax form a code written in JACK, and assign them to their specific types which are variables, datatype, integer, string, operator, array, expression, term etc. This is done as a necessary part of compilation.
 5. Compiler (not yet done)- In this part the compiler is completed.
 6. Operating System (not yet done)
