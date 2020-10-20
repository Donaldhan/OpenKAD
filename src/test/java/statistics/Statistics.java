package statistics;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {
	
	public AtomicInteger nrHandledMsgs; 

	@Inject
	public Statistics(
			@Named("openkad.testing.nrIncomingMessages") AtomicInteger nrHandledMsgs){
		this.nrHandledMsgs = nrHandledMsgs;
	}
	
}
