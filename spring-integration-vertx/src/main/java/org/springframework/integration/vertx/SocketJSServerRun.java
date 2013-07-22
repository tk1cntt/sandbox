package org.springframework.integration.vertx;

import java.util.UUID;

import org.springframework.context.SmartLifecycle;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpListener;
import org.springframework.integration.support.MessageBuilder;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.core.sockjs.SockJSSocket;

public class SocketJSServerRun extends AbstractServerConnectionFactory implements
		SmartLifecycle {

	private HttpServer server;
	private volatile boolean running;

	public SocketJSServerRun(int port) {
		super(port);
	}

	@Override
	public void run() {
		if (this.running) {
			return;
		}
		Vertx vertx = VertxFactory.newVertx();
		this.server = vertx.createHttpServer();

		SockJSServer sockJSServer = vertx.createSockJSServer(server);
		final String correlationId = UUID.randomUUID().toString();
		final TcpListener listener = SocketJSServerRun.this.getListener();

		JsonObject channelEcho = new JsonObject().putString("prefix", "/echo");
		sockJSServer.installApp(channelEcho, new Handler<SockJSSocket>() {
			public void handle(SockJSSocket sock) {
				SocketJSConnection connection = new SocketJSConnection(
						correlationId, sock, listener);
				SocketJSServerRun.this.getSender().addNewConnection(connection);
				sock.dataHandler(new Handler<Buffer>() {
					public void handle(Buffer buffer) {
						listener.onMessage(MessageBuilder
								.withPayload(buffer)
								.setCorrelationId(correlationId)
								.setHeader(IpHeaders.CONNECTION_ID,
										correlationId).build());
					}
				});
			}
		});

		JsonObject channelChat = new JsonObject().putString("prefix", "/chat");
		sockJSServer.installApp(channelChat, new Handler<SockJSSocket>() {
			public void handle(SockJSSocket sock) {
				SocketJSConnection connection = new SocketJSConnection(
						correlationId, sock, listener);
				SocketJSServerRun.this.getSender().addNewConnection(connection);
				sock.dataHandler(new Handler<Buffer>() {
					public void handle(Buffer buffer) {
						listener.onMessage(MessageBuilder
								.withPayload(buffer)
								.setCorrelationId(correlationId)
								.setHeader(IpHeaders.CONNECTION_ID,
										correlationId).build());
					}
				});
			}
		});

		server.listen(getPort());
		this.running = true;
	}

	public void stop() {
		this.running = false;
		this.server.close();
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public int getPhase() {
		return 0;
	}

	@Override
	public boolean isAutoStartup() {
		return false;
	}

	@Override
	public void stop(Runnable callback) {
		this.stop();
		callback.run();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
