/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lr1_parser;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author admin
 */
public class State {
    public int index;
    public boolean accept = false;
    public boolean visited = false; // variable used in computing closure only
    public ArrayList<Item> items = new ArrayList<>();
    
    public State(State param){
        
        this.items.addAll(param.items);
        this.accept = param.accept;
    }
    
    public State(ArrayList<Item> setOfItems){
     
        items.addAll(setOfItems);  // to the parameters
        
        // check if this state is an accepting state
        items.stream().filter((item) -> (item.dotPos == item.prod.rhs.size())).forEachOrdered((_item) -> {
            accept = true;
        });
    }
    
    public void Stamp(int i){index = i;}
    

    public void Display(){
                
        System.out.println("State " + index + " : ");
        for(Item item: items){
            System.out.printf("%50s \n", item.toString()); // right align
        }               
        
    }
    
    public boolean isNull(){
        return items.isEmpty();
    }
    
    @Override      
    public boolean equals(Object object)
    {
        State state = (State)object;
        if(this.items.size() != state.items.size())
            return false;
        else{
            for(int i=0;i<state.items.size();i++)
            {
                if(!this.items.contains(state.items.get(i)))
                    return false;
            }
        }
        
        return true;
    }
    
}
