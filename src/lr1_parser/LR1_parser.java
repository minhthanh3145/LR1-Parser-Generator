/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lr1_parser;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import java.io.File;
import primary_interface.Parser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import lr1_parser.grammar.Symbol;



/**
 *
 * @author admin
 */
public class LR1_parser {

    public static void main(String[] args) throws IOException {
            
        Parser parser = new Parser();
        parser.initialize();
        
        ArrayList<Symbol> input =  read("input.txt");  // Read the input file\        
        parser.parsing(input);  
    }
    
    public static ArrayList<Symbol> read(String name) throws IOException {
    
        File file = new File(name);        
        CharSource source = Files.asCharSource(file, Charsets.UTF_8);             
        String result = source.read().replaceAll("\r\n"," "); // replace all end line or new line with space            
        ArrayList<Symbol> res = new ArrayList<>();        
        ArrayList<String> temp = new ArrayList<>(Arrays.asList(result.split(" ")));
        
        for(String str: temp){
            res.add(new Symbol(str));
        }
   
        return res;
    }   
    
}
