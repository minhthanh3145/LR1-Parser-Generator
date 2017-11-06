/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package primary_interface;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import java.util.List;
import java.util.Set;
import lr1_parser.Item;
import lr1_parser.Pair;
import lr1_parser.State;
import lr1_parser.Util;
import lr1_parser.grammar.Production;
import lr1_parser.grammar.Symbol;


/**
 *
 * @author admin
 */
public class Parser {
    Util assistant = new Util();
    ArrayList<State> automata;
    ArrayList<Production> listOfProductions;
    ArrayList<Symbol> alphabet;
    ArrayList<Pair> first;
    ArrayList<Pair> follow;
    ArrayList<State> states;
    Stack<State> stack;
    Table<Integer, Symbol, String> table = HashBasedTable.create(); // Parsing table ( stateNumber, symbol, actionString )
    int numberOfStartProductions = 1;
    
    
    public void Read() throws IOException{
        String line;
        listOfProductions = new ArrayList<>();
        alphabet = new ArrayList<>();
        try {
            BufferedReader bufferreader = new BufferedReader(new FileReader("grammar.txt"));
            
            line = bufferreader.readLine(); // read the first line ( indicating the number of start symbol
                                            // productions ) 
            numberOfStartProductions = Integer.parseInt(line);
            line = bufferreader.readLine(); // start reading the first productions to the end of file
            
            while (line != null) {     
                listOfProductions.add(new Production(line.split(":")[0], line.split(":")[1]));
                line = bufferreader.readLine();
            }           
            
            for(Production prod: listOfProductions){ // add all symbols into the alphabet
                if(!alphabet.contains(prod.lhs)) alphabet.add(prod.lhs);
                for(Symbol symb: prod.rhs){
                    if(!alphabet.contains(symb)) alphabet.add(symb);
                }
            }
            if(!alphabet.contains(new Symbol("#)"))) alphabet.add(new Symbol("#")); // add EPSILON ( # ) in case
                                                                                    // grammar doesnt specify # s

        } catch (IOException ex) {} // catch something             
        
    }        
    
    public State Goto(State state, Symbol symb){
        
        ArrayList<Item> init = new ArrayList<>();
        
        for(Item item: state.items){
            if(item.dotPos + 1 <= item.prod.rhs.size()){
                if(item.prod.rhs.get(item.dotPos).equals(symb)){
                    init.add(new Item(item.prod, item.dotPos + 1, item.lookahead));
                }
            }
        }

        return Closure(init);
    }
    
    public State Closure(ArrayList<Item> init){
        ArrayList<Item> setOfItems = new ArrayList<>();
        setOfItems.addAll(init);
        
        for(int i=0;i<setOfItems.size();i++){
            Item item = setOfItems.get(i);
            // For an initial item A -> a.Bc
            for(Production prod: listOfProductions){
             
                if(item.dotPos + 1 <= item.prod.rhs.size()){

                    if(item.prod.rhs.get(item.dotPos).equals(prod.lhs)){ // if there exists B -> e 

                        // Computing FIRST(c) for lookaheads
                       List<Symbol> partitionList = item.prod.rhs.subList(item.dotPos + 1, item.prod.rhs.size());

                       ArrayList<Symbol> partitionArrayList = new ArrayList<>();
                       partitionArrayList.addAll(partitionList);
                       partitionArrayList.add(new Symbol(item.lookahead));

                       // FIRST(c) 
                       ArrayList<Symbol> FirstOfPartition = findFirstOfRHS(partitionArrayList);

                       for(Symbol symb: FirstOfPartition){
                           Item newItem = new Item(prod, 0, symb);
                           
                           if(!setOfItems.contains(newItem)){
                               setOfItems.add(newItem);
                           }
                       }
                        
                    }


                }
            }
        }
        return new State(setOfItems);
    }   
    
    public boolean nullableProduction(Production prod){
        if(prod.rhs.size() == 1 && prod.rhs.contains(new Symbol("#")))
            return true;
        
        boolean matched = false;
        
        for(Symbol symb: prod.rhs){ // if this symbol can match one of the lhs of the productions, then 
                                    // matched is on, otherwise prod.rhs contains all terminal and thus not nullable
            for(Production thisProd: listOfProductions){
                // if one of the symbols on rhs is terminal then it's not nullable 
                if(assistant.isTerm(symb) && !symb.equals(new Symbol("#"))) return false;
                
                if(thisProd.lhs.equals(symb)){
                    matched = true; // signals that prod.rhs doesn't contain all terminals 
                    if(!nullableProduction(thisProd)) // return false immediately when one of the rhs symbol is not
                        return false;                   // nullable
                }                    
            }                
        }
        return matched; // either matched is false and prod is not nullable, or else matched is true and 
                        // subsequent calls to nullableProduction(thisProd) will return false if all symbols of prod.rhs is
                        // not nullable, if no call to nullableProduction(thisProd) returns false, prod is nullable in which case
                        // matched is turned on. Therefore we just have to return 'matched'

    }
    
