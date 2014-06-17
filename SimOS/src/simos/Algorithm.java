package simos;

import simos.OS.OSEventListener;
import util.Queue;

public class Algorithm {
	
	String name;
	
	protected Planner planner;
	protected Queue<Process> processes;
	
	public Algorithm() {
		this("FIFO");
	}
	
	public Algorithm(String name) {
		this.name = name;
		processes = new Queue<>();
	}
	
	protected void plan() {
		Queue<Process> auxQ = new Queue<>();
		
		while (!processes.isEmpty()) {
			Process pr = processes.get();
			pr.waiting += 1;
			
			auxQ.put(pr);
		} 
		
		processes.joinBefore(auxQ);
	}
	
	protected void onRun(Process pr) {
		return;
	}
	
	protected boolean change(Process pr) {
		return false;
	}
	
	protected void addToQueue(Process pr) {
		processes.put(pr);
	}
	
	public void addProcess(Process pr) {
		addToQueue(pr);
		pr.waiting = 0;
		
		for (OSEventListener listener : planner.processor.os.listeners) {
			listener.onAddProcess(pr, planner.processor, this);
		}
	}
	
	public Process next(Process current) {
		
		if(current == null) {
			return processes.get();
		} 
		
		return current;
	}
	
	public boolean isEmpty() {
		return processes.isEmpty();
	}
	
	public Queue<Process> getProcesses() {
		return processes;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
