/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.vertx;

import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;
import org.springframework.integration.Message;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpListener;
import org.springframework.integration.ip.tcp.connection.TcpMessageMapper;
import org.springframework.integration.ip.tcp.connection.TcpSender;

import com.nhncorp.mods.socket.io.SocketIOSocket;

/**
 * @author Gary Russell
 * 
 */
public class SocketIOConnection implements TcpConnection {

	private final String id;

	private final SocketIOSocket socket;

	private final TcpListener listener;

	private volatile String hostName = "unknown";

	private volatile String hostAddress = "unknown";

	public SocketIOConnection(String id, SocketIOSocket socket,
			TcpListener listener) {
		this.id = id;
		this.socket = socket;
		this.listener = listener;
	}

	@Override
	public void run() {
	}

	@Override
	public void close() {
	}

	@Override
	public String getConnectionId() {
		return this.id;
	}

	@Override
	public Deserializer<?> getDeserializer() {
		return null;
	}

	@Override
	public String getHostAddress() {
		return hostName;
	}

	@Override
	public String getHostName() {
		return hostAddress;
	}

	@Override
	public TcpListener getListener() {
		return this.listener;
	}

	@Override
	public Object getPayload() throws Exception {
		return null;
	}

	@Override
	public int getPort() {
		return 0;
	}

	@Override
	public Serializer<?> getSerializer() {
		return null;
	}

	@Override
	public long incrementAndGetConnectionSequence() {
		return 0;
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public boolean isServer() {
		return false;
	}

	@Override
	public boolean isSingleUse() {
		return false;
	}

	@Override
	public void registerListener(TcpListener arg0) {
	}

	@Override
	public void registerSender(TcpSender arg0) {
	}

	@Override
	public void send(Message<?> message) throws Exception {
		this.socket.emit((String) message.getPayload());
	}

	@Override
	public void setDeserializer(Deserializer<?> arg0) {
	}

	@Override
	public void setMapper(TcpMessageMapper arg0) {
	}

	@Override
	public void setSerializer(Serializer<?> arg0) {
	}

	@Override
	public void setSingleUse(boolean arg0) {
	}

}
