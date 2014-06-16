package multilevel;

import java.util.ArrayList;
import java.util.List;

import javax.management.Query;

import simos.Algorithm;
import simos.Planner;
import simos.Process;
import util.Queue;

public class MultiLevel extends Planner {
	
	Algorithm currentAlgorithm;
	
	List <Integer> agingTimes;
	
	boolean  preemptive;
	boolean aging;
	
	public MultiLevel() {
		this(false, false);
	}
	
	public MultiLevel(boolean preemptive, boolean aging) {
		
		agingTimes = new ArrayList<>();
		
		this.preemptive = preemptive;
		this.aging = aging;
	}
	
	@Override
	protected void plan() {
		super.plan();
		
		if (aging) {
			
			for (Algorithm a : algorithms) {
				
				Queue<Process> auxQ = new Queue<>();
				
				while(!a.getProcesses().isEmpty()) {
					
					Process pr = a.getProcesses().get();
					
					if (pr.getWaiting() >= pr.getTime()*2 && pr.getPriority() > 1) {
						
						System.out.println("ascend: " + pr);
						
						pr.setPriority(pr.getPriority() - 1);
						addProcess(pr);
						
					} else {
						auxQ.put(pr);
					}
				}
				
				a.getProcesses().joinBefore(auxQ);
			}
			
		}
	}
	
	@Override
	public Algorithm getPreferredAlgorithm(Process pr) {
		Algorithm alg = null;
		
		if (pr.getPriority() > 0 && pr.getPriority() <= algorithms.size()) {
			alg = algorithms.get(pr.getPriority()-1);
			System.out.println("hello");
		}
		
		return alg;
	}
	
	@Override
	public Algorithm getNextAlgorithm() {
		
		Algorithm next = currentAlgorithm;
		
		if (preemptive || currentAlgorithm == null || currentAlgorithm.isEmpty()) {
			
			for (Algorithm a : algorithms) {
				if (!a.isEmpty()) {
					next = a;
					break;
				}
			}
		}
		
		return next;
	}
	
	@Override
	public void addAlgorithm(Algorithm alg) {
		super.addAlgorithm(alg);
		
		agingTimes.add(0);
	}
}
