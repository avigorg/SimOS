package test;

import static org.junit.Assert.*;

import org.junit.Test;

import util.Queue;

public class QueueTest {

	@Test
	public void test_queue_return_the_correct_objects() {
		
		Queue<Object> q = new Queue<>();
		
		Object first = new Object();
		Object second = new Object();
		
		q.put(first);
		q.put(second);
		
		assertEquals(q.get(), first);
		assertEquals(q.get(), second);
		assertEquals(q.get(), null);

		q.put(first);
		
		assertEquals(q.get(), first);
		assertEquals(q.get(), null);
	}

}
