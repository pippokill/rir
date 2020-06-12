Revised Random Indexing
==========================

Random Indexing for textual corpora
---------------------------------------

Random Indexing class: di.uniba.it.rir.cli.RI

usage: Execute Random Indexing [-c <arg>] [-dim <arg>] [-i <arg>] [-lf<br>
       <arg>] [-ns <arg>] [-o <arg>] [-ri] [-rseed <arg>] [-seed <arg>]<br>
       [-st] [-sw <arg>] [-t <arg>] [-txt] [-w <arg>]<br>
 -c <arg>       This will discard words that appear less than <n> times;<br>
                default is 5<br>
 -dim <arg>     The vector dimension (optional, default 300)<br>
 -i <arg>       Input corpus (compressed GZIP files are supported)<br>
 -lf <arg>      Enable letter filter<br>
 -ns <arg>      Number of negative samples (optinal, default 0)<br>
 -o <arg>       Output file<br>
 -ri            Save random vector<br>
 -rseed <arg>   Seed for random inizialization<br>
 -seed <arg>    The number of seeds (optional, default 10)<br>
 -st            Use standard analyzer (default false)<br>
 -sw <arg>      Stop word file<br>
 -t <arg>       Threshold for downsampling frequent words (optinal,<br>
                default 0.001)<br>
 -txt           Enable textual output format<br>
 -w <arg>       Windows size (optional, default 5)<br>

Random Indexing for graph
----------------------------

Random Indexing class: di.uniba.it.rir.cli.RIgraph

usage: Execute Graph Random Indexing [-c <arg>] [-dim <arg>] [-i <arg>]<br>
       [-o <arg>] [-r <arg>] [-rseed <arg>] [-seed <arg>] [-txt] [-u]<br>
 -c <arg>       This will discard verticies that appear less than <n><br>
                times; default is 5<br>
 -dim <arg>     The vector dimension (optional, default 300)<br>
 -i <arg>       Input file (compressed GZIP files are supported)<br>
 -o <arg>       Output file<br>
 -r <arg>       Reflective steps (optional, default 2)<br>
 -rseed <arg>   Seed for random inizialization<br>
 -seed <arg>    The number of seeds (optional, default 10)<br>
 -txt           Enable textual output format<br>
 -u             Undirected graph<br>

Graph format: one edge for each line, see "sample_graph" file. Comment lines must start with '#'.