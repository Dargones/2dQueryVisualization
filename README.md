# Rectangle queries over points

This project implements an algorithm for identifying points on a plane that
fall within a specific rectangle. I use the data structure we had to describe 
in homework 6 problem 1.b - a primary binary search tree with treaps hanging 
from each of its nodes. The project includes: (1) a graphical user interface
for dynamically visualizing the algorithm on small examples and (2) a command
line interface for running multiple queries over a large set of points.

The whole project is implemented in Java and the only library that I use is
apache.commons.cli, which allows parsing command-line-arguments. I implement
BSTs and treaps myself.

Contents:
1. Compiling the Code
2. Step by Step Algorithm Visualization
3. Command Line Interface
4. What I Learned from This

## 1. Compiling the Code

Unzip the `project.zip` file, enter the `project` directory and run 
`javac -cp src:lib/commons-cli-1.5.0.jar src/Main.java` 
from there to compile the code. 

## 2. Step by Step Algorithm Visualization

If you are running the code remotely, please use the `-X` flag with `ssh` so 
that you can see the output.

You can access the GUI for visualizing every step of the search algorithm by 
running the program with the `-gui` command line option. 
By default, Java creates a window of 1800 by 1000 pixels. 
If you need different resolution, use `-resolution` command line option. 
Important: `-file` and `-gui` options are incompatible since the GUI only 
supports relatively small examples. You might invoke the GUI like so: 

`java -cp src:lib/commons-cli-1.5.0.jar Main -gui -resolution=1800x1000`

When the GUI opens, you will see that the window is divided in four sections.
The bottom-left section contains a plane and points that you query over. 
The points can be gray, blue, yellow, and pink - more on that below. The
top section displays a binary search tree (by x coordinate) constructed from
these points. The middle section displays one of the treaps associated with
some node in the BST - you can select which particular treap you want to be
displayed in the bottom-right section of the screen, which contains all the
control options. 

You can add or remove points by selecting "Add Point" or "Remove Point" in
the control panel and then clicking on the plane wherever you would like to
add or remove a point. Click "Clear points" to remove all points. 
You can also specify a query over the points by selecting "Set Query" in the 
control panel and then clicking in two places on the plane, which will 
define the query rectangle (the two corners of the rectangle are marked by 
hollow red squares).

Once you specify the query, the points inside the query rectangle will be
colored blue and all other points that the algorithm considers during its 
execution will be colored yellow or pink. The corresponding nodes in the BST
and the treap being displayed will also be colored accordingly. Yellow points
correspond to points stored in those nodes of the BST or some treap that are
visited by the algorithm. Pink points correspond to median values that the 
algorithm compares against during some treap traversal.

You can use "Prev Step", "Next Step", "First Step", and "Last Step" buttons to
trace the algorithm's execution. As you advance the algorithm one step at a
time, the points on the plane and the nodes in the BST and the treaps are
colored accordingly. Whenever the algorithm begins traversing a treap, that 
treap will be displayed in the middle of the screen.

Click "Randomize" to quickly add 63 points to the plane (can be less if the
random value generator tries adding the same point several times) and define
a random query over them.

While it may look like the BST and the treaps are updated dynamically as you
add new points, they are actually being rebuilt at each change as I have
focused on visualizing the base version of the algorithm for this project.

## 3. Command Line Interface

To test the algorithm on larger examples, use the command line interface by
invoking the program with the `-file` options like so: 

`java -cp src:lib/commons-cli-1.5.0.jar Main -file=examples/simple.txt`

The file specified should be in this format: the first line contains all the
points that are being queried over, each of the successive lines contains two
points that define a query rectangle. On each line, the points are separated 
by spaces and each point is specified by its x coordinate, followed by comma,
followed by y coordinate. The following, for instance, is a valid input file:

```
3,4 5,6 3,6 6,7 0,7 8,0 1,5 5,5 5,4 9,2
0,0 10,10
5,5 5,5
2,6 4,8
2,2 6,6
```

The program will print the result of each query, one query per line. Note that
the order of points in the result is random due to the use of hashsets. Below
is a valid output for the set of queries above:

```
5,5 6,7 5,6 3,4 3,6 1,5 0,7 8,0 9,2 5,4
5,5
3,6
5,5 5,6 3,4 3,6 5,4
```

You can verify that the program indeed returns this output by running it
on `examples/simple.txt`

## 4. What I Learned from This

In my homework, I argued that each node must be associated with 2 treaps, but,
in fact, it is enough to have one treap per subtree, i.e. one treap per
every node in the BST. Depending on whether a given subtree is a right or left
child, the associated node would have a treap open to the right or left, 
respectively.

It wasn't my goal to make the trees dynamic for this project but I did sketch 
how the dynamic implementation would look like and it appears that a
single insert operation would take `log^2(n)` time because one would affect
`log(n)` nodes while inserting the new point in the BST and each node also
has a treap associated with it. Adding/Removing a constant number of points
to a treap should take `log(n)` time per treap (rebalancing of nodes might
also cause the nodes to exchange treaps associated with them).