// This part is added as-it-is on top of the generated scanner
//
import java_cup.runtime.*; // defines the Symbol class
// The generated scanner will return a Symbol for each token that it finds.
// A Symbol contains an Object field named value; that field will be of type
// TokenVal, defined below.
//
// A TokenVal object contains the line number on which the token occurs as
// well as the number of the character on that line that starts the token.
// Some tokens (literals and IDs) also include the value of the token.
class TokenVal {
      // TO BE COMPLETED Symbol s = new Symbol(sys.WHILE,new TokenVal(yyline,0))
      public int lineNum, charNum;
      public TokenVal(int lineNum, int charNum){
            this.lineNum = lineNum;
            this.charNum = charNum;
      }
}
class IntLitTokenVal extends TokenVal {
      // TO BE COMPLETED
      public int intVal;
      public IntLitTokenVal(int lineNum, int charNum, int text){
            super(lineNum,charNum);
            intVal = text;
      }
}
class IdTokenVal extends TokenVal {
      // TO BE COMPLETED
      public String idVal;
      public IdTokenVal(int lineNum, int charNum, String text){
            super(lineNum,charNum);
            idVal = text;
      }
}
class StrLitTokenVal extends TokenVal {
      // TO BE COMPLETED
      public String strVal;
      public StrLitTokenVal(int lineNum, int charNum, String text){
            super(lineNum,charNum);
            strVal = text;
      }
}
// The following class is used to keep track of the character number at which
// the current token starts on its line.
class CharNum {
    static int num = 1;
}


