package il.technion.ewolf.kbr.openkad.net.filter;

import il.technion.ewolf.kbr.Node;
import il.technion.ewolf.kbr.openkad.msg.KadMessage;
import lombok.ToString;

/**
 * Rejects all messages from src other than the given src
 * 
 * @author eyal.kibbar@gmail.com
 *
 */
@ToString
public class SrcExcluderMessageFilter implements MessageFilter {

	private final Node src;
	
	public SrcExcluderMessageFilter(Node src) {
		this.src = src;
	}
	
	
	
	@Override
	public boolean shouldHandle(KadMessage m) {
		return !src.equals(m.getSrc());
	}
	
}
