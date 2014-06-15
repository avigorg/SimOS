package simos;

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
	
	public void addProcess(Process pr) {
		processes.put(pr);
	}
	
	public Process next(Process current) {
		
		if(current == null || current.time == 0) {
			return processes.get();
		} 
		
		return current;
	}
}