class Yylex implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NOT_ACCEPT,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NOT_ACCEPT,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NOT_ACCEPT,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NOT_ACCEPT,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NOT_ACCEPT,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NOT_ACCEPT,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NOT_ACCEPT,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NOT_ACCEPT,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NOT_ACCEPT,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NOT_ACCEPT,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NOT_ACCEPT,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NOT_ACCEPT,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NOT_ACCEPT,
		/* 88 */ YY_NOT_ACCEPT,
		/* 89 */ YY_NOT_ACCEPT,
		/* 90 */ YY_NOT_ACCEPT,
		/* 91 */ YY_NOT_ACCEPT,
		/* 92 */ YY_NOT_ACCEPT,
		/* 93 */ YY_NOT_ACCEPT,
		/* 94 */ YY_NOT_ACCEPT,
		/* 95 */ YY_NOT_ACCEPT,
		/* 96 */ YY_NOT_ACCEPT,
		/* 97 */ YY_NOT_ACCEPT,
		/* 98 */ YY_NOT_ACCEPT,
		/* 99 */ YY_NOT_ACCEPT,
		/* 100 */ YY_NOT_ACCEPT,
		/* 101 */ YY_NOT_ACCEPT,
		/* 102 */ YY_NOT_ACCEPT,
		/* 103 */ YY_NOT_ACCEPT,
		/* 104 */ YY_NOT_ACCEPT,
		/* 105 */ YY_NOT_ACCEPT,
		/* 106 */ YY_NOT_ACCEPT,
		/* 107 */ YY_NOT_ACCEPT,
		/* 108 */ YY_NOT_ACCEPT,
		/* 109 */ YY_NOT_ACCEPT,
		/* 110 */ YY_NOT_ACCEPT,
		/* 111 */ YY_NOT_ACCEPT,
		/* 112 */ YY_NOT_ACCEPT,
		/* 113 */ YY_NOT_ACCEPT,
		/* 114 */ YY_NOT_ACCEPT,
		/* 115 */ YY_NOT_ACCEPT,
		/* 116 */ YY_NOT_ACCEPT,
		/* 117 */ YY_NOT_ACCEPT,
		/* 118 */ YY_NOT_ACCEPT,
		/* 119 */ YY_NOT_ACCEPT,
		/* 120 */ YY_NOT_ACCEPT,
		/* 121 */ YY_NOT_ACCEPT,
		/* 122 */ YY_NOT_ACCEPT,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NOT_ACCEPT,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NOT_ACCEPT,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NOT_ACCEPT,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NOT_ACCEPT,
		/* 134 */ YY_NOT_ACCEPT,
		/* 135 */ YY_NOT_ACCEPT,
		/* 136 */ YY_NOT_ACCEPT,
		/* 137 */ YY_NOT_ACCEPT,
		/* 138 */ YY_NOT_ACCEPT,
		/* 139 */ YY_NOT_ACCEPT,
		/* 140 */ YY_NOT_ACCEPT,
		/* 141 */ YY_NOT_ACCEPT,
		/* 142 */ YY_NOT_ACCEPT,
		/* 143 */ YY_NOT_ACCEPT,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NOT_ACCEPT,
		/* 147 */ YY_NOT_ACCEPT,
		/* 148 */ YY_NOT_ACCEPT,
		/* 149 */ YY_NOT_ACCEPT,
		/* 150 */ YY_NOT_ACCEPT,
		/* 151 */ YY_NOT_ACCEPT,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NOT_ACCEPT,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NO_ANCHOR,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR,
		/* 174 */ YY_NO_ANCHOR,
		/* 175 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"45:9,35,36,45:2,43,45:18,35,22,40,44,45:2,26,42,30,31,20,18,33,19,34,21,39:" +
"10,45,32,24,23,25,42,45,37:4,46,37:21,45,41,45:2,38,45,13,4,15,8,11,12,37,1" +
"7,1,37:2,6,37,2,5,37:2,9,14,3,10,7,16,37:3,28,27,29,45:2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,176,
"0,1,2,3,4,1,5,6,7,8,9,10,1:9,11,5,12,1:11,13,12:2,1,12:5,14,12:4,15,12,16,1" +
"5,1,17,18,19,20,15,21,14,22,21,23,1,24,13,25,26,27,28,29,17,30,31,24,32,26," +
"33,34,35,36,37,14,38,22,39,31,40,41,29,42,43,44,45,46,47,48,49,50,51,52,53," +
"54,55,56,57,58,59,60,18,61,62,63,64,65,66,67,68,69,70,71,72,47,73,74,57,65," +
"34,41,47,75,54,57,76,77,47,78,79,80,54,69,65,81,82,18,48,83,22,68,84,72,85," +
"65,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,10" +
"7")[0];

	private int yy_nxt[][] = unpackFromString(108,47,
"1,2,50,162,163,50:2,164,50,174,50,165,170,50,175,125,171,50,3,4,5,6,7,8,9,1" +
"0,11,51,12,13,14,15,16,17,18,19,20,50:2,21,58,64:2,-1,22,64,50,-1:48,50,57," +
"50:9,23,50:5,-1:19,50,-1,50,-1:6,50,-1:18,24,-1:47,25,-1:28,22:35,-1,22:6,-" +
"1,22:3,-1:23,26,-1:46,27,-1:46,28,29,-1:45,30,-1,31,-1:47,32,-1:59,21,-1:8," +
"50:17,-1:19,50,-1,50,-1:6,50,-1,66:35,-1,66:4,69,66,-1,66:3,-1,83:35,-1,83:" +
"3,44,95,83,-1,83:3,-1,49:35,34,49:3,35,56,49,62,49:2,124,-1:27,33,-1:20,72:" +
"35,38,72:3,44,154,72,85,72:2,146,-1,110:35,34,110:3,60,114,110,89,110:2,151" +
",-1,72,75:2,72:32,38,72:3,77,134,75,133,72:2,146,-1,50:2,36,50:14,-1:19,50," +
"-1,50,-1:6,50,-1,62:35,34,62:3,53,79,62:4,131,-1,85:35,38,85:3,-1,148,85:4," +
"137,-1,50,37,50:15,-1:19,50,-1,50,-1:6,50,-1,75:35,34,75:3,35,56,75,87,75:2" +
",147,-1,50:10,39,50:6,-1:19,50,-1,50,-1:6,50,-1,77:35,34,77:3,35,88,77,89,7" +
"7:2,135,-1,83,66:2,83:32,-1,83:3,66,69,66,136,83:3,-1,50:5,40,50:11,-1:19,5" +
"0,-1,50,-1:6,50,-1,90:35,34,90:3,60,102,90,87,90:2,149,-1,50:7,41,50:9,-1:1" +
"9,50,-1,50,-1:6,50,-1,87:35,34,87:3,53,128,87:4,138,-1,50:10,42,50:6,-1:19," +
"50,-1,50,-1:6,50,-1,50:2,43,50:14,-1:19,50,-1,50,-1:6,50,-1,85,87:2,85:32,3" +
"8,85:3,89,87:2,85:3,137,-1,50:10,45,50:6,-1:19,50,-1,50,-1:6,50,-1,49:11,52" +
",49:23,34,49:3,35,56,49,62,49:2,124,-1,50:10,46,50:6,-1:19,50,-1,50,-1:6,50" +
",-1,50,47,50:15,-1:19,50,-1,50,-1:6,50,-1,50:2,48,50:14,-1:19,50,-1,50,-1:6" +
",50,-1,83,77:2,83:32,-1,83:3,77,99,77,136,83:3,-1,89:35,34,89:3,53,100,89:4" +
",139,-1,106,133:2,106:32,38,106:3,123,91,133,85,106:2,92,-1,133:4,107,133:3" +
"0,38,133:3,123,91,133,85,133:2,92,-1,72:11,54,72:23,38,72:3,44,154,72,85,72" +
":2,146,-1,62:11,59,62:23,34,62:3,53,79,62:4,131,-1,83:35,-1,83:3,44,95,83,1" +
"36,83:3,-1,96:35,-1,96:3,123,96:2,-1,96:3,-1,72:4,93,72:30,38,72:3,44,154,7" +
"2,85,72:2,146,-1,75:11,65,75:23,34,75:3,35,56,75,87,75:2,147,-1,110,77:2,11" +
"0:32,34,110:3,35,88,77,142,110:2,151,-1:2,89:2,-1:36,89:3,-1:5,77:11,68,77:" +
"23,34,77:3,35,88,77,89,77:2,135,-1,72,90:2,72:32,38,72:3,55,112,90,133,72:2" +
",97,-1,103:35,34,103:3,130,104,103,87,103:2,141,-1,106,103:2,106:32,38,106:" +
"3,127,150,103,85,106:2,92,-1,90:11,71,90:23,34,90:3,60,102,90,87,90:2,149,-" +
"1,133:35,38,133:3,123,91,133,85,133:2,92,-1,133:11,126,133:23,38,133:3,123," +
"91,133,85,133:2,92,-1,85:11,61,85:23,38,85:3,-1,148,85:4,137,-1,87:11,74,87" +
":23,34,87:3,53,128,87:4,138,-1,89:11,129,89:23,34,89:3,53,100,89:4,139,-1,9" +
"0:35,34,90:3,60,102,90,140,90:2,118,-1,103:11,132,103:23,34,103:3,130,104,1" +
"03,87,103:2,141,-1,83,110:2,83:32,-1,83:3,55,120,110,136,83:3,-1,115:35,34," +
"115:3,130,116,115,89,115:2,143,-1,96,115:2,96:32,-1,96:3,127,115:2,-1,96:3," +
"-1,110:11,145,110:23,34,110:3,60,114,110,89,110:2,151,-1,90:4,105,90:30,34," +
"90:3,60,102,90,87,90:2,149,-1,103:4,113,103:30,34,103:3,130,104,103,87,103:" +
"2,141,-1,110:35,34,110:3,60,114,110,142,110:2,122,-1,115:11,153,115:23,34,1" +
"15:3,130,116,115,89,115:2,143,-1,110:4,117,110:30,34,110:3,60,114,110,89,11" +
"0:2,151,-1,49:4,81,49:30,34,49:3,35,56,49,62,49:2,124,-1,63,50:3,157,50:12," +
"-1:19,50,-1,50,-1:6,50,-1,62:4,94,62:30,34,62:3,53,79,62:4,131,-1,90,75:2,9" +
"0:32,34,90:3,35,56,75,140,90:2,149,-1,77:4,101,77:30,34,77:3,35,88,77,89,77" +
":2,135,-1,85:4,108,85:30,38,85:3,-1,148,85:4,137,-1,87:4,109,87:30,34,87:3," +
"53,128,87:4,138,-1,89:4,111,89:30,34,89:3,53,100,89:4,139,-1,115:4,121,115:" +
"30,34,115:3,130,116,115,89,115:2,143,-1,50:9,67,50:7,-1:19,50,-1,50,-1:6,50" +
",-1,75:4,98,75:30,34,75:3,35,56,75,87,75:2,147,-1,103:35,34,103:3,130,104,1" +
"03,87,103:2,119,-1,50:4,70,50:12,-1:19,50,-1,50,-1:6,50,-1,72:35,38,72:3,44" +
",154,72,133,72:2,97,-1,73,50:16,-1:19,50,-1,50,-1:6,50,-1,50:13,76,50:3,-1:" +
"19,50,-1,50,-1:6,50,-1,50:9,78,50:7,-1:19,50,-1,50,-1:6,50,-1,50:13,80,50:3" +
",-1:19,50,-1,50,-1:6,50,-1,50:5,82,50:11,-1:19,50,-1,50,-1:6,50,-1,50:8,84," +
"50:8,-1:19,50,-1,50,-1:6,50,-1,50:14,86,50:2,-1:19,50,-1,50,-1:6,50,-1,50:8" +
",144,50:8,-1:19,50,-1,50,-1:6,50,-1,50:4,152,50:12,-1:19,50,-1,50,-1:6,50,-" +
"1,50:4,155,50:12,-1:19,50,-1,50,-1:6,50,-1,50:5,156,50:11,-1:19,50,-1,50,-1" +
":6,50,-1,50:5,158,50:11,-1:19,50,-1,50,-1:6,50,-1,159,50:16,-1:19,50,-1,50," +
"-1:6,50,-1,50:9,160,50:7,-1:19,50,-1,50,-1:6,50,-1,50:9,161,50:7,-1:19,50,-" +
"1,50,-1:6,50,-1,50:12,166,50:4,-1:19,50,-1,50,-1:6,50,-1,50:16,167,-1:19,50" +
",-1,50,-1:6,50,-1,50:2,168,50:14,-1:19,50,-1,50,-1:6,50,-1,50:8,169,50:8,-1" +
":19,50,-1,50,-1:6,50,-1,50:10,172,50:6,-1:19,50,-1,50,-1:6,50,-1,50:2,173,5" +
"0:14,-1:19,50,-1,50,-1:6,50");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

return new Symbol(sym.EOF);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -3:
						break;
					case 3:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.PLUS, new TokenVal(yyline+1,temp));
}
					case -4:
						break;
					case 4:
						{
      int temp = CharNum.num;
      CharNum.num ++;
      return new Symbol(sym.MINUS, new TokenVal(yyline+1,temp));
}
					case -5:
						break;
					case 5:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.TIMES, new TokenVal(yyline+1,temp));
}
					case -6:
						break;
					case 6:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.DIVIDE, new TokenVal(yyline+1,temp));
}
					case -7:
						break;
					case 7:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.NOT, new TokenVal(yyline+1,temp));
}
					case -8:
						break;
					case 8:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.ASSIGN, new TokenVal(yyline+1,temp));
}
					case -9:
						break;
					case 9:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.LESS, new TokenVal(yyline+1,temp));
}
					case -10:
						break;
					case 10:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.GREATER, new TokenVal(yyline+1,temp));
}
					case -11:
						break;
					case 11:
						{
      int temp = CharNum.num;
      CharNum.num++;
      ErrMsg.fatal(yyline+1, temp, "illegal character ignored: "+yytext());
}
					case -12:
						break;
					case 12:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.LCURLY, new TokenVal(yyline+1,temp));
}
					case -13:
						break;
					case 13:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.RCURLY, new TokenVal(yyline+1,temp));
}
					case -14:
						break;
					case 14:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.LPAREN, new TokenVal(yyline+1,temp));
}
					case -15:
						break;
					case 15:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.RPAREN, new TokenVal(yyline+1,temp));
}
					case -16:
						break;
					case 16:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.SEMICOLON, new TokenVal(yyline+1,temp));
}
					case -17:
						break;
					case 17:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.COMMA, new TokenVal(yyline+1,temp));
}
					case -18:
						break;
					case 18:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.DOT, new TokenVal(yyline+1,temp));
}
					case -19:
						break;
					case 19:
						{
      int temp = CharNum.num;
      CharNum.num++;
}
					case -20:
						break;
					case 20:
						{
      CharNum.num = 1;
}
					case -21:
						break;
					case 21:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      long val = new Long(yytext()).longValue();
      if(val > Integer.MAX_VALUE){
            ErrMsg.warn(yyline+1, temp, "integer literal too large");
            return new Symbol(sym.INTLITERAL, new IntLitTokenVal(yyline+1,temp,Integer.MAX_VALUE));
      }
      else{
            return new Symbol(sym.INTLITERAL, new IntLitTokenVal(yyline+1,temp,(new Integer(yytext()).intValue())));
      }
}
					case -22:
						break;
					case 22:
						{
      CharNum.num += yytext().length();
}
					case -23:
						break;
					case 23:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.IF, new TokenVal(yyline+1,temp));
}
					case -24:
						break;
					case 24:
						{
      int temp = CharNum.num;
      CharNum.num += 2;
      return new Symbol(sym.PLUSPLUS, new TokenVal(yyline+1,temp));
}
					case -25:
						break;
					case 25:
						{
      int temp = CharNum.num;
      CharNum.num += 2;
      return new Symbol(sym.MINUSMINUS, new TokenVal(yyline+1,temp));
}
					case -26:
						break;
					case 26:
						{
      int temp = CharNum.num;
      CharNum.num++;
      return new Symbol(sym.NOTEQUALS, new TokenVal(yyline+1,temp));
}
					case -27:
						break;
					case 27:
						{
      int temp = CharNum.num;
      CharNum.num+=2;
      return new Symbol(sym.EQUALS, new TokenVal(yyline+1,temp));
}
					case -28:
						break;
					case 28:
						{
      int temp = CharNum.num;
      CharNum.num+=2;
      return new Symbol(sym.LESSEQ, new TokenVal(yyline+1,temp));
}
					case -29:
						break;
					case 29:
						{
      int temp = CharNum.num;
      CharNum.num += 2;
      return new Symbol(sym.WRITE, new TokenVal(yyline+1,temp));
}
					case -30:
						break;
					case 30:
						{
      int temp = CharNum.num;
      CharNum.num+=2;
      return new Symbol(sym.GREATEREQ, new TokenVal(yyline+1,temp));
}
					case -31:
						break;
					case 31:
						{
      int temp = CharNum.num;
      CharNum.num += 2;
      return new Symbol(sym.READ, new TokenVal(yyline+1,temp));
}
					case -32:
						break;
					case 32:
						{
      int temp = CharNum.num;
      CharNum.num+=2;
      return new Symbol(sym.AND, new TokenVal(yyline+1,temp));
}
					case -33:
						break;
					case 33:
						{
      int temp = CharNum.num;
      CharNum.num+=2;
      return new Symbol(sym.OR, new TokenVal(yyline+1,temp));
}
					case -34:
						break;
					case 34:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated stringliteral ignored");
}
					case -35:
						break;
					case 35:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.STRINGLITERAL, new StrLitTokenVal(yyline+1,temp,yytext()));
}
					case -36:
						break;
					case 36:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.INT, new TokenVal(yyline+1,temp));
}
					case -37:
						break;
					case 37:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.CIN, new TokenVal(yyline+1,temp));
}
					case -38:
						break;
					case 38:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated string literal with bad escaped character ignored");
}
					case -39:
						break;
					case 39:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.TRUE, new TokenVal(yyline+1,temp));
}
					case -40:
						break;
					case 40:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.BOOL, new TokenVal(yyline+1,temp));
}
					case -41:
						break;
					case 41:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.VOID, new TokenVal(yyline+1,temp));
}
					case -42:
						break;
					case 42:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ELSE, new TokenVal(yyline+1,temp));
}
					case -43:
						break;
					case 43:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.COUT, new TokenVal(yyline+1,temp));
}
					case -44:
						break;
					case 44:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      ErrMsg.fatal(yyline+1, temp, "string literal with bad escaped character ignored");
}
					case -45:
						break;
					case 45:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.FALSE, new TokenVal(yyline+1,temp));
}
					case -46:
						break;
					case 46:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.WHILE, new TokenVal(yyline+1,temp));
}
					case -47:
						break;
					case 47:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.RETURN, new TokenVal(yyline+1,temp));
}
					case -48:
						break;
					case 48:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.STRUCT, new TokenVal(yyline+1,temp));
}
					case -49:
						break;
					case 50:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -50:
						break;
					case 51:
						{
      int temp = CharNum.num;
      CharNum.num++;
      ErrMsg.fatal(yyline+1, temp, "illegal character ignored: "+yytext());
}
					case -51:
						break;
					case 52:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated stringliteral ignored");
}
					case -52:
						break;
					case 53:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.STRINGLITERAL, new StrLitTokenVal(yyline+1,temp,yytext()));
}
					case -53:
						break;
					case 54:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated string literal with bad escaped character ignored");
}
					case -54:
						break;
					case 55:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      ErrMsg.fatal(yyline+1, temp, "string literal with bad escaped character ignored");
}
					case -55:
						break;
					case 57:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -56:
						break;
					case 58:
						{
      int temp = CharNum.num;
      CharNum.num++;
      ErrMsg.fatal(yyline+1, temp, "illegal character ignored: "+yytext());
}
					case -57:
						break;
					case 59:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated stringliteral ignored");
}
					case -58:
						break;
					case 60:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.STRINGLITERAL, new StrLitTokenVal(yyline+1,temp,yytext()));
}
					case -59:
						break;
					case 61:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated string literal with bad escaped character ignored");
}
					case -60:
						break;
					case 63:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -61:
						break;
					case 64:
						{
      int temp = CharNum.num;
      CharNum.num++;
      ErrMsg.fatal(yyline+1, temp, "illegal character ignored: "+yytext());
}
					case -62:
						break;
					case 65:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated stringliteral ignored");
}
					case -63:
						break;
					case 67:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -64:
						break;
					case 68:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated stringliteral ignored");
}
					case -65:
						break;
					case 70:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -66:
						break;
					case 71:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated stringliteral ignored");
}
					case -67:
						break;
					case 73:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -68:
						break;
					case 74:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated stringliteral ignored");
}
					case -69:
						break;
					case 76:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -70:
						break;
					case 78:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -71:
						break;
					case 80:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -72:
						break;
					case 82:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -73:
						break;
					case 84:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -74:
						break;
					case 86:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -75:
						break;
					case 123:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      ErrMsg.fatal(yyline+1, temp, "string literal with bad escaped character ignored");
}
					case -76:
						break;
					case 125:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -77:
						break;
					case 126:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated string literal with bad escaped character ignored");
}
					case -78:
						break;
					case 127:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      ErrMsg.fatal(yyline+1, temp, "string literal with bad escaped character ignored");
}
					case -79:
						break;
					case 129:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated stringliteral ignored");
}
					case -80:
						break;
					case 130:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.STRINGLITERAL, new StrLitTokenVal(yyline+1,temp,yytext()));
}
					case -81:
						break;
					case 132:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated stringliteral ignored");
}
					case -82:
						break;
					case 144:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -83:
						break;
					case 145:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated stringliteral ignored");
}
					case -84:
						break;
					case 152:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -85:
						break;
					case 153:
						{
      int temp = CharNum.num;
      CharNum.num = 1;
      ErrMsg.fatal(yyline+1, temp, "unterminated stringliteral ignored");
}
					case -86:
						break;
					case 155:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -87:
						break;
					case 156:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -88:
						break;
					case 157:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -89:
						break;
					case 158:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -90:
						break;
					case 159:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -91:
						break;
					case 160:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -92:
						break;
					case 161:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -93:
						break;
					case 162:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -94:
						break;
					case 163:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -95:
						break;
					case 164:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -96:
						break;
					case 165:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -97:
						break;
					case 166:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -98:
						break;
					case 167:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -99:
						break;
					case 168:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -100:
						break;
					case 169:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -101:
						break;
					case 170:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -102:
						break;
					case 171:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -103:
						break;
					case 172:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -104:
						break;
					case 173:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -105:
						break;
					case 174:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -106:
						break;
					case 175:
						{
      int temp = CharNum.num;
      CharNum.num += yytext().length();
      return new Symbol(sym.ID, new IdTokenVal(yyline+1,temp,yytext()));
}
					case -107:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
