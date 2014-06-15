package roundrobin;

import simos.Algorithm;
import simos.Planner;
import simos.Process;
import util.Queue;

public class RoundRobin extends Algorithm {

	public RoundRobin() {
		super("RoundRobin");
	}
	
	@Override
	public void addProcess(Process pr) {
		super.addProcess(pr);
		calculateQuantums();
	}
	
	@Override
	public Process next(Process current) {
		
		if (current == null) {
			return processes.get();
		
		} else if (current.getQuantum() == 0) {
			current.suspend();
			return processes.get();
		}
		
		return current;
	}
	
	public void calculateQuantums() {
		
		Queue<Process> auxQ = new Queue<>();
		
		float average = 0;
		
		while(!processes.isEmpty()) {
			
			Process pr = processes.get();			
			average += pr.getTime();
			
			auxQ.put(pr);
		}
		
		average /= auxQ.size();
		
		while (!auxQ.isEmpty()) {
			
			Process pr = auxQ.get();
			float distance = pr.getTime() - average;
			float quantum = average; 
			
			if (distance > 0) {
				if (distance > average*0.5) {
					quantum = average * 1.3f;
				}
			} else {
				if(distance < average*-0.5) {
					quantum = pr.getTime();
				} else {
					quantum = pr.getTime() * 0.8f;
				}
			}
			
			pr.setQuantum((int)Math.round(quantum));
			
			processes.put(pr);
		}
	}
}