    public void computeFirst(){
        first = new ArrayList<>();         
        for(Symbol symb: alphabet){ 
        // initialize FIRST(X) where X is a terminal to itself                    
            if(assistant.isTerm(symb)) {
                first.add(new Pair(symb, new ArrayList<>(Arrays.asList(symb))));                
            }else{                
                nonTermFirst(symb);
            }
        }        
    }
    
    public void nonTermFirst(Symbol symb){ //symb is a non-terminal, 
                                            // since we already initialized all FIRST of terminals to themselves
       
        if(first.contains(new Pair(symb))){ // Ignore terminal parameters     
            return;
        }
        
        ArrayList<Symbol> val = new ArrayList<>();  // val represents values of FIRST(symb)  
        
        for(Production prod: listOfProductions){
            if(prod.lhs.equals(symb)){ 
                
                if(nullableProduction(prod)){ // If there is a Production X → ε 
                    Symbol tmp = new Symbol("#");                    
                    if(!val.contains(tmp))
                        val.add(tmp); // then add ε to first(X) 
                }
                
                if(!val.containsAll(firstOfRHS(prod.rhs))) // firstOfRHS computes FIRST of an ArrayList<Symbol> rhs
                val.addAll(firstOfRHS(prod.rhs));
                
            }
        }
        
        first.add(new Pair(symb, val)); // Finally add the pair into FIRST
    }
    
    public ArrayList<Symbol> findFirstOfRHS(ArrayList<Symbol> rhs){ // similar to FirstOfRHS without having to call nonTermFirst
        ArrayList<Symbol> val = new ArrayList<>();
        
        for(int i=0;i<rhs.size();i++){ 
            Symbol rhsSymb = rhs.get(i);
            
            if(i == 0 && assistant.isTerm(rhsSymb)){ // if rhs[0] it's a terminal then return that terminal only
                val.add(new Symbol(rhsSymb));           
                return val;                             
            }
            // FIRST(rhsSymb) 
            ArrayList<Symbol> firstOfRhsSymb = first.get(first.indexOf(new Pair(new Symbol(rhsSymb)))).listOfSymbols;
            
            if(i == 0 ){        // if rhs[0] is a non-terminal and
                if(!firstOfRhsSymb.contains(new Symbol("#"))){ //  FIRST(rhsSymb)doesnt contain epsilon
                    for(Symbol tmp: firstOfRhsSymb)             // then FIRST(symb) = FIRST(rhsSymb) 
                       if(!val.contains(tmp))    
                           val.add(tmp);               
                   return val;
                }  
            }                       // otherwise, keep on considering rhs[i] 
                for(Symbol tmp: firstOfRhsSymb)  
                       if(!val.contains(tmp))    
                           val.add(tmp);              
                        
        }
        return val;
    }
    
    public ArrayList<Symbol> firstOfRHS(ArrayList<Symbol> rhs){ // return FIRST(rhs)
        
        ArrayList<Symbol> val = new ArrayList<>();
        boolean nullable = true;

        for(int i=0;i< rhs.size();i++){          // If there is a Production X → Y1Y2..Yk 
            Symbol rhsSymb = rhs.get(i);

                             // compute FIRST(Yi) if Yi is not a terminal
            if(!assistant.isTerm(rhsSymb)) nonTermFirst(rhsSymb); 
            else {          // But if Y1 is a terminal, then add Y1 to FIRST(X) and done
                if(i==0 && !val.contains(rhsSymb))
                    val.add(rhsSymb);
                return val;
            }                    

            // rhsSymbFirst holds FIRST(Yi)
            ArrayList<Symbol> rhsSymbFirst = first.get(first.indexOf(new Pair(new Symbol(rhsSymb)))).listOfSymbols;                                  
           
            
            if(i==0){                                          // if Y1 is not a non-terminal and               
               if(!rhsSymbFirst.contains(new Symbol("#"))){     //if First(Y1) doesn't contain ε
                   for(Symbol rhsSymbFirstSymb: rhsSymbFirst)   //  then FIRST(X) = FIRST(Y1) and done 
                       if(!val.contains(rhsSymbFirstSymb))    
                           val.add(rhsSymbFirstSymb);               
                   return val;
               }
            }                                             // for the remaining Yi
            for(Symbol rhsSymbFirstSymb: rhsSymbFirst)      // , add all FIRST(Yi) to FIRST(X) 
                       if(!val.contains(rhsSymbFirstSymb))  // but consider FIRST(Y(i+1)Y(i+2)....Yk) too 
                           val.add(rhsSymbFirstSymb);
            if(!rhsSymbFirst.contains(new Symbol("#")))
                nullable = false;
        }
        if(nullable) val.add(new Symbol("#"));
        return val;
    }
        
