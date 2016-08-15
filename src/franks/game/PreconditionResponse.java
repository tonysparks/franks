/*
 * see license.txt 
 */
package franks.game;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tony
 *
 */
public class PreconditionResponse {

	private List<String> failureReasons;
	
	/**
	 * 
	 */
	public PreconditionResponse() {
		this(new ArrayList<>());
	}
	
	public PreconditionResponse(List<String> failureReasons) {
		this.failureReasons = failureReasons;
	}
	
	/**
	 * @return true only if the preconditions have been met
	 */
	public boolean isMet() {
		return this.failureReasons.isEmpty();
	}
	
	public PreconditionResponse addFailure(String reason) {
		this.failureReasons.add(reason);
		return this;
	}
	
	/**
	 * @return the failureReasons
	 */
	public List<String> getFailureReasons() {
		return failureReasons;
	}

}
