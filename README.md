# marsrover
A solution to problem discussed at https://github.com/JavaDojo/marsrover 

MarsRover
=========

The Mars rover is programmed to drive around Mars.
Its programming is very simple. The commands are the following:

 * **s** drive in a straight line
 * **r** turn right
 * **l** turn left
 * **S** take a sample

Note that the Mars rover always land at the `X` and starts by facing east.

The Mars rover can send a 2D string representation of its path back to Mission Control. The following character are used with the following meanings:

 * **X** where the Mars rover landed
 * **\*** current position of the Mars rover
 * **-** path in the west-east direction
 * **|** path in the north-south direction
 * **+** a place where the Mars rover turned or a crossroad
 * **S** a place where a sample was taken

Your goal is to implement the MarsRover class to make all tests pass.
 


