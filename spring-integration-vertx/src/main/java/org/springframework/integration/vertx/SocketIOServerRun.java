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
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;

import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;

public class SocketIOServerRun extends AbstractServerConnectionFactory
		implements SmartLifecycle {

	private HttpServer server;
	private volatile boolean running;

	public SocketIOServerRun(int port) {
		super(port);
	}

	@Override
	public void run() {
		if (this.running) {
			return;
		}
		Vertx vertx = VertxFactory.newVertx();
		this.server = vertx.createHttpServer();

		SocketIOServer socketIO = new DefaultSocketIOServer(vertx, server);

		socketIO.sockets().onConnection(new Handler<SocketIOSocket>() {
			public void handle(final SocketIOSocket socket) {

				final String correlationId = UUID.randomUUID().toString();
				final TcpListener listener = SocketIOServerRun.this
						.getListener();
				SocketIOConnection connection = new SocketIOConnection(
						correlationId, socket, listener);
				SocketIOServerRun.this.getSender().addNewConnection(connection);

				socket.on("echo", new Handler<JsonObject>() {
					public void handle(JsonObject msg) {
						listener.onMessage(MessageBuilder
								.withPayload(msg)
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
