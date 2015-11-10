package org.freshgrade.molr;

import static org.hamcrest.core.Is.is;

import org.freshgrade.molr.OplogIdentifier;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;


public class OplogIdentifierTest {
	public static final Integer ONE_DAY_IN_SECONDS = 86400;
	
	@Test
	public void simpleGetterTest() {
		OplogIdentifier id = new OplogIdentifier(1, 1);
		
		assertThat(id.getInc(), is(1));
		assertThat(id.getTime(), is(1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void notNullTimeTest() {
		new OplogIdentifier(1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notNullIncTest() {
		new OplogIdentifier(null, 1);
	}

	@Test()
	public void toStringTest() {
		OplogIdentifier id = new OplogIdentifier(ONE_DAY_IN_SECONDS, 1);
		
		String string = id.toString();
		assertThat(string, is("1970-01-02T00:00 (1)"));
	}

}
