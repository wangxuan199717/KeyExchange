/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CuckooSearch.Main;


import CuckooSearch.CS.CuckooSearch;

public class Main {
    public static void main(String[] args) {
        CuckooSearch cs = new CuckooSearch(15, 512,-512, 2,0.25); //MAX GENERAION, UPPER BOUND, LOWER BOUND,DIMENSIION, PA
        cs.debugCuckooEgg();
        cs.doSearch(100000);

        for(int i=0;i<cs.getMostOptimalNest().getCuckoo_eggs().size();i++){
            System.out.println("sol "+(i+1)+" : "+cs.getMostOptimalNest().getCuckoo_eggs().get(i).getEgg());    
        }
        System.out.println("fitness : "+cs.getMostOptimalNest().getFitness());
    }
}
