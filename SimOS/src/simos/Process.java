package simos;

import java.util.ArrayList;
import java.util.List;

import simos.OS.OSEventListener;

public class Process {

	public enum State {READY, SUSPENDED, BLOCKED, ENDED};
	
	String name;
	
	int time;
	int quantum;
	int priority;
	
	State state;
	Algorithm algorithm;
	
	List <String> resources;
	
	OS os;
	
	public Process(String name, int time, String[] resources) {
		
		this.name = name;
		this.time = time;
		this.state = State.READY;
		
		this.resources = new ArrayList<String>();
		
		for (String res : resources) {
			this.resources.add(res);
		}
		
		this.priority = -1;
		this.quantum = -1;
	}
	
	public void ready() {
		algorithm.addProcess(this);
		state = State.READY;
		
		for ( OSEventListener listener : os.listeners ) {
			listener.onAddProcess(name, algorithm.planner.processor.name, algorithm.name);
		}
	}
	
	public void suspend() {
		algorithm.planner.toSuspended(this);
		state = State.SUSPENDED;

		if (algorithm.planner.processor == null) {
			System.out.println("ta raro");
		}
		
		for ( OSEventListener listener : os.listeners ) {
			listener.onSuspendProcess(name, algorithm.planner.processor.name);
		}
	}
	
	public void block() {
		algorithm.planner.toBlocked(this);
		state = State.BLOCKED;
		
		for ( OSEventListener listener : os.listeners ) {
			listener.onBlockProcess(name, algorithm.planner.processor.name);
		}
	}
	
	public void end() {
		algorithm.planner.toEnded(this);
		state = State.ENDED;

		for ( OSEventListener listener : os.listeners ) {
			listener.onEndProcess(name);
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
}
