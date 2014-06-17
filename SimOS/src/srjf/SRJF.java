package srjf;

import simos.Algorithm;
import simos.Process;
import util.Queue;

public class SRJF extends Algorithm {

	boolean preemptive;
	
	public SRJF() {
		this(false);
	}
	
	public SRJF(boolean preemptive) {
		super("SRJF");
		this.preemptive = preemptive;
	}
	
	@Override
	public void addToQueue(Process newP) {
		
		Queue<Process> auxQ = new Queue<>();
		boolean added = false;
		
		while(!processes.isEmpty() && !added) {
			Process oldP = processes.get();
			
			if (newP.getTime() < oldP.getTime()) {
				auxQ.put(newP);
				added = true;
			}
			
			auxQ.put(oldP);
		}
		
		if (!added) {
			auxQ.put(newP);
		}
		
		processes.joinBefore(auxQ);
	}
	
	@Override
	public Process next(Process current) {
		Process next = current;
		
		if (current == null) {
			next = processes.get();
		
		} else if (preemptive) {
			
			Process first = processes.get();
			
			if (first != null && first.getTime() < current.getTime()) {
				next = first;
			
			} else {
				addToQueue(first);
			} 
		}
		
		return next;
	}
	
	public void setPreemptive(boolean preemptive) {
		this.preemptive = preemptive;
	}
}
