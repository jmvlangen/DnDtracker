D&D tracker:
----------------------------------------------------------------------------------------

The D&D tracker functions as an advanced calculator that allows you to store, manage
and calculate things that you need during a D&D campaign or similar game. The program
can be used in combination with already existing setups or can be used to `program'
your own stuff. This readme contains a quick explanation of the basic usage of the
program. For more details about specific existing setups for the program, read the
readme files corresponding to the related .dat and .scr files.

--1) Values-----------------------------------------------------------------------------
Everything that you enter into the program or is stored inside the program is considered
to be a value. A value can be everything from an integer to a complicated calculation.
The important property of a value is that it can always be evaluated to a simpler value,
called a primitive value. For calculations this simply means that the result of the
calculation is calculated. For example, if we evaluate the value:
	5 + 3
the result will be the value
	8
This is the most basic way to evaluate a value, but there are also values which have a
more complicated behavior when evaluated.

Below we list the most basic types of values and their behavior when evaluated

---a) Primitive Values------------------------------------------------------------------
The simplest types of values are the primitive values. The evaluation of any value will
always result in a primitive value and every primitive value will always evaluate to
itself (with one exception explained in ...). Here are the primitive values you will
encounter most often

----i) Void Value-----------------------------------------------------------------------
A void value basically represents nothing. A void value is represented by
	()
Any basic operation performed on a void value will result in the other value present in
the operation.

----ii) Integers------------------------------------------------------------------------
Integers are numbers as we know them. They are denoted by their decimal representation.
Integers inside the program take values between -2^63 and 2^63 - 1 (both inclusive).
Basic operations such as addition, multiplication and subtraction result in the
respective integer operation. Note that the program will present and error if the result
of such an operation is not an integer in the afore mentioned range. Division of an
integer 'x' by an integer 'y' will result in the largest integer 'z' such that 'y' times
'z' is at most 'x'.

----iii) Strings------------------------------------------------------------------------
A string is a value that represents a text. Strings will be represented by the
characters inside the string (in the right order) preceded and succeeded by the symbol "
Furthermore certain characters in the string will be replaced by a different combination
of two characters as follows
	" will be represented by \"
	\ will be represented by \\
	the newline characters will be represented by \n
Note that the symbol \ succeeded by another symbol in the representation will always
result in the succeeding symbol in the string if it is not one of the cases described
above. Hence the only way to get \ in the string is to have \\ in the representation.

The only operation that works on strings is addition, which just concatenates strings.

---b) Basic Operations------------------------------------------------------------------
Besides primitive values there are certain operations which combine multiple values
into a more complicated value. Here follows a list of possible operations.

----i) Addition-------------------------------------------------------------------------
Addition represents the sum of two values, hereafter called the terms of the sum. An
addition is represented by the symbol + preceded and succeeded by a value, which are
the first and second term respectively. When evaluated the addition will evaluate both
terms first and then try to add the evaluation of the second term to the evaluation of
the first. An error will be presented when this is impossible.

----ii) Subtraction---------------------------------------------------------------------
Subtraction represents one value, hereafter called b, subtracted from another value,
hereafter called a. A subtraction is represented by the symbol - preceded and succeeded
by a value, which are a and b respectively. When evaluated the subtractions will first
evaluate both a and b and then try to subtract the evaluation of b from the evaluation
of a. An error will be presented when this is impossible.

----iii) Multiplication-----------------------------------------------------------------
Multiplication represents the product of two values, hereafter called the factors of the
multiplication. A multiplication is represented by the symbol * preceded and succeeded
by a value, which are the first and second factor respectively. When evaluated the
multiplication will first evaluate both factors and then try to multiply the evaluation
of the first factor by the evaluation of the second. An error will be presented when
this is impossible.

----iv) Division------------------------------------------------------------------------
Division represents one value, hereafter named a, divided by another value, hereafter
called b. A division is represented by the symbol / preceded and succeeded by a value,
which are the first and second factor respecitively. When evaluated the division will
first evaluate a and b and then try to divide the evaluation of a by the evaluation
of b. An error will be presented when this is impossible.

----v) Dice-----------------------------------------------------------------------------
A dice value represents the rolling of one or several dice. A dice value is represented
by one of the following sequences
	a D b
	a D b L c
	a D b H c
In these sequences a, b and c should be values, whilst D, L and H precisely mean these
symbols. (They may also be the lower case version.) When evaluated a dice value will
first evaluate a and b (and c if given), of which we shall denote their evaluations by
ea and eb (and ec) respectively. Next it will check whether ea and eb are positive
integers (and if ec is a positive integer at most equal to eb). If not it will present
an error. If so it will roll ea dice of size eb. The result of these dice rolls will be
presented to the user. Next it will order the results from lowest to highest. If c was
specified it will keep the highest (respectively lowest) ec of these results if c was
preceeded by the symbol H (or L respectively). If c was not specified it will keep all
results. Lastly, the result of the evaluation will be the sum of all the results that
were kept.

For example the evaluation of
	2 D 8
will be the sum of the results of two eight sided dice, whilst
	4 D 6 H 3
will be the sum of the three highest results of four dice rolls of a six sided dice.

----vi) Grouping values-----------------------------------------------------------------
For many purposes it can be usefull to group values. For example in the line
	2 + 3 * 5
one might want the addition specified by + to be the sum of
	2
and
	3 * 5
but one could also mean it to be the sum of
	2
and
	3
For correct execution the user should therefore specify which parts should be
interpreted as one value. This can easily be done by using brackets ( and ). Anything
preceeded by the symbol ( and succeeded by the symbol ) will be seen as one value by
everything not within the brackets, hence
	2 + (3 * 5)
will give the first interpretation of the example, whilst
	(2 + 3) * 5
will give the second interpretation of the example. Note that the program will
automatically surround each of the afore mentioned operations by brackets when printing
the value, to avoid confusion.

However, when using the above mentioned operations one can also leave the brackets out.
If no brackets are entered, the program will use the default grouping. In this default
grouping:
	Each operation i) and ii) will group expressions with iii), iv) and v) as one value.
	Each operation iii) and iv) will group expressions with v) as one value.
Hence for our example
	2 + 3 * 5
will always be interpreted as
	2 + (3 * 5)
if no brackets are given.

--2) User Interface---------------------------------------------------------------------
When starting the program the user is presented by the user interface. The user
interface can be used to enter values and to see the result of certain calculations.
The user interface consists of two parts.

---a) Output window---------------------------------------------------------------------
At the top is a window where all output will be printed. This includes all information
that the program should relate to the user, such as the result of evaluations and
error messages.

---b) Input line------------------------------------------------------------------------
At the bottom is a small line where the user can enter input. The input should consist
of anything that represents a value. When the user presses enter, whatever has been
written in the input line will be interpreted as a value and then evaluated. The result
of the evaluation will be printed on the output window, unless it is a void value.

--3) Variables--------------------------------------------------------------------------
The way to store information inside the program is by the use of variables. A variable
is nothing else then a name to which we associate a value. In this section the basic
usage of variables will be explained

---a) Names-----------------------------------------------------------------------------
As mentioned every variable is defined by a name. A name of a variable may consist of
alphanumerical characters, but must start with a letter. Alternatively one can start a
variable with the symbol _ which indicates a hidden variable (see section ... for more
explanation of what this means).

Names of the type defined above can also be used to represent values. If used in such
a way the value of the name will be a reference to a variable with the corresponding
name. When such a value is evaluated it will thus try to find a variable with the given
name and evaluate the value stored in that variable. If a variable with that name can
not be found an error will be presented.

...