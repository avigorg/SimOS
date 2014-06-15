package simos;

import simos.OS.OSEventListener;

public class Resource {
	
	public enum State{ FREE, BUSY, BLOCKED };
	
	String name;
	State state;
	Process process;
	
	OS os;
	
	public Resource(String name) {
		this.name = name;
		this.state = State.FREE;
	}
	
	public boolean isFree() {
		return state == State.FREE;
	}
	
	public void free() {
		
		if (state == State.BLOCKED) {
			return;
		}
		
		state = State.FREE;
		
		for (OSEventListener listener : os.listeners) {
			listener.onFreeResource(this);
		}
		
		process = null;
	}
	
	public void active(Process pr) {
		
		if (state == State.BLOCKED) {
			return;
		}
		
		state = State.BUSY;
		process = pr;
		
		for (OSEventListener listener : os.listeners) {
			listener.onActiveResource(this, pr);
		}
	}
	
	public void block() {
		state = State.BLOCKED;
		
		for (OSEventListener listener : os.listeners) {
			listener.onBlockResource(this);
		}
		
		process = null;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return String.format("n: %s s: %s pr: %s", name, state, process != null ? process.name : "None");
	}
}
