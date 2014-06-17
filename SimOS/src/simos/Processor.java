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

		do {
			
			if (current != null) {
				if (current.time == 0) {
					current.end();
					current = null;
				
				} else if (current.change()) {
					
					current.suspend();
					current = null;
				}
			}
			
			Process next = planner.next(current);
			
			if (next != null)
				System.out.println("next: " + next.name);
			
			if (next == null || next == current) {				
				keep = false;
			
			} else if (os.tryRun(next)) {
				
				if (current != next && current != null) {
					current.suspend();
				}
				
				current = next;
				keep = false;
				
			} else {
				next.suspend();
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
			current.run();
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
