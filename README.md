# jorth

## jorth it up baby!!!!!!!!

a mini forth-like stack-based language running in Java with a Swing frontend. made because i want to put a forth computer in minecraft someday.

### syntax
jorth is made up of **words** separated by whitespace. each word either pushes a number to the stack or does something to the stack. arithmetic is written in reverse polish notation, i.e. `(1 + 2) -> 1 2 +`

### current dictionary
```
word					details
def			-- 		start defining a word
;			--		end definition of a word
+,-,*,/		--		standard arithmetic operators
%			--		modulo
|			--		bitwise or
&			--		bitwise and
.			--		pop top of the stack & print it
.S			--		print the whole stack from bottom to top (non-destructive)
//			--		(not a word) begin comment
\\			--		end comment
```
### planned features
- control flow
- variables