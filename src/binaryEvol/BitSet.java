package binaryEvol;

/**
 *
 * @author neal
 */
public class BitSet {
    
    private boolean[] bits;
    
    public BitSet(int length) {
        bits = new boolean[length];
        for(int i = 0; i < length; i++) {
            bits[i] = false;
        }
    }
    
    public boolean get(int index) {
        return bits[index];
    }
    
    public void set(int index, boolean value) {
        bits[index] = value;
    }
    
    public void flip(int index) {
        if(bits[index]) {
            bits[index] = false;
        } else {
            bits[index] = true;
        }
    }
    
    //Start inclusive, end exclusive
    public void flip(int start, int end) {
        for(int i = start; i < end; i++) {
            flip(i);
        }
    }
    
    public int cardinality() {
        int cardinality = 0;
        for(int i = 0; i < bits.length; i++) {
            if(bits[i]) {
                cardinality++;
            }
        }
        return cardinality;
    }
    
    public int length() {
        return bits.length;
    }
    
    public BitSet deepClone() {
        BitSet bitSet = new BitSet(bits.length);
        for (int i = 0; i < bits.length; i++) {
            bitSet.set(i, bits[i]);
        }
        return bitSet;
    }
    
}
