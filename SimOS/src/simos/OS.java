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
			p.plan();
		}
		
		for (Processor p : processors) {
			p.execute();
		} 
	}
	
	public boolean tryRun(Process pr) {
		
		boolean canRun = canRun(pr);
		
		if(canRun) {			
			run(pr);
		}
		
		return canRun;
	}
	
	private boolean canRun(Process pr) {
		
		boolean canRun = true;
		
		for (Processor p : processors) {
			
			if (p.current == pr || p.current == null) {
				continue;
			}
			
			if (haveCommonRes(pr, p.current)) {
				
				if (!decider.hasPriority(pr, p.current)) {	
					canRun = false;
					break;
				}
			}
		}
		
		for (String res : pr.resources) {
			if (!resources.containsKey(res)){
				canRun = false;
				break;
			}
		}
		
		return canRun;
	}
	
	private void run(Process pr) {
		
		for (Processor p : processors) {
			
			if (p.current == pr || p.current == null) {
				continue;
			}
			
			if (haveCommonRes(pr, p.current)) {
				
				freeResources(p.current);
				
				p.current.block();
				p.current = null;
				
				p.plan();
			}
		}
		
		activeResources(pr);
		
	}
	
	public boolean verifyResources(Process pr) {
		
		boolean result = true;
		
		for (String res : pr.resources) {
			if (!resources.containsKey(res)) {
				result = false;
				break;
			} else if (!resources.get(res).isFree()) {
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	private void activeResources(Process pr) {
		
		for (String res : pr.resources) {
			Resource r = resources.get(res);
			
			if (r != null) {
				r.active(pr);
			}
		}
	}
	
	private void freeResources(Process pr) {
		
		for (String res : pr.resources) {
			Resource r = resources.get(res);
			
			if (r != null) {
				r.free();
			}
		}
		
	}
	
	private boolean haveCommonRes(Process pr, Process other) {
		
		boolean common = false;
		
		for (String res : pr.resources) {
			if (other.resources.contains(res)) {
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
			return pr.priority > other.priority;
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

