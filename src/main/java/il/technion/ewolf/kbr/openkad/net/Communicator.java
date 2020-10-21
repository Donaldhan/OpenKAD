package il.technion.ewolf.kbr.openkad.net;


import il.technion.ewolf.kbr.Node;
import il.technion.ewolf.kbr.openkad.msg.KadMessage;

import java.io.IOException;

public interface Communicator extends Runnable {

	/**
	 *
	 */
	public void bind();

	/**
	 * @param to
	 * @param msg
	 * @throws IOException
	 */
	public void send(Node to, KadMessage msg) throws IOException;

	/**
	 * @param serverThread
	 */
	public void shutdown(Thread serverThread);
	
}