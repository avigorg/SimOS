package srjf;

import simos.OS.OSDecider;
import simos.Process;

public class SRJFDecider implements OSDecider {

	@Override
	public boolean hasPriority(Process pr, Process other) {
		return pr.getTime() < other.getTime();
	}

}
