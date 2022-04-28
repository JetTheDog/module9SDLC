package application;

import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.Test;

public class poemTest {
	
	@Test
	public void wordTest() throws IOException  {
		Boolean ret = Main.evaluatePoem("ravenPoem.html");
		assertTrue(ret);
		assertTrue(Main.wordFrequency.size() > 0);
		assertTrue(Main.poemWords.size() > 0);
	}

}
