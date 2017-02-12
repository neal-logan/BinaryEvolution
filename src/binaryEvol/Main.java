package binaryEvol;

/**
 *
 * @author neal
 */
public class Main {
    
    
    public static void main(String[] args) {
        
//        String a = "thequickbrownfoxjumpedoverthelazydog";
//        String b = "thequickgreyowlflewoverthelazyfoxandanotherdog";
        
        String a = "The email vcbmndfgssdasdgaghaddress used by Bgasdfaeertyejhcvlackboaasdfasdfrd asdcfommguhfdddnghdfihfcation is your official UA sdfasissued email address";
        String b = "address usasdfeasdfdffd byasdf Blacasgdahaa fordfgsdfsdfgsg commwerwgsdfication iasdfs yourgsdfhdddddd official UA issued"
                + "(UAUsername@alaska.edu) sdfgcannoasdftewrt be chnged to a bcfprddddddddeferred emaisdfjghkertl addresasdfs whin";
        
        
        Population pop = new Population(a, b, 100, 200);
        int i = 0;
        while(i < 10000) {
            pop.runOneGeneration();
            pop.textDisplay();
            i++;
        }
        
    }
    
    
    
    
    
    
}
