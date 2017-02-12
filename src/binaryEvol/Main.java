/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binaryEvol;

/**
 *
 * @author neal
 */
public class Main {
    
    
    public static void main(String[] args) {
        
//        String a = "thequickbrownfoxjumpedoverthelazydog";
//        String b = "thequickgreyowlflewoverthelazyfoxandanotherdog";
        
        String a = "The email address used by Blackboaasdfrd communication is your official UA sdfasissued email address";
        String b = "address useasdfdffd by Blaca fordfgsdfg commwerwgsdfication is your official UA issued"
                + "(UAUsername@alaska.edu) sdfgcannoasdftewrt be chnged to a preferred emaisdfjghkertl addresasdfs whin";
        
        
        Population pop = new Population(a, b, 100, 200);
        int i = 0;
        while(i < 1000) {
            pop.runOneGeneration();
            pop.textDisplay();
            i++;
        }
        
    }
    
    
    
    
    
    
}
