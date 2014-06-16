package srjf;

import simos.Algorithm;
import simos.Process;
import util.Queue;

public class SRJFAlgorithm extends Algorithm {

	boolean preemptive;
	
	public SRJFAlgorithm() {
		this(false);
	}
	
	public SRJFAlgorithm(boolean preemptive) {
		this.preemptive = preemptive;
	}
	
	@Override
	public void addProcess(Process newP) {
		
		Queue<Process> auxQ = new Queue<>();
		
		Process oldP = null;
		boolean keep = true;
		
		while (!processes.isEmpty() && keep) {
			
			oldP = processes.get();
			
			if (newP.getTime() < oldP.getTime()) {
				keep = false;
			} else {
				auxQ.put(oldP);
			}
		}
		
		auxQ.put(newP);
		
		if (oldP != null) {
			auxQ.put(oldP);
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
				addProcess(first);
			} 
		}
		
		return next;
	}
	
	public void setPreemptive(boolean preemptive) {
		this.preemptive = preemptive;
	}
}
