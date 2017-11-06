/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lr1_parser.grammar;

import java.util.ArrayList;
import lr1_parser.Util;
import lr1_parser.Util;

/**
 *
 * @author admin
 */
public class Production {
    public Nonterminal lhs;
    public ArrayList<Symbol> rhs;
    public Util assistant = new Util();
    
    public Production(Production prod){ // copy constructor
        this.lhs = prod.lhs;
        this.rhs = prod.rhs;
    }
    
    public Production(String lhs, String rhs){ // Constructor receives 2 strings of rhs and lhs 
        
        ArrayList<String> listOfSymbols = assistant.stringToSymbolList(rhs); // Split rhs string into arrayList
        
        this.lhs = new Nonterminal(lhs); // initialize this.lhs
        this.rhs = new ArrayList<>();            
        listOfSymbols.forEach((rh) -> {
            this.rhs.add(new Symbol(rh)); // initialize this.rhs
        });
    }
    
    public Production(Symbol lhs, ArrayList<Symbol> rhs){ // Constructor receives 2 strings of rhs and lhs 
        Nonterminal term = new Nonterminal(lhs);
        this.lhs = term; 
        this.rhs = rhs;           
    }
    
    public boolean isNull(){
        return(this.lhs == null || this.rhs == null);
    }
    public void Display(){
        System.out.println(this.lhs + " -> " + assistant.symbolListToString(this.rhs));
    }    
    
    @Override
    public boolean equals(Object object){
        Production prod = (Production)object;
        return (this.lhs.equals(prod.lhs) && this.rhs.equals(prod.rhs));
    }
    
    @Override
    public String toString(){
        return this.lhs + " -> " + assistant.symbolListToString(this.rhs);
    }


}
