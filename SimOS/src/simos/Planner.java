package simos;

import java.util.ArrayList;
import java.util.List;

import util.Queue;

public class Planner {
	
	final int SUSPENDED_TIME = 3;
	
	List<Algorithm> algorithms;
	
	Queue<Process> suspended;
	Queue<Process> blocked;
	Queue<Process> ended;
	
	Processor processor;
	
	int suspendedCounter;
	
	public Planner() {
		algorithms = new ArrayList<>();
		
		suspended = new Queue<>();
		blocked = new Queue<>();
		ended = new Queue<>();
	}
	
	public void addAlgorithm(Algorithm alg) {
		alg.planner = this;
		algorithms.add(alg);
	}
	
	public Algorithm getPreferredAlgorithm() {
		return algorithms.get(0);
	}
	
	public Process next(Process current) {
		
		Process pr = null;
		
		planSuspended();
		planBlocked();
		
		if (current != null){
			pr = current.algorithm.next(current);
			
		} else {
			Algorithm alg = getPreferredAlgorithm();
			
			if (alg != null) {
				pr = alg.next(null);
			}
		}
		
		return pr;
	}
	
	private void planSuspended() {
		
		if (suspended.isEmpty()) {
			return;
		}
		
		if (suspendedCounter == SUSPENDED_TIME) {
			toReady(suspended.get());
			suspendedCounter = 0;
		}
		
		suspendedCounter += 1;
	}
	
	private void planBlocked() {
		
		if (blocked.isEmpty()) {
			return;
		}
		
		Queue<Process> auxQueue = new Queue<>();
		
		while(!blocked.isEmpty()) {
			
			Process pr = blocked.get();
			
			if (processor.os.verifyResources(pr)) {
				toReady(pr);
			} else {
				auxQueue.put(pr);
			}
		}
		
		blocked.joinBefore(auxQueue);
	}
	
	public void toReady(Process pr) {
		
		if(pr.algorithm != null) {
			pr.ready();
			
		} else {
			
			Algorithm alg = getPreferredAlgorithm();
			
			if (alg != null) {
				pr.algorithm = alg;
				pr.ready();
			}
		}
	}
	
	public void toSuspended(Process pr) { 
		suspended.put(pr);
	}
	
	public void toBlocked(Process pr) {
		blocked.put(pr);
	}
	
	public void toEnded(Process pr) {
		ended.put(pr);
	}
	
}
