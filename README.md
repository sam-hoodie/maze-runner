# Maze Generator/Solver
This is a project that allows you to generate a random two dimensional maze with specified dimensions and solve it. It can also make a path for the solution to the maze.

To use the project, there are four things that must be specified: width, height (both including outside walls), start point, and end point.
### Recursion
The project is based around the concept of recursion, which is very useful in both maze solving and generation. To make a solution, the program explores all possible ways to go deeper into the maze, and when it reaches a dead end, it backtracks and tries the next possible option.
### Maze generation
To generate a maze, the same concept of recursion is used. For each step, all possibilities for a new piece extension of an existing wall. Once no more new walls can be added, a full maze has been generated. No new pieces of wall can stand alone in the maze, which ensures it does not have freestanding walls. 
### Code structure
This project also utilizes many helper functions that are built on top of one another. For example to get all new possible walls, a function is used that finds how far a wall can extend from a certain point. This function then uses another helper function that finds if a certain point on the maze can be a wall. I have done this to provide easier readability and navigability.

