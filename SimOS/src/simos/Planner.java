package simos;

import java.util.ArrayList;
import java.util.List;

import simos.OS.OSEventListener;
import util.Queue;

public class Planner {
	
	final int SUSPENDED_TIME = 3;
	
	Queue<Process> suspended;
	Queue<Process> locked;
	Queue<Process> ended;
	
	Processor processor;
	int suspendedCounter;
	
	protected List<Algorithm> algorithms;
	
	public Planner() {
		algorithms = new ArrayList<>();
		
		suspended = new Queue<>();
		locked = new Queue<>();
		ended = new Queue<>();
	}
	
	public void addAlgorithm(Algorithm alg) {
		alg.planner = this;
		algorithms.add(alg);
	}
	
	public Algorithm getNextAlgorithm(Process current) {
		return algorithms.get(0);
	}
	
	public Process next(Process current) {
		
		Process pr = null;
		Algorithm alg = getNextAlgorithm(current);
		
		if (current != null && alg != current.algorithm && !(current.time == 0 || current.change())) {
			pr = alg.next(null);
		} else {
			pr = alg.next(current);
		}

		return pr;
	}
	
	protected Algorithm getPreferredAlgorithm(Process pr) {
		return algorithms.get(0);
	}
	
	protected void addProcess(Process pr){
		
		Algorithm alg = getPreferredAlgorithm(pr);
		
		if (alg != null) {
			alg.addProcess(pr);
			pr.algorithm = alg;
		}
	}
	
	protected void plan() {
		
		for (Algorithm a : algorithms) {
			a.plan();
		}
		
		planSuspended();
		planBlocked();
	}
	
	protected void planSuspended() {
		
		if (suspended.isEmpty()) {
			return;
		}
		
		boolean empties = true;
		
		for (Algorithm a : algorithms) {
			empties = empties || a.processes.isEmpty();
		}
		
		if (suspendedCounter == SUSPENDED_TIME || empties) {
			toReady(suspended.get());
			suspendedCounter = 0;
		}
		
		suspendedCounter += 1;
	}
	
	protected void planBlocked() {
		
		if (locked.isEmpty()) {
			return;
		}
		
		Queue<Process> auxQueue = new Queue<>();
		
		while(!locked.isEmpty()) {
			
			Process pr = locked.get();
			
			if (processor.os.verifyResources(pr)) {
				toReady(pr);
			} else {
				auxQueue.put(pr);
			}
		}
		
		locked.joinBefore(auxQueue);
	}
	
	public void toReady(Process pr) {
		
		if(pr.algorithm != null) {
			pr.ready();
			
		} else {
			addProcess(pr);
		}
	}
	
	public void toSuspended(Process pr) { 
		suspended.put(pr);
	}
	
	public void toLocked(Process pr) {
		locked.put(pr);
	}
	
	public void toEnded(Process pr) {
		ended.put(pr);
	}
	
	public Queue<Process> getSuspended() {
		return suspended;
	}
	
	public Queue<Process> getLocked() {
		return locked;
	}
	
	public List<Algorithm> getAlgorithms() {
		return algorithms;
	}
	
}
