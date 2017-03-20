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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class RemoteHandler extends AbstractHandler {

	private PrintWriter out;
	private BufferedReader in;

	public RemoteHandler(BufferedReader in, PrintWriter out) {
		this.in = in;
		this.out = out;
	}

	@Override
	public String processRequest(String command) {
		// now assumes all commands are handled by the server
		// System.out.println("processing command:" + command);
		out.println(command);
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(in.readLine());
			while (in.ready()) {
				sb.append("\n" + in.readLine());
			}
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
