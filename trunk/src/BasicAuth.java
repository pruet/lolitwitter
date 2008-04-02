// Copyright (C) 2008 Pruet Boonma <pruetboonma@gmail.com>
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  US



class BasicAuth {
	private BasicAuth() {}
	private static byte[] lookupTable = {
		(byte)'A', (byte)'B', (byte)'C', (byte)'D',
		(byte)'E', (byte)'F', (byte)'G', (byte)'H',
		(byte)'I', (byte)'J', (byte)'K', (byte)'L',
		(byte)'M', (byte)'N', (byte)'O', (byte)'P',
		(byte)'Q', (byte)'R', (byte)'S', (byte)'T',
		(byte)'U', (byte)'V', (byte)'W', (byte)'X',
		(byte)'Y', (byte)'Z', (byte)'a', (byte)'b', 
		(byte)'c', (byte)'d', (byte)'e', (byte)'f', 
		(byte)'g', (byte)'h', (byte)'i', (byte)'j',
		(byte)'k', (byte)'l', (byte)'m', (byte)'n', 
		(byte)'o', (byte)'p', (byte)'q', (byte)'r', 
		(byte)'s', (byte)'t', (byte)'u', (byte)'v', 
		(byte)'w', (byte)'x', (byte)'y', (byte)'z',
		(byte)'0', (byte)'1', (byte)'2', (byte)'3', 
		(byte)'4', (byte)'5', (byte)'6', (byte)'7', 
		(byte)'8', (byte)'9', (byte)'+', (byte)'/'
	};

	static String encode(String name, String passwd) {
		byte in[] = (name + ":" + passwd).getBytes();
		byte[] out = new byte[((in.length / 3) + 1) * 4];
		int rix = 0;
		int chnk = 0;
		for (int i = 0; i < in.length; i += 3) {
			int left = in.length - i;
			if (left > 2) {
				chnk = (in[i] << 16) | (in[i + 1] << 8) | in[i + 2];
				out[rix++] = lookupTable[(chnk&0xFC0000)>>18];
				out[rix++] = lookupTable[(chnk&0x3F000) >>12];
				out[rix++] = lookupTable[(chnk&0xFC0)   >> 6];
				out[rix++] = lookupTable[(chnk&0x3F)];
			} else if (left == 2) {
				chnk = (in[i] << 16) | (in[i + 1] << 8);
				out[rix++] = lookupTable[(chnk&0xFC0000)>>18];
				out[rix++] = lookupTable[(chnk&0x3F000) >>12];
				out[rix++] = lookupTable[(chnk&0xFC0)   >> 6];
				out[rix++] = '=';
			} else {
				chnk = in[i] << 16;
				out[rix++] = lookupTable[(chnk&0xFC0000)>>18];
				out[rix++] = lookupTable[(chnk&0x3F000) >>12];
				out[rix++] = '=';
				out[rix++] = '=';
			}
		}
		return new String(out);
	}
}
