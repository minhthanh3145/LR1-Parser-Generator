
# Implementation of LR(1) shift-reduce parser generator.
- Visit my [blog post](https://dafuqisthatblog.wordpress.com/2017/10/12/compiler-theories-parser-bottom-up-parsers-slr-lr1-lalr/) for the theories of bottom-up parsers.
- Prequel: [LR(0) shift-reduce parser generator](https://github.com/minhthanh3145/LR0_Parser_Generator).

# Basic info
- **OS**: Windows.
- **Language**: Java.
- **Third library**: Google Guava. The library is included in the repository. However you have to manually add the JAR file into the library yourself.
- **Java competency**: Experienced.
- **Prerequesties**: A machine that is able to run java programs and JDK 1.8 or higher ( to use GUAVA ).

## Insights.
- **Null is different from empty**. More specifically, when you **NEW** an object it is impossible for it to be null. If you use **return new Object(param)** in a function then the returned value is never null. Also in practice you usually are concern with the status of the returned value ,i.e. wether the returned value is usable. I mistakenly homogenized the usability, null and empty together and thus wrote the condition **if(returnedValue!=null)** which luckily only led to an extra member in the list while adding. Call isEmpty() solved the problem.

- **In theory, theory and practice are the same. In practice, they are not**. For example, the concept of **FIRST** and **FOLLOW** can be easily explained on paper in less than half a paper. Writing the actual function that returns the correct set of values takes nearly a hundred lines of codes. It dawned on me how edifying the process of implementing what i learned is.

- **Parsers are really fun**. The feeling when you compile and your code produces the desired output is probably universally pleasant among developers. But with parser, it is also accompanied with a certain sense of magic. The input consists of simply a grammar description and a sentence. One output is simply wether the sentence belongs to the grammar. Building a parse tree is the primary output but trivial once the classification is done. The black box seems almost elegant and only the creator can fully appreciate the inner workings.

- **Everything has a price**. Implementing LR0 parser generator was a breeze. Implementing LR1 parser generator was miles from easy. The difficulty of implementation is proportional to the subset of languages covered by each parser. Just like talking is easy since it only leaves sensational traces that soon become distorted by recalling, while doing is exponentially harder because it carves into the very fabric of reality. Lol, too metaphysical there. 

## What does this program do ?
- It takes a **LR(1**) grammar description and an input and decides wether the input belongs to the language described by the grammar.

**Tasks finished**:
- [x] Finish the project. ( it generates correct output ) :relieved:
- [ ] Error report. ( shift/reduce conflicts and reduce/reduce conflicts detection ) -- too lazy :innocent:
- [ ] Reconsider the data structures and functions organization.  -- not interested

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

