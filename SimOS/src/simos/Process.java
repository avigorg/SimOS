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
	
	int waiting;
	
	State state;
	Algorithm algorithm;
	
	List <String> resources;
	
	OS os;
	
	public Process(String name, int time, String[] resources, int  priority, int quantum) {
		
		this.name = name;
		this.time = time;
		this.state = State.READY;
		this.priority = priority;
		this.quantum = quantum;
		this.waiting = 0; 
		
		this.resources = new ArrayList<String>();
		
		for (String res : resources) {
			this.resources.add(res);
		}
	}
	
	public Process(String name, int time, String[] resources, int  priority) {
		this(name, time, resources, priority, -1);
	}
	
	public Process(String name, int time, String[] resources) {
		this(name, time, resources, -1, -1);
	}
	
	protected boolean change() {
		return algorithm.change(this);
	}
	
	protected void onRun() {
		algorithm.onRun(this);
	}
	
	public void ready() {
		algorithm.addToQueue(this);
		state = State.READY;
		
		for ( OSEventListener listener : os.listeners ) {
			listener.onAddProcess(this, algorithm.planner.processor, algorithm);
		}
	}
	
	public void suspend() {
		algorithm.planner.toSuspended(this);
		state = State.SUSPENDED;

		if (algorithm.planner.processor == null) {
			System.out.println("ta raro");
		}
		
		for ( OSEventListener listener : os.listeners ) {
			listener.onSuspendProcess(this, algorithm.planner.processor);
		}
	}
	
	public void block() {
		algorithm.planner.toBlocked(this);
		state = State.BLOCKED;
		
		for ( OSEventListener listener : os.listeners ) {
			listener.onBlockProcess(this, algorithm.planner.processor);
		}
	}
	
	public void end() {
		algorithm.planner.toEnded(this);
		os.freeResources(this);
		state = State.ENDED;

		for ( OSEventListener listener : os.listeners ) {
			listener.onEndProcess(this);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public int getTime() {
		return time;
	}
	
	public void resetWaiting() {
		waiting = 0;
	}
	
	public int getWaiting() {
		return waiting;
	}
	
	public void setQuantum(int quantum) {
		this.quantum = quantum;
	}
	
	public int getQuantum() {
		return quantum;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int getPriority() {
		return priority;
	}
	
	@Override
	public String toString() {
		return  String.format("name: %s time: %d quantum: %d p: %d", name, time, quantum, priority);
	}
}
