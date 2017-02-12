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
        
        String a = "The email address used by Blackboaasdfasdfrd asdcfommguhfdddnghdfihfcation is your official UA sdfasissued email address";
        String b = "address useasdfdffd by Blaca fordfgsdfg commwerwgsdfication is yourdddddd official UA issued"
                + "(UAUsername@alaska.edu) sdfgcannoasdftewrt be chnged to a prddddddddeferred emaisdfjghkertl addresasdfs whin";
        
        
        Population pop = new Population(a, b, 1000, 200);
        int i = 0;
        while(i < 100) {
            pop.runOneGeneration();
            pop.textDisplay();
            i++;
        }
        
    }
    
    
    
    
    
    
}
