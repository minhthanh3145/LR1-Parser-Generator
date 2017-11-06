/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lr1_parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lr1_parser.grammar.Symbol;
import lr1_parser.grammar.Terminal;

/**
 *
 * @author admin
 */
public class Util {
    public boolean isTerm(Symbol symb){
        Terminal term = new Terminal(symb);
        return !term.error;
    }
    public String symbolListToString(List<Symbol> list){
        String result="";
        for(int i=0;i<list.size();i++){
            result = result +  list.get(i).toString() + " ";
        }
        return result;
    }
    
    public ArrayList<String> stringToSymbolList(String str){
        return new ArrayList<>(Arrays.asList(str.split(" ")));              
        
    }



}
