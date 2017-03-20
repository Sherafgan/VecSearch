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

import com.vectorization.parsing.Lexer;
import com.vectorization.parsing.Parser;
import vectorization.parsing.ClientLexer.ClientType;

public class ClientParser extends Parser<ClientCommand> {

	private ClientCommand defaultCommand;

	public ClientParser(Lexer l, ClientCommand defaultCommand) {
		super(l);
		this.defaultCommand = defaultCommand;
	}

	@Override
	public ClientCommand parse() {
		if (getLookAhead().type.equals(ClientType.LITERAL)) {
			String val = getLookAhead().val;
			if(val.equals("source")){ return source(); }
			if(val.equals("quit") || val.equals("exit")){ return exit();}
		}
		return defaultCommand;
	}

	private ClientCommand exit() {
		return new Exit();
	}

	private ClientCommand source() {
		match(ClientType.LITERAL, "source");
		match(ClientType.QUOTE);
		String path = literal();
		match(ClientType.QUOTE);
		return new Source(path);
	}
	
	private String literal() {
		String name = getLookAhead().val;
		match(ClientType.LITERAL);
		return name;
	}

}
