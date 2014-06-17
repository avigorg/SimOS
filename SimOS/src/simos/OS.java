package simos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class OS {

	List<Processor> processors;
	HashMap<String, Resource> resources;
	
	List<OSEventListener> listeners;
	
	OSDecider decider;
	
	public OS(OSDecider decider) {
		
		processors = new ArrayList<>();
		resources = new HashMap<>();
		listeners = new LinkedList<>(); 
		
		if (decider != null) { 
			this.decider = decider;
		} else {
			this.decider = new DefaultDecider();
		}
	}
	
	public OS() {
		this(null);
	}
	
	public void execute() {
		
		for (Processor p : processors) {
			p.planner.plan();
			p.plan();
		}
		
		for (Processor p : processors) {
			p.execute();
		} 
	}
	
	public boolean tryRun(Process pr) {
		
		System.out.println("try " + pr.name);
		
		boolean canRun = verifyResources(pr);
		
		if(canRun) {			
			run(pr);
		}
		
		return canRun;
	}
	
	private void run(Process pr) {
		
		Process aux = pr.algorithm.planner.processor.current;
		pr.algorithm.planner.processor.current = pr;
		
		for (Processor p : processors) {
			
			if (p.current == pr || p.current == null || p.current.time == 0 || p.current.change()) {
				continue;
			}
			
			if (haveCommonRes(pr, p.current)) {				
				p.current.lock();
				p.current = null;
				
				System.out.println("plan extra for " + p.name);
				p.plan();
			}
		}
		
		pr.algorithm.planner.processor.current = aux;
		activeResources(pr);
	}
	
	public boolean verifyResources(Process pr) {
		
		boolean available = true;
		
		for (String res : pr.resources) {
			if (!resources.containsKey(res) || resources.get(res).isLocked()) {
				available = false;
				break;
			}
		}
		
		if (!available) {
			return false;
		}
		
		for (Processor p : processors) {
			
			if (p.current == pr || p.current == null || p.current.time == 0 || p.current.change()) {
				continue;
			}
			
			if (haveCommonRes(pr, p.current)) {				
				if (!decider.hasPriority(pr, p.current)) {	
					System.out.println("no lo logro: " + pr.name);
					available = false;
					break;
				} else {
					System.out.println("si lo logro: " + pr.name);
				}
			} else {
				System.out.println("nope commons");
			}
		}
		
		return available;
	}
	
	protected void activeResources(Process pr) {
		
		for (String res : pr.resources) {
			Resource r = resources.get(res);
			
			if (r != null) {
				r.active(pr);
			}
		}
	}
	
	protected void freeResources(Process pr) {
		
		for (String res : pr.resources) {
			Resource r = resources.get(res);
			
			if (r != null && r.process == pr) {
				r.free();
			}
		}
		
	}
	
	private boolean haveCommonRes(Process pr, Process other) {
		
		boolean common = false;
		
		for (String res : pr.resources) {
			
			System.out.println(pr.name + " "+res);
			System.out.println(other.name);
			
			if (other.resources.contains(res)) {
				
				System.out.println("common " + res + " " + pr.name + " " + other.name);
				
				common = true;
				break;
			}
		}
		
		return common;
	}
	
	public void addResource(String name) {
		
		Resource res = new Resource(name);
		res.os = this;
		
		resources.put(name, res);
		
		for (OSEventListener listener : listeners) {
			listener.onAddResource(res);
		}
	}
	
	public void addProcessor(Processor p) {
		processors.add(p);
	}
	
	public void addListener(OSEventListener listener) {
		listeners.add(listener);
	}
	
	public interface OSDecider {
		boolean hasPriority(Process pr, Process other);
	}
	
	class DefaultDecider implements OSDecider {

		@Override
		public boolean hasPriority(Process pr, Process other) {
			return pr.priority < other.priority;
		}
		
	}
	
	public interface OSEventListener {
		
		void onAddProcess(Process pr, Processor p, Algorithm alg);
		void onSuspendProcess(Process pr, Processor p);
		void onBlockProcess(Process pr, Processor p);
		void onEndProcess(Process pr);
		
		void onRunProcess(Process pr, Processor p);
		
		void onAddResource(Resource r);
		void onActiveResource(Resource r, Process pr);
		void onFreeResource(Resource r);
		void onBlockResource(Resource r);
	}
}

