/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lr1_parser;

import java.util.ArrayList;
import lr1_parser.grammar.Symbol;


/**
 *
 * @author admin
 */
public class Pair {
    public Symbol key;
    public ArrayList<Symbol> listOfSymbols; // a list of symbols in FIRST
    
    public Pair(Symbol e, ArrayList<Symbol> f){
      
        key = e; listOfSymbols = f;
    }
    public Pair(Symbol e){ 
        key = e;
    }
    
    public Symbol key(){
        return key;
    }
    public ArrayList<Symbol> value(){
        return listOfSymbols;
    }
    
    @Override
    public String toString(){
        return "SET(" + key + ") : " + listOfSymbols.toString() + "\n";                
    }
    @Override
    public boolean equals(Object object){
        Pair pair = (Pair)object;
        return (this.key.equals(pair.key));
    }
    
}
