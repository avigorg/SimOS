package simos;

import simos.OS.OSEventListener;

public class Processor {
	
	String name;
	Process current;
	Planner planner;
	OS os;
	
	public Processor(String name, Planner planner, OS os) {
		this.name = name;
		this.planner = planner;
		this.os = os;
		
		this.planner.processor = this;
	}
	
	public void addProcess(Process pr) {
		pr.os = os;
		planner.toReady(pr);
	}
	
	public void plan() {
		
		boolean keep = true;
		
		planner.planSuspended();
		planner.planBlocked();
		
		do {
			
			if (current != null && current.time == 0) {
				current.end();
				current = null;
			}
			
			current = planner.next(current);
			
			if (current == null) {
				keep = false;
			
			} else if (!os.tryRun(current)) {
				current.suspend();
				current = null;
			} else {
				keep = false;
			}
			
		} while(keep);
	}
	
	public void execute() {

		for(OSEventListener listener : os.listeners) {
			if (current != null) {
				listener.onRunProcess(current, this);
			} else {
				listener.onRunProcess(null, this);
			}
		}
		
		if (current != null) {
			
			current.time -= 1;
			
			if (current.quantum != -1) {
				current.quantum -= 1;
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return String.format("n: %s pr: %s", name, current);
	}
}