    public void computeFollow(){
        follow = new ArrayList<>();
        ArrayList<Production> tmpProd = new ArrayList<>();
        
        // Start symbol always have epsilon followed
        follow.add(new Pair(new Symbol(alphabet.get(0)), new ArrayList<>(Arrays.asList(new Symbol("#")))));
        
        // Two iterations over the alphabet to compute FOLLOW set 
        for(Symbol symb: alphabet){
            for(Production prod: listOfProductions){
                   symInTheMiddle(symb, prod);
            }                                   
        }  
        
        for(Symbol symb: alphabet){
            for(Production prod: listOfProductions){
                   symAtTheEnd(symb, prod);
            }                                   
        } 
    }
    
   // Compute FOLLOW set for B such that B is at the end of some productions A -> aB or A -> aBb where FIRST(b) = e
    public void symAtTheEnd(Symbol symb, Production production ) {
        ArrayList<Symbol> rhs = production.rhs;
        
         for(Symbol rhsSymbol: rhs){
            if(!assistant.isTerm(rhsSymbol) &&  rhsSymbol.equals(symb)){
                
                // Partition from symb till the end
                List<Symbol> partitionList = rhs.subList(rhs.indexOf(new Symbol(symb)) + 1 ,rhs.size());
                ArrayList<Symbol> partitionArrayList = new ArrayList<>();
                partitionArrayList.addAll(partitionList);                 
                
                // FOLLOW( partitionArrayList ) 
                ArrayList<Symbol> partitionFollow = findFirstOfRHS(partitionArrayList); 
                
                // if either symb is at the end or FIRST(partition from symb on) contains epsilon 
                if((rhs.indexOf(new Symbol(rhsSymbol)) == rhs.size() - 1) || partitionFollow.contains(new Symbol("#"))){
                    Symbol lhs = production.lhs;
                    
                    // FOLLOW(lhs)
                    ArrayList<Symbol> lhsFollow = follow.get(follow.indexOf(new Pair(new Symbol(lhs)))).listOfSymbols;    
                    
                    
                    // FOLLOW(rhsSymbol)
                    ArrayList<Symbol> rhsSymbolFollow = follow.get(follow.indexOf(new Pair(new Symbol(rhsSymbol)))).listOfSymbols;                    
                    
                    // FOLLOW(rhsSymbol) = U {FOLLOW(lhs)}
                    for(Symbol temp: lhsFollow){
                        if(!rhsSymbolFollow.contains(temp))
                             rhsSymbolFollow.add(temp);
                    }
                                      
                }
            }
         }
    }
    
    // Compute FOLLOW set for B such that B is in the midle of rhs of some productions A -> aBb
    public void symInTheMiddle(Symbol symb, Production production ){        
        ArrayList<Symbol> rhs = production.rhs;
        
        for(Symbol rhsSymbol: rhs){
            if(!assistant.isTerm(symb) && rhsSymbol.equals(symb)){
                
                // Partition from symb till the end
                List<Symbol> partitionList = rhs.subList(rhs.indexOf(new Symbol(symb)) + 1 ,rhs.size());
                ArrayList<Symbol> partitionArrayList = new ArrayList<>();
                partitionArrayList.addAll(partitionList);                 
                
                // FOLLOW( partitionArrayList ) 
                ArrayList<Symbol> partitionFollow = findFirstOfRHS(partitionArrayList); 
                
                // Either create a new entry in FOLLOW or append to the existing FOLLOW entry
                if(!follow.contains(new Pair(new Symbol(symb)))){
                    follow.add(new Pair(new Symbol(symb), partitionFollow));
                }else{
                    ArrayList<Symbol> curFollowOfSymb = follow.get(follow.indexOf(new Pair(new Symbol(symb)))).listOfSymbols;
                    for(Symbol pFSymb: partitionFollow){
                        if(!curFollowOfSymb.contains(new Symbol(pFSymb)))
                            curFollowOfSymb.add(new Symbol(pFSymb));
                    }
                }                                
            }
        }             
                     
    }
    
