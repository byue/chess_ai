# Project 3 (Chess) Write-Up #
--------

## Project Enjoyment ##
- How Was Your Partnership?
  <pre>
  Great, we have enjoyed our partnership and are looking forward to wrapping up a great quarter.
  </pre>
  
- What was your favorite part of the project?
  <pre>
  My favorite part of the project was seeing how well my bot could do against flexo and bender. It was really cool
  to see how much of a difference move ordering made in the runtime of the evaluations. 
  </pre>

- What was your least favorite part of the project?
  <pre>
  My least favorite part was debugging. I forgot to have the leaf case in jamboree and just had the alpha beta base 
  case, which significantly decreased the quality of my moves. This was a tricky bug since it passed all the gitlab
  tests and the tests I did on my program. I eventually figured out what was going on by printing out the moves and 
  associated values. It was hard to figure out where to put the print statements. 
  </pre>

- How could the project be improved?
  <pre>
  I wish we had more time to learn other algorithms such as negamax and MTD-f. It would have been more interesting
  if we had more background/time to decide on alternative implementations. I also wished the easychess server crashed
  less, since I had to wait a while to complete my games. 
  </pre>

- Did you enjoy the project?
  <pre>
  I really enjoyed this project since I used to play chess a lot. It was fun to look at the game from a programming
  perspective and to see the impact of simple fixes such as changing the order of code lines and implementing tricks
  such as move ordering and iterative deepening. I was really satisfied in seeing my code translate to a bot that could
  make coherent moves. Although the bot was not perfect, I was satisfied we could see our bot improving in several
  stages as we made changes and improvements. 
  </pre>
    
-----

## The Chess Server ##
- When you faced Clamps, what did the code you used do?  Was it just your jamboree?  Did you do something fancier?
  <pre>
  When we faced clamps we used an improved version of jamboree. We added move ordering to jamboree and did iterative
  deepening to improve the speed of the evaluations. We also changed the chess engine parameters to depth 6 and 2
  for the cutoff. 
  </pre>

- Did you enjoy watching your bot play on the server?  Is your bot better at chess than you are?
  <pre>
  I enjoyed watching my bot play on the server. Unfortunately the bot is not better at chess than me. I used to play
  competitive chess and was rated around 1800. I would have been really happy to make a bot that was stronger than me.
  I think my bot needed additional features such as opening books and improved heuristics to be stronger. Nevertheless
  I was still satisfied at the performance of my bot given the simple heuristics. 
  </pre>

- Did your bot compete with anyone else in the class?  Did you win?
  <pre>
  I did not have time to compete with anyone in the class but I wished I had played some games. I would have liked to
  see how well my bot did. 
  </pre>

- Did you do any Above and Beyond?  Describe exactly what you implemented.
  <pre>
  For above and beyond I implemented move ordering and iterative deepening. For move ordering I made a comparator
  and did collections.sort on my moves list to prioritize moves with a higher eval on the first move. This improved
  performance by improving the pruning, which chops off more branches. I also implemented iterative deepening. This
  allowed us to speed up evaluation. I looped calls to my jamboree task from 1 to ply, and on each loop, I received 
  the best move and put the move at the front of the moves list for the next iteration. This allowed for faster
  processing. I also implemented variable ply. For the first 10 moves ply was 5, then I kept increasing ply
  to 8 in the endgame. 
  </pre>

## Experiments ##

### Chess Game ###

