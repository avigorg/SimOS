/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.ArrayList;

import simos.Process;

/**
 *
 * @author jefferson
 */
public class GUtil {
 
    public static Object[] queueToArray(Queue q) {
        
        ArrayList arraylist = new ArrayList<>();
        Queue auxQ = new Queue<>();
        
        while (!q.isEmpty()) {
            Object obj = q.get();
            arraylist.add(obj);
            auxQ.put(obj);
        }
        
        q.joinBefore(auxQ);
        
        Object[] array = arraylist.toArray();
        
        if (array.length == 0) {
            array = null;
        }
        
        return array;
    }
    
    public static String stringResources(Process pr) {
        
        String cadena = "";
        
        for (String res : pr.getResources()) {
            cadena += res + "   ";
        }
        
        return cadena;
    }
}
