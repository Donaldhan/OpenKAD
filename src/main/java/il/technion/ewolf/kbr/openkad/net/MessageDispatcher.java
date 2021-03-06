package il.technion.ewolf.kbr.openkad.net;

import il.technion.ewolf.kbr.Node;
import il.technion.ewolf.kbr.concurrent.CompletionHandler;
import il.technion.ewolf.kbr.concurrent.FutureCallback;
import il.technion.ewolf.kbr.openkad.msg.KadMessage;
import il.technion.ewolf.kbr.openkad.msg.KadRequest;
import il.technion.ewolf.kbr.openkad.net.filter.MessageFilter;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;

/**
 * Handle all the messages different states.
 * A request state: 
 * init -> sent -> response received -> callback invoked
 * 
 * A message state:
 * init -> expecting -> message received -> callback invoked -> back to expecting or end
 * 
 * @author eyal.kibbar@gmail.com
 *
 * @param <A>
 */
@Slf4j
public class MessageDispatcher<A> {

	// state
	private A attachment;
	/**
	 * 消息分发回调
	 */
	private CompletionHandler<KadMessage, A> callback;
	/**
	 *
	 */
	private boolean isConsumbale = true;
	private long timeout;
	private final Set<MessageFilter> filters = new HashSet<MessageFilter>();
	private TimerTask timeoutTimerTask = null;
	private final AtomicBoolean isDone;
	// dependencies
	private final BlockingQueue<MessageDispatcher<?>> outstandingRequests;
	/**
	 * must be sync'ed set
	 */
	private final Set<MessageDispatcher<?>> expecters;
	/**
	 *must be sync'ed set
	 */
	private final Set<MessageDispatcher<?>> nonConsumableexpecters;

	/**
	 *
	 */
	private final Timer timer;
	/**
	 *
	 */
	private final Communicator communicator;

	
	@Inject
	MessageDispatcher(
			@Named("openkad.net.req_queue") BlockingQueue<MessageDispatcher<?>> outstandingRequests,
			@Named("openkad.net.expecters") Set<MessageDispatcher<?>> expecters,
			@Named("openkad.net.expecters.nonConsumable") Set<MessageDispatcher<?>> nonConsumableexpecters,
			@Named("openkad.timer") Timer timer,
			@Named("openkad.net.timeout") long timeout,
			Communicator communicator) {
		
		this.outstandingRequests = outstandingRequests;
		this.expecters = expecters;
		this.nonConsumableexpecters = nonConsumableexpecters;
		this.timer = timer;
		this.timeout = timeout;
		this.communicator = communicator;
		this.isDone = new AtomicBoolean(false);
	}

    /**
     *
     */
	private void expect() {
		if (isConsumbale)
			expecters.add(this);
		else 
			nonConsumableexpecters.add(this);
	}
	
	private void cancelExpect() {
		if (isConsumbale)
			expecters.remove(this);
		else 
			nonConsumableexpecters.remove(this);
	}

	/**
	 * 取消操作
	 * @param exc
	 */
	public void cancel(Throwable exc) {
		if (!isDone.compareAndSet(false, true))
			return;
		
		if (timeoutTimerTask != null)
			timeoutTimerTask.cancel();
		
		outstandingRequests.remove(this);
		cancelExpect();
		
		if (callback != null)
			callback.failed(exc, attachment);
	}

	/**
	 * returns true if should be handled
	 * @param m
	 * @return
	 */
	boolean shouldHandleMessage(KadMessage m) {
		for (MessageFilter filter : filters) {
			if (!filter.shouldHandle(m)) {
				log.debug("MessageDispatcher shouldHandleMessage false filters:{}, msg:{}",filters , m);
				return false;
			}

		}
		log.debug("MessageDispatcher shouldHandleMessage true filters:{}, msg:{}",filters , m);
		return true;
	}

	/**
	 * 处理消息
	 * @param msg
	 */
	void handle(KadMessage msg) {
		assert (shouldHandleMessage(msg));
		
		if (isDone.get()) {
			return;
		}
		
		if (timeoutTimerTask != null) {
			timeoutTimerTask.cancel();
		}
		
		outstandingRequests.remove(this);
		if (isConsumbale) {
			expecters.remove(this);
			if (!isDone.compareAndSet(false, true)) {
				return;
			}
		}
		log.debug("handle KadMessage:{}",msg);
		//通知回调
		if (callback != null) {
			callback.completed(msg, attachment);
		}
	}
	
	public MessageDispatcher<A> addFilter(MessageFilter filter) {
		filters.add(filter);
		return this;
	}
	
	public MessageDispatcher<A> setCallback(A attachment, CompletionHandler<KadMessage, A> callback) {
		this.callback = callback;
		this.attachment = attachment;
		return this;
	}
	
	public MessageDispatcher<A> setTimeout(long t, TimeUnit unit) {
		timeout = unit.toMillis(t);
		return this;
	}
	
	public MessageDispatcher<A> setConsumable(boolean consume) {
		isConsumbale = consume;
		return this;
	}

	/**
	 * @return
	 */
	public MessageDispatcher<A> register() {
		//注册消息分发器到KadServer，
		//通过	@Named("openkad.net.expecters") Set<MessageDispatcher<?>> expecters 共享
		expecters.add(this);
		setupTimeout();
		return this;
	}
	
	
	public Future<KadMessage> futureRegister() {
		
		FutureCallback<KadMessage, A> f = new FutureCallback<KadMessage, A>() {
			@Override
			public synchronized boolean cancel(boolean mayInterruptIfRunning) {
				MessageDispatcher.this.cancel(new CancellationException());
				return super.cancel(mayInterruptIfRunning);
			};
		};
		
		setCallback(null, f);
		expect();
		setupTimeout();
		
		return f;
	}

	/**
	 *
	 */
	private void setupTimeout() {
		if (!isConsumbale) {
			return;
		}
		
		timeoutTimerTask = new TimerTask() {
			
			@Override
			public void run() {
				log.info("MessageDispatcher timeout...");
				MessageDispatcher.this.cancel(new TimeoutException());
			}
		};
		//debug 暂时关闭
		timer.schedule(timeoutTimerTask, timeout);
	}

	/**
	 * @param to
	 * @param req
	 * @return
	 */
	public boolean trySend(Node to, KadRequest req) {
		setConsumable(true);
		try {
			if (!outstandingRequests.offer(this)) {
				return false;
			} else
			{
				//outstandingRequests.put(this);
				expect();
				communicator.send(to, req);
				setupTimeout();
				return true;
			}

		} catch (Exception e) {
            log.error("log.error trySend to:{}, req:{}",to, req);
			cancel(e);
			// if something bad happened - feel free to try again. 
			return true;
		}
	}

    /**
     * @param to
     * @param req
     */
	public void send(Node to, KadRequest req) {
		setConsumable(true);
		try {
			/*
			if (!outstandingRequests.offer(this, timeout, TimeUnit.MILLISECONDS))
				throw new RejectedExecutionException();
			*/
			outstandingRequests.put(this);
			expect();
			communicator.send(to, req);
			log.debug("communicator send to Node:{},req:{}",to,req);

			setupTimeout();
			
		} catch (Exception e) {
		    log.error("log.error send to:{}, req:{}",to, req);
			cancel(e);
		}
	}
	
	public Future<KadMessage> futureSend(Node to, KadRequest req) {
		
		FutureCallback<KadMessage, A> f = new FutureCallback<KadMessage, A>();
		setCallback(null, f);
		
		send(to, req);
		
		return f;
	}
}
