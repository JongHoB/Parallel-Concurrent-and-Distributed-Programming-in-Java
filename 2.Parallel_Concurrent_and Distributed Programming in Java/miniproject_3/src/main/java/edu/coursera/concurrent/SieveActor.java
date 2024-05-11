package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;

import java.util.ArrayList;
import java.util.List;

import static edu.rice.pcdp.PCDP.finish;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        SieveActorActor firstActor = new SieveActorActor(2);
        finish(()->{
            for(int i = 3; i <= limit; i += 2){
                firstActor.send(i);
            }
            firstActor.send(-1);
        });

        SieveActorActor lastActor = firstActor;
        int numPrimes = 0;

        while(lastActor!=null){
            numPrimes ++;
            lastActor = lastActor.getNextActor();
        }


        return numPrimes;

    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        private final List<Integer> localPrimes = new ArrayList<Integer>(); // List of primes found by this actor
        private SieveActorActor nextActor = null; // Next actor in the pipeline

        private int prime; // Prime number for this actor

        SieveActorActor(int prime){ // Constructor
            this.prime = prime;
        }

        public SieveActorActor getNextActor(){
            return nextActor;
        }

        @Override
        public void process(final Object msg) {
            final int candidate = (Integer) msg; // Received message
            if(candidate == -1){
                if(nextActor != null){
                    nextActor.send(msg); // Send the message to the next actor
                }
                return;
            }
            else{
                if(candidate % prime != 0){ // Check if the candidate is a prime
                    if(nextActor == null){
                        nextActor = new SieveActorActor(candidate); // Create a new actor
                    }
                    else{
                        nextActor.send(msg);    // Send the message to the next actor
                    }
                }
            }
        }
    }
}
