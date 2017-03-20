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
package vectorization.parsing;

import vectorization.client.Handler;
import com.vectorization.util.IO;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;

public class Source implements ClientCommand {

	private String path;

	public Source(String path) {
		this.path = path;
	}

	public String execute(Handler h) {
		try{
			StringBuilder sb = new StringBuilder();
			BufferedReader br = IO.createBufferedReader(new FileInputStream(path));
			String line;
			while((line = br.readLine()) != null){
				sb.append("> " + line + "\n");
				sb.append(h.processRequest(line)+ "\n");
			}
			return sb.toString();
		}catch(IOException e){
			return e.getMessage();
		}
	}

}
