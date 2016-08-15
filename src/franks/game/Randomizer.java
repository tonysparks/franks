/*
 * see license.txt
 */
package franks.game;

import java.util.Random;

/**
 * @author Tonys
 *
 */
public class Randomizer {

    class CountedRandom extends Random {        
        /**
         * UID
         */
        private static final long serialVersionUID = -1556357862929234022L;

        public CountedRandom(long seed) {
            super(seed);
        }
        
        @Override
        public int next(int bits) {
            iteration += 1;
            return super.next(bits);
        }
    }
    
    private long iteration;
    private long startingSeed;
    private CountedRandom rand;

    public Randomizer() {
        this(new Random().nextLong(), 0);
    }
    
    /**
     * @param seed
     * @param iteration
     */
    public Randomizer(long seed, long iteration) {
        this.rand = new CountedRandom(seed);
        this.startingSeed = seed;
        for(long i = 0; i < iteration; i++) {
            this.rand.next(0);
        }
    }
    
    /**
     * @return the iteration
     */
    public long getIteration() {
        return iteration;
    }
    
    /**
     * @return the seed
     */
    public long getStartingSeed() {
        return startingSeed;
    }
    

    public int nextInt(int max) {
        return this.rand.nextInt(max);
    }
    
    /**
     * The lower the percentage, the closer to the max value this will get
     * 
     * @param max
     * @param percentage
     * @return a random number
     */
    public int nextInt(int max, double percentage) {
    	double m = (double)max * percentage;
    	int n = max - (int)m;
    	return (int)m + nextInt(Math.max(n, 1));
    }
    
    

    public static void main(String[] args) {        
        Randomizer rand = new Randomizer(7L, 0);
//        for(int i = 0; i < 10; i++) {
//            System.out.println(rand.nextInt(1_000));
//        }
//        System.out.println("===");
//        
        Randomizer rand2 = new Randomizer(rand.getStartingSeed(), rand.getIteration());        
//        for(int i = 0; i < 10; i++) {
//            System.out.println(rand2.nextInt(1_000) + " = " + rand.nextInt(1_000));
//        }
        
        for(int i = 0; i < 10; i++) {
        	System.out.println(rand2.nextInt(100, 0));
        }
    }
}
