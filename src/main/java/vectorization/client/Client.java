/*  
 *  Copyright (C) 2014 Robert Moss
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package vectorization.client;

import com.vectorization.core.SSException;
import com.vectorization.util.IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	private Handler handler;

	public Client(String database, String address, int port) {
		try {
			handler = createHandler(address, port);
			printWelcome();
			useDatabase(database);
//			processInput();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processInput() throws IOException {
		BufferedReader stdIn = IO.createBufferedReader(System.in);
		processInput(stdIn);
	}

	private void processInput(BufferedReader stdIn) throws IOException {
		String userInput;
		while ((userInput = stdIn.readLine()) != null) {
			sendRequest(userInput);
			prompt();
		}
	}

	private void useDatabase(String database) {
		System.out.println("loading...");
		sendRequest("use " + database);
//		prompt();
	}

	private Handler createHandler(BufferedReader in, PrintWriter out) {
		return new LocalHandler(new RemoteHandler(in, out));
	}

	private Handler createHandler(String address, int port)
			throws UnknownHostException, IOException {
		Socket socket = IO.createSocket(address, port);
		PrintWriter out = IO.createPrintWriter(socket.getOutputStream());
		BufferedReader in = IO.createBufferedReader(socket.getInputStream());
		return createHandler(in, out);
	}

	private void printWelcome() {
		System.out.println("Welcome to similarity-database client");
		System.out.println("copyright Robert Moss, all rights reserved");
		System.out.println();
	}

	private void prompt() {
		System.out.print("> ");
	}

	public void sendRequest(String request) {
		try {
			System.out.println(handler.processRequest(request));
		} catch (SSException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		new Client("Test", "localhost", 4567);
	}

}
