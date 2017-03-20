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
import com.vectorization.parsing.Token;
import com.vectorization.parsing.Token.Type;

public class ClientLexer extends Lexer {
	
	public enum ClientType implements Type{
		LITERAL, QUOTE, EOF_TYPE
	}
	
	public ClientLexer(String input) {
		super(input);
	}

	private boolean isQuote(){
		return getLookAhead() == '\'' || getLookAhead() == '\"';
	}
	
	private Token consumeLiteral(){
		StringBuilder sb = new StringBuilder();
		while(getLookAhead() != EOF && !isQuote() && !Character.isWhitespace(getLookAhead())){
			sb.append(getLookAhead());
			consume();
		}
		return new Token(ClientType.LITERAL, sb.toString());
	}
	
	

	@Override
	public Token next() {
		consumeWhiteSpace();
		if(isQuote()){
			consume();
			return new Token(ClientType.QUOTE, "'");
		}
		if(getLookAhead() != EOF){
			return consumeLiteral();
		}
		return new Token(ClientType.EOF_TYPE, "");
	}
	
	public static void main(String[] args) {
		Lexer l = new ClientLexer("source \"../home/robert/script.ss\"");
		while(l.hasNext()){
			System.out.println(l.next());
		}
	}

}
