/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lr1_parser;

import lr1_parser.grammar.Production;
import lr1_parser.grammar.Symbol;

/**
 *
 * @author admin
 */
public class Item {
    public Production prod;
    public Symbol lookahead;
    public int dotPos;
    
    public Item(Item param){
        this.prod = param.prod;
        this.dotPos = param.dotPos;
        this.lookahead = param.lookahead;
    }
    public Item(Production production,int dotPos, Symbol lookahead){
        this.prod = new Production(production);
        this.dotPos = dotPos;
        this.lookahead = lookahead;
    }
    
    public Item(Production production, int dotPos){
        this.prod = new Production(production);
        this.dotPos = dotPos;
        this.lookahead = new Symbol("#");
    }
    
    public boolean isNull(){
        return (prod.isNull());
    }
    
    public void Display(){
        System.out.printf("(" + prod.toString()+ "\t dotPos = " + this.dotPos + ", " + this.lookahead.toString() + ")");
    }
    
    @Override
    public boolean equals(Object object)
    {
        if(object==null) return false;
        
        Item item = (Item)object;
        return this.dotPos == item.dotPos && this.lookahead.equals(item.lookahead) && this.prod.equals(item.prod);
    }    
    
    @Override
    public String toString(){
        return "( " + prod.toString()+ "\t dotPos =" + this.dotPos + ", " + this.lookahead.toString() + " )";
    }

}
