/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

/**
 *
 * @author jefferson
 * @param <T>
 */
public class Queue <T> {
	
	class Node {
	    
	    T value;
	    Node sig;
	    
	    public Node(T valor){
	        
	        this.value = valor;
	        this.sig = null;
	    }
	}
    
    Node head;
    
    public void put(T obj) {
        
        if (obj == null) {
            return;
        }
        
        if (isEmpty()) {
            head = new Node(obj);
            return;
        }
        
        Node aux = head;
        
        while (aux.sig != null) {
            aux = aux.sig;
        }
        
        aux.sig = new Node(obj);
    }
    
    public T get() {
        
        if(isEmpty()){
            return null;
        }
        
        Node first = head;
        head = head.sig;
        
        return first.value;
    }
    
    public void remove(T element) {
        
        Queue <T> queueAux = new Queue<T>();
        
        while( !isEmpty() ) {
            
            T aux = get();
            
            if (aux != element) {
                queueAux.put(aux);
            }
        }
         
        while(!queueAux.isEmpty()) {
            put(queueAux.get());
        }
        
    }
    
    public void joinBefore(Queue<T> before) {
    	
    	while(!isEmpty()) {
    		before.put(this.get());
    	}
    	
    	while (!before.isEmpty()) {
    		this.put(before.get());
    	}
    }
    
    public void moveTo(T element, Queue <T> dst) {
        
       Queue <T> queueAux = new Queue<T>();
       T aux = get();
       
       while (aux != null && aux != element) {
           
           queueAux.put(aux);
           aux = get();
       }
       
       dst.put(element);
       
       while ( !isEmpty()) {
           queueAux.put(get());
       }
       
       while( !queueAux.isEmpty() ) {
           put(queueAux.get());
       }
        
    }
    
    public boolean isEmpty() {
        return head == null;
    }
    
    public String toString() {
        
        Queue <T> queueAux = new Queue<T>();
        String string = "";
        
        while( !isEmpty() ){
            
            T element = get();
            
            string += element.toString() +", ";
            queueAux.put(element);
        }
        
        while( !queueAux.isEmpty() ) {
            put(queueAux.get());
        }
        
        return string;
    }
}


