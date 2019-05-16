
# Implementation of LR(1) shift-reduce parser generator.
- Visit my [blog post](https://dafuqisthatblog.wordpress.com/2017/10/12/compiler-theories-parser-bottom-up-parsers-slr-lr1-lalr/) for the theories of bottom-up parsers.
- Prequel: [LR(0) shift-reduce parser generator](https://github.com/minhthanh3145/LR0_Parser_Generator).

# Basic info
- **OS**: Windows.
- **Language**: Java.
- **Third library**: Google Guava. The library is included in the repository. However you have to manually add the JAR file into the library yourself.
- **Java competency**: Experienced.
- **Prerequesties**: A machine that is able to run java programs and JDK 1.8 or higher ( to use GUAVA ).

## What does this program do ?
- It takes a **LR(1**) grammar description and an input and decides wether the input belongs to the language described by the grammar.

## The parser consists of the following packages that in turn consist of the designated classes:
- **lr1_parser**:
  - **grammar**: Includes classes of Nonterminal, Terminal, Symbol, Production which do what their names suggest.
  - Also includes classes of **Item**, **Pair**, **State**, **Util** and **LR1_parser**.
    - **LR1_parser** is the main class of the program.
    - **Item** consists of a production and a lookahead symbol, equivalent to an LR(1) item.
    - **State** has a set of items.
    - **Util** means utility which contains helper functions. ( Although there are many helper functions in other classes too, ... inelegant, i guess ).
    - **Pair** represents a pair of Symbol and a list of Symbol. For example: SET(a) = { a,b,c,d } ( a is the first component, (a,b,c,d) is the second ).

- **primary_interface**: What we see on the surface of a parser.
  - Includes **Parser**, **Stack**:
    - **Parser** handles most of the work, like building automata, filling the action table, manipulating the stack in accordance with the table.
    - **Stack** is a generic data structure, but Stack<State> is used in parser. 


-**Test files**: 
- grammar.txt has the first line indicating the number of start symbol productions, followed by indicated start symbol productions and the remaining productions.
- input.txt contains the input.