#### Hypotheses ####
Suppose your bot goes 3-ply deep.  How many game tree nodes do you think
it explores (we're looking for an order of magnitude) if:
 - ...you're using minimax?
    <pre>
    I predict that my bot will visit 50,000 nodes 3 ply deep for minimax.
    </pre>
 - ...you're using alphabeta?
    <pre>
    I predict alphabeta will visit 5,000 nodes 3 ply deep. 
    </pre>

#### Results ####
Run an experiment to determine the actual answers for the above.  To run
the experiment, do the following:
1. Run SimpleSearcher against AlphaBetaSearcher and capture the board
   states (fens) during the game.  To do this, you'll want to use code
   similar to the code in the testing folder.
2. Now that you have a list of fens, you can run each bot on each of them
   sequentially.  You'll want to slightly edit your algorithm to record the
   number of nodes you visit along the way. 
3. Run the same experiment for 1, 2, 3, 4, and 5 ply. And with all four
   implementations (use ply/2 for the cut-off for the parallel
   implementations).  Make a pretty graph of your results (link to it from
   here) and fill in the table here as well:


|      Algorithm     | 1-ply | 2-ply | 3-ply | 4-ply | 5-ply |
| :----------------: |:-----:|:-----:|:-----:|:-----:|:-----:|
|       Minimax      |29.1563|933.625|31492.7|1061080|3.72E7 |
|  Parallel Minimax  |29.1563|933.625|31492.7|1061080|3.72E7 |
|      Alphabeta     |29.1563|372.187|6398.10|63343.1|721194 |
|      Jamboree      |29.1563|379.468|6735.92|67800.9|907371 |

[Node Count Data](https://gitlab.cs.washington.edu/cse332-17wi/p3-lassi/blob/master/src/chess/experiments/ex1/nodes.csv)

<img src="https://i.imgsafe.org/ff5be8ff18.jpg" width="550" height="300" />
<img src="https://i.imgsafe.org/ff5bf5835c.jpg" width="550" height="300" />
<img src="https://i.imgsafe.org/ff5c0111e6.jpg" width="550" height="300" />
<img src="https://i.imgsafe.org/ff5c1343d4.jpg" width="550" height="300" />
<img src="https://i.imgsafe.org/ff5c2acc99.jpg" width="550" height="300" />

#### Conclusions ####
How close were your estimates to the actual values?  Did you find any
entry in the table surprising?  Based ONLY on this table, do you feel
like there is a substantial difference between the four algorithms?
<pre>
We tested our base jamboree. The tables are averages of the visited nodes. 
I measured visited nodes for each fen, added up the total, and divided by the total number 
of fens to get the average. My estimates were fairly close to the actual values. 
I was surprised that jamboree actually did worse than alphabeta in terms of nodes visited. 
This is probably due to the fact that we are visiting more nodes through parallelism that would have
been pruned off by alpha beta. Although jamboree visits more nodes than alphabeta,
the runtime of jamboree is faster due to parallelism. There is a substantial difference 
between the minimax/parallel minimax and alphabeta/jamboree groups. Alphabeta/jamboree 
visits less nodes since those algorithms are using pruning to avoid going down branches.
According to the graphs game complexity is the highest in the middle of the game. Minimax
and parallel searcher suffer the most during the middle game as their peaks are the highest.
Parallel searcher and minimax had the same exact node counts since their algorithms are the same,
except parallelization is used, which improves speed but not the number of nodes visited. Alpha beta
and jamboree have a similar node count, but are different since some parallel children in jamboree 
evaluate unnecessary parts of the tree. This happens because when we parallelize the loop, we cannot 
communicate alpha and beta values between sibling threads. 
</pre>

### Optimizing Experiments ###
THE EXPERIMENTS IN THIS SECTION WILL TAKE A LONG TIME TO RUN. 
To make this better, you should use Google Compute Engine:
* Run multiple experiments at the same time, but **NOT ON THE SAME MACHINE**.
* Google Compute Engine lets you spin up as many instances as you want.

#### Generating A Sample Of Games ####
Because chess games are very different at the beginning, middle,
and end, you should choose the starting board, a board around the middle
of a game, and a board about 5 moves from the end of the game.  The exact boards
you choose don't matter (although, you shouldn't choose a board already in
checkmate), but they should be different.

#### Sequential Cut-Offs ####
Experimentally determine the best sequential cut-off for both of your
parallel searchers.  You should test this at depth 5.  If you want it
to go more quickly, now is a good time to figure out Google Compute
Engine.   Plot your results and discuss which cut-offs work the best on each of
your three boards.

[Cutoff Data](https://gitlab.cs.washington.edu/cse332-17wi/p3-lassi/blob/master/src/chess/experiments/ex2/SeqCutoff.csv)

<img src="https://i.imgsafe.org/1f530005fb.jpg" width="550" height="300" />
<img src="https://i.imgsafe.org/1f5295c576.jpg" width="550" height="300" />
<img src="https://i.imgsafe.org/1f5d02e395.jpg" width="550" height="300" />

<pre>
We tested on our optimized jamboree. According to the data, for all three board positions, jamboree performed
the best with a cutoff of 2. Parallel minimax performed the best with a cutoff of around 3. Parallel minimax
requires a higher cutoff than jamboree to perform optimally since parallel minimax is not doing any pruning
so will not be able to evaluate as efficiently as jamboree. Changing the cutoffs also seems to impact
parallel minimax more than jamboree as well since making too many threads at 1 cutoff and entering the 
base case too early at 4 cutoff can adversely affect the performance of minimax, whereas the pruning jamboree
does lessens the impact of these affects. 
</pre>

#### Number Of Processors ####
Now that you have found an optimal cut-off, you should find the optimal
number of processors. You MUST use Google Compute Engine for this
experiment. For the same three boards that you used in the previous 
experiment, at the same depth 5, using your optimal cut-offs, test your
algorithm on a varying number of processors.  You shouldn't need to test all 32
options; instead, do a binary search to find the best number. You can tell the 
ForkJoin framework to only use k processors by giving an argument when
constructing the pool, e.g.,
```java
ForkJoinPool POOL = new ForkJoinPool(k);
```
Plot your results and discuss which number of processors works the best on each
of the three boards.

[Processor Data](https://gitlab.cs.washington.edu/cse332-17wi/p3-lassi/blob/master/src/chess/experiments/ex3/Processors.csv)

<img src="https://i.imgsafe.org/27627d0dd2.jpg" width="550" height="300" />
<img src="https://i.imgsafe.org/2761f4bcc1.jpg" width="550" height="300" />
<img src="https://i.imgsafe.org/276575ffb6.jpg" width="550" height="300" />
<img src="https://i.imgsafe.org/2764961ee2.jpg" width="550" height="300" />
<img src="https://i.imgsafe.org/2766753267.jpg" width="550" height="300" />
<img src="https://i.imgsafe.org/2767750a38.jpg" width="550" height="300" />

<pre>
We used optimized jamboree. According to the data the minimum number of processors that results in the best
runtime is 10 processors for both algorithms, at early, middle, and end game states. Each of the curves
decrease runtimes until about 10 cores, after which the runtime is fairly constant. Originally
we tried binary search but this resulted in missing data points; we felt like sequentially displaying
all the data from 1-32 cores would display the trend better. From the graphs, the runtimes converge
after 10 cores. Jamboree runtimes were consistently better than that of Parallel Minimax's. Between
board states runtimes were highest in the middle game and shortest in the beginning/end, where
game complexity was the lowest. Runtime decreases before 10 cores because increasing the number
of cores allows for more parallelization, which speeds up the evaluations. However, we get diminishing
returns and stop decreasing around 10 cores since parallelization can only do so much; we are still
restricted by the processing speed, and overhead from forking all the tasks may limit decreases in runtime. 
Thus, if we want to limit costs but have the best runtimes, we should utilize 10 cores for running our 
parallel minimax and optimized jamboree algorithms for chess. We performed 100 trials for each data point and
averaged the values. 
</pre>

#### Comparing The Algorithms ####
Now that you have found an optimal cut-off and an optimal number of processors, 
you should compare the actual run times of your four implementations. You MUST
use Google Compute Engine for this experiment (Remember: when calculating
runtimes using *timing*, the machine matters).  At depth 5, using your optimal 
cut-offs and the optimal number of processors, time all four of your algorithms
for each of the three boards.

[Algorithm Data](https://gitlab.cs.washington.edu/cse332-17wi/p3-lassi/blob/master/src/chess/experiments/ex4/Algos.csv)

|      Algorithm     | Early Game | Mid Game  | End Game |
| :----------------: |:----------:|:---------:|:--------:|
|       Minimax      |6423061937  |48638691955|6207065389|
|  Parallel Minimax  |514731630   |4525745096 |702505092 |
|      Alphabeta     |324591200   |829306166  |742902933 |
|      Jamboree      |71615381    |233257050  |66515109  |

<img src="https://i.imgsafe.org/1075e0e48d.jpg" width="550" height="300" />
<img src="https://i.imgsafe.org/107601730a.jpg" width="550" height="300" />
<img src="https://i.imgsafe.org/1075e9e641.jpg" width="550" height="300" />

<pre>
We ran the experiments on GCE using a cutoff of (2 for optimized jamboree, 3 for parallel), depth 5, and 10 
cores. We timed the results in nanoseconds. We did 20 rounds of JVM warmup on searching on jamboree, then we
did 100 trials at each board state for each searcher. The table values are the averaged 
runtimes for 100 trials. Across the early, middle, and endgame, jamboree consistently 
runs faster, due to pruning and our addition of move ordering. Minimax consistently took 
the longest to complete since this was the most rudimentary algorithm, and did a brute force 
search on the nodes. Minimax suffered the biggest increase in the middle game where the number 
of possible continuations/board complexity is higher. In the early and middle game, parallel 
minimax did worse than alphabeta. This shows that parallelization in those scenarios do not 
outweigh pruning moves. Although alphabeta was not parallelized, pruning moves increased performance
more than delegating all the work to children with parallelization. However, in the end game, parallel minimax
performed better than alphabeta. In the endgame, there are less pieces, so there are less branches and 
possibilities. Thus parallelization outweighs the benefits of pruning in the endgame since the number of
possibilities is already smaller, so parallelization speeds up the evaluation more. Pruning does help in
the endgame, but its affect is marginalized compared to pruning in more complicated positions where there
are more pieces. In addition, according to the graphs, our optimized jamboree seems to change the least between
game states. Since jamboree was optimized, changing game complexity does affect runtime, but does not result
in changes as large as those of minimax/parallel minimax/alphabeta. Jamboree prunes away so many branches
with ordering that game complexity has less of an affect on the runtime. 
</pre>

### Beating Traffic ###
In the last part of the project, you made a very small modification to your bot
to solve a new problem.  We'd like you to think a bit more about the 
formalization of the traffic problem as a graph in this question.  
- To use Minimax to solve this problem, we had to represent it as a game. In
  particular, the "states" of the game were "stretches of road" and the valid
  moves were choices of other adjacent "stretches of road".  The traffic and
  distance were factored in using the evaluation function.  If you wanted to use
  Dijkstra's Algorithm to solve this problem instead of Minimax, how would you
  formulate it as a graph?
  <pre>
  If we use Dijkstra's Algorithm, the nodes would be each intersection and the edges would be roads between
  each intersection. The edges would be weighted based on the traffic and speed limit. The more traffic there
  is and the lower the speed limit is, the more the edge would be weighted. 
  </pre>

- These two algorithms DO NOT optimize for the same thing.  (If they did,
  Dijkstra's is always faster; so, there would be no reason to ever use
  Minimax.)  Describe the difference in what each of the algorithms is
  optimizing for.  When will they output different paths?
  <pre>
  Djikstra's algorithm looks for the shortest weighted path from a single source to all other points, given
  edge weights. Minimax attempts to look for the shortest path between 2 points. Minimax does not necessarily
  find the shortest path if the depth is not set to the bottom level; for example, if there is a "hidden" dead
  end we cannot see with minimax because our depth cutoff is limiting. Djikstra's would report the correct
  shortest path but minimax would possibly end up at a dead end. One drawback of Djikstra's is that this
  algorithm cannot easily handle dynamic "states" of the roads. If an accident happened, minimax would be able
  to continue evaluating the state given the new information. However, since Djikstra computes the shortest
  path all the way to the destination, we would be forced to update all the edge values and recompute
  Djikstra's from the node when the update happened. Thus, Djikstra would either incorrectly report a
  suboptimal path or will be computationally more expensive updating every edge every time the "state"
  changes. Djikstra's algorithm optimizes for the shortest path taking into account the entire board state
  whereas minimax optimizes for the shortest path by evaluations at each step. One other drawback of
  Djikstra's is that we could report a sub-optimal path if negative edge weights are somehow assigned
  to the graph.
  </pre>