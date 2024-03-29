Parser and AST creator for Scheme language written in Java
==========================================================

Parser and AST creator for Scheme language, written in Java for an university Project.

Read doc.html for the documentation (Italian).

[University of Bologna](http://corsi.unibo.it/) 2006

Documentation

<h2>Elenco componenti del gruppo</h2>

Elvis Ciotti
matr. 261459
[omissis for privacy reasons]

<p>
<h2>Descrizione architettura progetto</h2>

Lo scanner &egrave; stato Implementato tramite il tool JLex.<br>
Nel rispetto delle specifiche, si è inserita la direttiva <em>%line</em> in modo da memorizzare il numero di linea dei token.<br>
Le espressioni regolari riconoscono i caratteri di parentesi aperta e chiusa, le parole chiave (<em>define #t #f and or cond else local lambda list</em>), le stringhe (cioè il testo fra virgolette che non contiene virgolette).<br>
Gli identificatori di variabili devono iniziare con un carattere lettera (<em>[a-zA-Z_]</em>) oppure un underscore e possono contenere anche i caratteri numerici e il carattere '<em>?</em>'   (<em>[a-zA-Z0-9_?]*</em>)
Come da specifica, gli operatori (+,-,*,/) sono già riconosciuti come identificatori.<br />
Gli interi sono semplici sequenze di almeno una cifra numerica ([0-9]+ )
<p>

Siccome per ogni token si vuole memorizzare il numero di linea e l'eventuale contenuto (per stringhe, interi, identificatori), si è costruita appositamente la classe <em><b>Token</b></em>, che permette per ogni token di memorizzarne il tipo (con il relativo metodo per restituirlo), la linea e l'eventuale contenuto.<br>
Per esempio se alla chiamata del metodo tok() lo scanner analizza "abc",  viene restituito un Token costruito con <em>Token(line, Costanti.STRING,tokstring)</em>, cioè passandogli numero di linea, tipo del token e valore della stringa.<br>

La classe <em><b>Costanti</b></em> è usata per le costanti globali usate dalle classi e dichiara come "<em>static public final int</em>" i tipi di token.<br>

Il costruttore dello scanner (<em><b>SchemeScannerImpl</b></em>) apre il file sorgente e istanzia <em>Yylex</em>, poi tramite il metodo <i>nextToken()</i> viene restituito il Token successivo. Il metodo <i>int get_token()</i>  restituisce un valore della classe settato dalla <i>set_token()</i> dal parser in modo in <em>Schemer</em> il ciclo while termini quando non ci sono più definizioni
<p>

Il parser (<b>SchemeParserImpl</b>) funziona lavorando su una una LinkedList di oggetti Token.
Il costruttore riempie la lista di Token chiamando la <i>nextToken()</i> dello scanner.
Successivamente si analizza la lista e i token di tipo intero, identificatore, stringa, booleani sono sostituiti da nuovi oggetti Token il cui contenuto è un oggetto <em>SchemeExpression</em>, creato tramite il relativo costruttore in cui viene passato il numero di linea, il tipo del token e l'espressione costruita tramite le relative funzioni della <i>SchemeFactory</i> fornita.<br />
La classe Token contiene infatti anche una puntatore a <em>SchemeExpression</em>, in modo che possa contenere qualsiasi espressione.
Poi vengono di volta in volta analizzati i token fra le parentesi più interne (cioè si analizza prima la lista di token fra l'ultima parantesi aperta e la corrispondente parentesi chiusa)
e risolti dalla <i>risolvi()</i> a cui passiamo appunto gli indici che specificano quale porzione della lista rimpiazzare con la relativa espressione più ad alto livello nell'albero di parsing.<p>

Siccome lavoro su una lista di Token, un Token può contenere qualsiasi cosa, anche <em>SchemeBranch</em>, <em>SchemeDefinition</em>, inseriti tramite i relativi costruttori e puntatori nella classe. La variabile <i>tipo</i> della classe Token permette di riconoscere se si tratta di un Token passato dallo scanner (TOKEN_SCANNER), se contiene una espressione(<em>SCHEME_EXPRESS</em>) o un branch (<em>SCHEME_BRANCH</em>) o una definition <em>SCHEME_DEFINITION</em>) o un altro tipo di Token temporaneo che andrà poi inserito dentro altre espressioni (<em>TOKEN_ELSE</em> e <em>TOKEN_VUOTO</em> per lambda con n=0)
<p>

Procedendo con un match sul tipo del token per il primo elemento da parte della <i>risolvi()</i>:
<ul>
<li> Se incontriamo <b>AND</b> (o OR) rimpiazziamo la porzione con un Token che impostiamo di tipo <em>Costanti.AND</em> che contiene una espressione creata tramite la <em>SchemeFactory</em> (contenente in questo caso la lista delle espressioni presenti dopo AND (o OR) ), oltre al numero di linea che prendiamo dal Token contenente l'AND.
<br>
</li><li>
Nel caso della <b>define</b> semplice rimpiazziamo la porzione di lista con un Token contenente la definizione e costruito con l'id e con l'espressione relativa.<br>
Nel caso di define con una lista di argomenti al espandiamo in modo che costruisca un Token contenente la definizione in cui il primo id è l'id della define e l'espressione è una lambda con i restati id e l'espressione della define<br>

</li><li>Se incontriamo un else viene creato un Token temporaneo che verrà inglobato nella cond esterna.<br>

</li><li>Nel caso della <b>cond</b> verifichiamo se gli argomenti (per il momento sono liste di espressioni, già analizzate dalla <i>risolvi<i> precedentemente visto che sono al livello più interno) possano essere branch (cioè contengano due espressioni) e se esiste o no il token else alla fine. creiamo eventualmente gli oggetti <em>SchemeBranch</em> con la <em>createBranch</em> di <em>SchemeFactory</em> e con questi creiamo il Token di tipo <em>Costanti.COND</em> (o <em>Costanti.CONDELSE</em>) contenente l'espressione creata con la createCondExpression() e lo rimpiazziamo nella porzione di lista<br>

</li>
<li>Per la <b>local</b> rimpiazziamo la porzione con il Token costruito passando (oltre al tipo <em>Costanti.LOCAL</em> del Token) l'espressione costruita tramite la createLocalExpression, a cui passiamo la lista di define presenti e l'espressione in fondo alla local.<br>
</li><li>Nel caso di <b>lambda</b> consideriamo anche il caso in cui n=0, ovvero non ci sono id.<br>
Quindi in modo simile ai casi precedente costruiamo il Token di tipo <em>Costanti.LAMBDA</em>.<br>

</li><li>Nel caso di <b>list</b> creiamo il token di tipo <em>Costanti.LIST</em> contenente l'espressione creata con la createApplyExpression<br>

</li><li>Nel caso incontrassimo un'<b>espressione</b> (già trasformato in token contenente una espressione) come primo token della porzione, se anche tutti gli altri token della porzione sono espressioni, creiamo un token di tipo <em>Costanti.LISTA_EXPR</em> contenente la lista delle espressioni<br>

</li><li>Se il primo token della porzione è un token di tipo <b>definition</b> significa che inizia una lista di <em>define</em> dentro una <em>local</em>. Rimpiazziamo la porzione con un token di tipo <em>Costanti.DEFINE_INTERNA</em> contenente la lista di definizioni <br>
</li>
</ul>

<p>Ogni errore che non rispetta la grammatica &egrave; segnalato da <em>SchemeSyntaxError</em></p>
<p>Alla fine la lista di token del parse contiene token con sole definizioni.
parseDefine() restituisce i vari oggetti SchemeDefinition dai token alle varie chiamate.<br>


<i><strong>NOTA</strong>: Ogni token contiene due flag:<br />
</i> tipo<i> = indica se è un token dallo scanner, oppure una espressione, un branch o una def.<br>
</i>tipo_token<i> = se è una espressione indica di che tipo è (cond, lambda, or...)<br>
Spesso nella costruzione dei token alcuni costruttori sono chiamati solo da espressioni di un certo tipo, quindi  il tipo del token non viene passato come parametro ma viene automaticamente scritto dal costruttore.<br>
</i></p>
<h2>Note implementative</h2>
Per la lista dei token e altre liste di oggetti è stata usata la classe List di java con alcune funzionalità di JDK 1.5.<br>

Per il lancio degli errori viene istanziato un oggetto <em>SchemeSyntaxError</em> che stampa l'errore e termina l'esecuzione.



<h4>Implementazione delle classi SchemeExpression, SchemeDefinition, SchemeBranch per il debug</h4>
<em>Schemer</em> stampa ogni volta l'oggetto <em>schemedefinition</em> restituito dalla <em>parseDefine</em>() in questo modo:<br />
<em>SCHEME_DEF( &lt;nomeid> &lt;expr> )</em><br />
es: <em><i>(define tre 3)</em><i> stampa <em>SCHEME_DEF( tre EXPR_INT(3) )</em><br />
<br />
Un'oggetto <em>SchemeBranch</em> viene invece stampato con:<br />
<em>SCHEME_BRANCH( &lt;expr1&gt; &lt;expr2&gt; )</em><br />
es: <i><em>(1 2)</em><i> se &egrave; all'interno della cond, stampa <em>SCHEME_BRANCH(EXPR_INT(1) EXPR_INT(2))</em><br />
<br />
Come vengono stampate le altre espressioni:  (
<em>&lt;x&gt; indica che x contiene la stampa dell'espressione o definizione o branch x </em> )
<table border="1">
<tr>
<td><strong><font size="2" face="Verdana">tipo di espressione </font></strong></td>
<td><strong><font size="2" face="Verdana">come viene stampata </font></strong></td>
<td><strong><font size="2" face="Verdana">esempio codice </font></strong></td>
<td><strong><font size="2" face="Verdana">esempio stampa </font></strong></td>
</tr>
<tr><td><font size="2" face="Verdana">
Identificatori	</font></td>
<td><font size="2" face="Verdana"> EXPR_ID( &lt;valore stringa dell'id&gt; ) </font></td>
<td><font size="2" face="Verdana">uno</font></td>
<td><font size="2" face="Verdana">EXPR_ID( uno ) </font></td>
</tr><tr><td bgcolor="#E4E4E4"><font size="2" face="Verdana">
Stringhe	</font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana"> EXPR_STRING( &lt;valore stringa dell'espressione&gt; ) </font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana">&quot;due&quot;</font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana">EXPR_STRING( &quot;due&quot; )</font></td>
</tr><tr><td><font size="2" face="Verdana">
Int	</font></td>
<td><font size="2" face="Verdana"> EXPR_INT( &lt;valore intero&gt; ) </font></td>
<td><font size="2" face="Verdana">1</font></td>
<td><font size="2" face="Verdana">EXPR_INT(1)</font></td>
</tr><tr><td bgcolor="#E4E4E4"><font size="2" face="Verdana">
And		</font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana"> EXPR_AND( &lt;expr1&gt; &lt;expr2&gt; ... &lt;exprn&gt; ) </font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana">(AND 1 uno) </font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana">EXPR_AND( EXPR_INT(1)  EXPR_ID(uno)</font></td>
</tr><tr><td><font size="2" face="Verdana">
Or		</font></td>
<td><font size="2" face="Verdana"> EXPR_OR( &lt;expr1&gt; &lt;expr2&gt; ... &lt;exprn&gt; ) </font></td>
<td><font size="2" face="Verdana">&nbsp;</font></td>
<td><font size="2" face="Verdana">&nbsp;</font></td>
</tr><tr><td bgcolor="#E4E4E4"><font size="2" face="Verdana">
True	</font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana">		BOOL(true)
</font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana">#t</font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana">BOOL(true)</font></td>
</tr><tr><td><font size="2" face="Verdana">
False		</font></td>
<td><font size="2" face="Verdana">	BOOL(false)
</font></td>
<td><font size="2" face="Verdana">&nbsp;</font></td>
<td><font size="2" face="Verdana">&nbsp;</font></td>
</tr><tr><td bgcolor="#E4E4E4"><font size="2" face="Verdana">
Cond senza else	</font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana"> EXPR_COND( &lt;branch1&gt; &lt;branch2&gt; ... &lt;branchn&gt; ) </font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana">(cond (1 2)) </font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana">EXPR_COND( <br />
SCHEME_BRANCH( EXPR_ID(uno)  EXPR_INT(2)  )<br />
) </font></td>
</tr><tr><td><font size="2" face="Verdana">
Cond con else	</font></td>
<td><font size="2" face="Verdana"> EXPR_CONDELSE( &lt;branch1&gt; &lt;branch2&gt; ... &lt;branchn&gt; &lt;expr> ) </font></td>
<td><font size="2" face="Verdana">(cond (1 2)(else 3))</font></td>
<td><font size="2" face="Verdana">EXPR_COND( <br />
SCHEME_BRANCH( EXPR_ID(uno)  EXPR_INT(2)  )  <br />
EXPR_INT(3)<br />
)</font></td>
</tr><tr><td bgcolor="#E4E4E4"><font size="2" face="Verdana">
Local	</font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana"> EXPR_LOCAL( &lt;define1&gt; &lt;define2&gt; ... &lt;definen&gt;  &lt;expr&gt; ) </font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana">(local ((define due 2))<br />
(* due due) ) </font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana">EXPR_LOCAL( SCHEME_DEF( due EXPR_INT(2)  )<br />
EXPR_LISTA_EXPRS( EXPR_ID(*)  EXPR_ID(due)  EXPR_ID(due)   ) ) </font></td>
</tr><tr><td><font size="2" face="Verdana">
Lambda		</font></td>
<td><font size="2" face="Verdana"> EXPR_LAMBDA( ( &lt;expr di id1&gt; ... &lt;expr di idn ) &lt;expr&gt;  ) </font></td>
<td><font size="2" face="Verdana">(lambda (...) 5) </font></td>
<td><font size="2" face="Verdana">EXPR_LAMBDA( ( ...) EXPR_INT(5)  )</font></td>
</tr><tr><td bgcolor="#E4E4E4"><font size="2" face="Verdana">
Lista di espressioni	</font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana"> EXPR_LISTA_EXPRS( &lt;expr1&gt; &lt;expr2&gt; ... &lt;exprn&gt; ) </font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana">(* 1 2)</font></td>
<td bgcolor="#E4E4E4"><font size="2" face="Verdana"> EXPR_LISTA_EXPRS(EXPR_ID(*)   EXPR_ID(x1)  EXPR_ID(x2)   )</font></td>
</tr><tr><td><font size="2" face="Verdana">
List		</font></td>
<td><font size="2" face="Verdana"> EXPR_LIST( &lt;expr1> &lt;expr2&gt; ... &lt;exprn&gt; ) </font></td>
<td><font size="2" face="Verdana">(list 1 2)</font></td>
<td><font size="2" face="Verdana">EXPR_LIST( EXPR_INT(1)    EXPR_INT(2)   )</font></td>
</tr>
</table>
</font>
<p>Esempi di input sono nei file txt in questa cartella </p>
