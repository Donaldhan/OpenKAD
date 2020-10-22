package il.technion.ewolf.kbr.openkad.net.filter;

import il.technion.ewolf.kbr.openkad.msg.KadMessage;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Reject all messages not in the given class
 * @author eyal.kibbar@gmail.com
 *
 */
@Slf4j
@ToString
public class TypeMessageFilter implements MessageFilter {

	/**
	 *
	 */
	private final Class<? extends KadMessage> clazz;
	
	public TypeMessageFilter(Class<? extends KadMessage> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public boolean shouldHandle(KadMessage m) {
		log.debug("TypeMessageFilter filter class:{}, msg class:{}",clazz.getName(),m.getClass().getName());
		return m.getClass().equals(clazz);
	}

	
}