    public void initialize() throws IOException{    
        Read();
        computeFirst();
        computeFollow();
        
        ArrayList<Item> initSetOfItems = new ArrayList<>();
       
        for(int i=0;i<numberOfStartProductions;i++){
           initSetOfItems.add(new Item(listOfProductions.get(i), 0 , new Symbol("#")));       
        }
        State initState = new State(Closure(initSetOfItems));
        initState.Stamp(0);
        
        states = new ArrayList<>();        
        states.add(initState);
        
        boolean changed = true;
        
        while(changed){ // fixed-point algorithm to build the LR(1) automata
            
            int oldSize = states.size();
            for(int i=0;i<states.size();i++){
                
                if(states.get(i).visited == false){
                    
                    states.get(i).visited = true;
                    State fromState = states.get(i);                   
                    
                    for(Symbol symb: alphabet){
                        
                        String actionToTake = "";

                        State toState = Goto(fromState, symb);     
                          
                        if(!toState.isNull() && !states.contains(toState))
                        {       
                            toState.Stamp(states.size());
                            states.add(toState);                                                                                      
                        }
                                                
                        if(!toState.isNull()){                            
                            
                            if(toState.accept){
                                
                                ArrayList<Item> finalItems = new ArrayList<>();
                                finalItems = stateToFinalItem(toState);        
                                
                                for(Item finalItem: finalItems){
                                    int prodToReduceByID = itemToFinalProduction(finalItem);                                    
                                    actionToTake = "r" + prodToReduceByID;
                                    table.put(toState.index, finalItem.lookahead, actionToTake); 
                                    if(table.contains(toState.index, finalItem.lookahead)){

                                    }
                                }                                 
                            }
                            
                            if(assistant.isTerm(symb)){
                                actionToTake = "s" + toState.index;                                
                            }else{
                                actionToTake = "" + toState.index;
                            }
                            
                            table.put(fromState.index, symb , actionToTake); // Fill in the action for the entry
                        }
                      
                        
                        
                    }
                }
            }
            
            changed = (states.size() != oldSize);
        }
        
        table.put(initState.index, alphabet.get(0), "accept"); // first state and start symbol signals sucessful parsing

        for(State state: states){
            state.Display();
        }
        
    }
    
    public void parsing( ArrayList<Symbol> input ){
        
        stack = new Stack<>(); // initialize the stack
        stack.push(states.get(0));
        
        int i=0; // index of input
        
        while( i < input.size() ){
            
            String action ="none";
            State top = stack.peek();
            
            if(top.accept){
                boolean needLookahead = true;
                Symbol lookahead = input.get(i+1);                
                for(Item item: stateToFinalItem(top)){
                    if(item.lookahead.equals(new Symbol("#"))){
                        action = table.get(top.index, new Symbol("#"));
                        needLookahead = false;
                        break;
                    }
                }
                if(needLookahead)
                     action = table.get(top.index, lookahead); // extract productionID to reduce
                
                int prodToReduceByID = Character.getNumericValue(action.charAt(1));
                Production prodToReduceBy = listOfProductions.get(prodToReduceByID); // extraction production to reduce
                
                for (Symbol rh : prodToReduceBy.rhs) {          // pop states corresponding to the production's rhs
                    stack.pop();
                }
                
                State fromState = stack.peek();         // fromState being the state after popping                
                lookahead = new Symbol(prodToReduceBy.lhs); // lookahead is replaced by lhs of the production reduced                
                action = table.get(fromState.index, lookahead);   // accepting states trigger 2 actions, this is the 2nd  
                
                if(action.endsWith("accept"))break;                               
                State toState = states.get(Integer.parseInt(action)); 
                if(table.get(toState.index, new Symbol("#")).equals("accept")) break; // if ACCEPT then break out of the loop               

                stack.push(toState);                // Pushing the new state which correspond to 
                
            }else{
                Symbol curSymb = input.get(i);
                
                State toState;
                action = table.get(top.index, curSymb);
                
                if(!assistant.isTerm(curSymb))      // if it's a non-terminal
                    toState = states.get(Integer.parseInt(action)); // then action has only one digit
                else{                            // if it's a terminal
                    
                    int integer = Integer.valueOf(action.substring(1)); // then action is of "s" + number
                    
                    toState = states.get(integer);                     
                }
                
                stack.push(toState);
                i++;
            }
            
        }
        
        if(stack.peek().index == 0){
            System.out.println("Parsed successfully \n\t" + input.toString() + " does belong to the grammar described in grammar.txt");
        }else{
            System.out.println("cannot recognize this sentence, topState is "+stack.peek().index);
        }
    }

    public ArrayList<Item> stateToFinalItem(State state){ // Given an accepting state, return a final item
        if(!state.accept)
            return null;        
        
        ArrayList<Item> ret = new ArrayList<>();
        for(Item item: state.items){
            if(item.dotPos == item.prod.rhs.size()){
                ret.add(item);
            }
        }       
        return ret;
    }
    
    public int itemToFinalProduction(Item item){ // given an item, return the number of production matches that item
        if(item==null) return -1;
        
        for(Production prod: listOfProductions){
            if(prod.lhs.equals(item.prod.lhs) && prod.rhs.equals(item.prod.rhs))
                return listOfProductions.indexOf(prod);
        }     
        return -1;
    }
    
    public void Display(List<?> list){        
        System.out.println(Arrays.toString(list.toArray()));
    }

   
}
