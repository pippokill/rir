Revised Random Indexing
==========================

Random Indexing for textual corpora
---------------------------------------

Random Indexing class: di.uniba.it.rir.cli.RI

usage: Execute Random Indexing [-c <arg>] [-dim <arg>] [-i <arg>] [-lf
       <arg>] [-ns <arg>] [-o <arg>] [-ri] [-rseed <arg>] [-seed <arg>]
       [-st] [-sw <arg>] [-t <arg>] [-txt] [-w <arg>]
 -c <arg>       This will discard words that appear less than <n> times;
                default is 5
 -dim <arg>     The vector dimension (optional, default 300)
 -i <arg>       Input corpus (compressed GZIP files are supported)
 -lf <arg>      Enable letter filter
 -ns <arg>      Number of negative samples (optinal, default 0)
 -o <arg>       Output file
 -ri            Save random vector
 -rseed <arg>   Seed for random inizialization
 -seed <arg>    The number of seeds (optional, default 10)
 -st            Use standard analyzer (default false)
 -sw <arg>      Stop word file
 -t <arg>       Threshold for downsampling frequent words (optinal,
                default 0.001)
 -txt           Enable textual output format
 -w <arg>       Windows size (optional, default 5)

Random Indexing for graph
----------------------------

Random Indexing class: di.uniba.it.rir.cli.RIgraph

usage: Execute Graph Random Indexing [-c <arg>] [-dim <arg>] [-i <arg>]
       [-o <arg>] [-r <arg>] [-rseed <arg>] [-seed <arg>] [-txt] [-u]
 -c <arg>       This will discard verticies that appear less than <n>
                times; default is 5
 -dim <arg>     The vector dimension (optional, default 300)
 -i <arg>       Input file (compressed GZIP files are supported)
 -o <arg>       Output file
 -r <arg>       Reflective steps (optional, default 2)
 -rseed <arg>   Seed for random inizialization
 -seed <arg>    The number of seeds (optional, default 10)
 -txt           Enable textual output format
 -u             Undirected graph

Graph format: one edge for each line, see "sample_graph" file. Comment lines must start with '#'.