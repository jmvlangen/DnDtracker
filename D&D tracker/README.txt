D&D tracker:
----------------------------------------------------------------------------------------

The D&D tracker functions as an advanced calculator that allows you to store, manage
and calculate things that you need during a D&D campaign or similar game. The program
can be used in combination with already existing setups or can be used to 'program'
your own stuff. This readme contains a quick explanation of the basic usage of the
program. For more details about specific existing setups for the program, read the
readme files corresponding to the related .dat and .scr files.

--1) Values-----------------------------------------------------------------------------
Everything that you enter into the program or is stored inside the program is considered
to be a value. A value can be everything from an integer to a complicated calculation.
The important property of a value is that it can always be evaluated to a simpler value
called a primitive value. For calculations this simply means that the result of the
calculation is calculated. For example, if we evaluate the value:
	5 + 3
the result will be the value
	8
This is the most basic way to evaluate a value, but there are also values which have
more complicated behavior when evaluated.

Below we list the most basic types of values and their behavior when evaluated.

---a) Primitive Values------------------------------------------------------------------
The simplest type of value is a primitive value. The evaluation of any value will always
result in a primitive value and every primitive value will always evaluate to itself
(with one exception explained in section 7). Here are the primitive values you will
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

----iii) Texts--------------------------------------------------------------------------
A text is a value that represents a text. Texts will be represented by the characters
inside the text (in the right order) preceded and succeeded by the symbol " Furthermore
certain characters in the text will be replaced by a different combination of two
characters as follows
	" will be represented by \"
	\ will be represented by \\
	the newline characters will be represented by \n
Note that the symbol \ succeeded by another symbol in the representation will always
result in the succeeding symbol in the text if it is not one of the cases described
above. Hence the only way to get \ in the text is to have \\ in the representation.

The only operation that works on texts is addition, which just concatenates texts.

----iv) Booleans------------------------------------------------------------------------
A boolean is a value that can be either true or false. Since there are only two possible
boolean values - true and false - they both have their own representation. The boolean
value true is represented by
	$TRUE
and the boolean value false is represented by
	$FALSE

The operations defined on boolean values are: addition which works like the logical OR,
subtraction which works like the logical OR in which the second value is inverted,
multiplication which works like the logical AND, and division which works like the
logical AND in which the second argument is inverted.

---b) Basic Operations------------------------------------------------------------------
Besides primitive values there are certain operations which combine multiple values
into another value. Here follows a list of possible operations.

----i) Equality-------------------------------------------------------------------------
Equality compares two values to see whether they are the same. An equality is
represented by the symbol = preceded and succeeded by a value. When evaluated an
equality will evaluate the value in front and the value thereafter and check whether
or not their evaluations are the same. If they are the same the equality will evaluate
to the boolean value true, whilst it will evaluate to the boolean value false otherwise.

----ii) Addition------------------------------------------------------------------------
Addition represents the sum of two values, hereafter called the terms of the sum. An
addition is represented by the symbol + preceded and succeeded by a value, which are
the first and second term respectively. When evaluated the addition will evaluate both
terms first and then try to add the evaluation of the second term to the evaluation of
the first. An error will be presented when this is impossible.

----iii) Subtraction--------------------------------------------------------------------
Subtraction represents one value, hereafter called b, subtracted from another value,
hereafter called a. A subtraction is represented by the symbol - preceded and succeeded
by a value, which are a and b respectively. When evaluated the subtractions will first
evaluate both a and b and then try to subtract the evaluation of b from the evaluation
of a. An error will be presented when this is impossible.

----iv) Multiplication------------------------------------------------------------------
Multiplication represents the product of two values, hereafter called the factors of the
multiplication. A multiplication is represented by the symbol * preceded and succeeded
by a value, which are the first and second factor respectively. When evaluated the
multiplication will first evaluate both factors and then try to multiply the evaluation
of the first factor by the evaluation of the second. An error will be presented when
this is impossible.

----v) Division-------------------------------------------------------------------------
Division represents one value, hereafter named a, divided by another value, hereafter
called b. A division is represented by the symbol / preceded and succeeded by a value,
which are the first and second factor respecitively. When evaluated the division will
first evaluate a and b and then try to divide the evaluation of a by the evaluation
of b. An error will be presented when this is impossible.

----vi) Dice----------------------------------------------------------------------------
A dice value represents the rolling of one or several dice. A dice value is represented
by one of the following sequences
	a D b
	a D b L c
	a D b H c
In these sequences a, b and c should be values, whilst D, L and H precisely mean these
symbols. (They may also be the lower case letters.) When evaluated a dice value will
first evaluate a and b (and c if given), of which we shall denote their evaluations by
ea and eb (and ec) respectively. Next it will check whether ea and eb are positive
integers (and if ec is a positive integer at most equal to eb). If not, it will present
an error. If so, it will roll ea dice of size eb. The result of these dice rolls will be
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

----vii) Grouping values----------------------------------------------------------------
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
	Each operation i) will group expression with ii), iii), iv), v) and vi) as one.
	Each operation ii) and iii) will group expressions with iv), v) and vi) as one.
	Each operation iv) and v) will group expressions with vi) as one value.
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
variable with the symbol _ which indicates a hidden variable. Hidden variables are often
ignored when displaying collections and are used for more advanced behavior.

Names of the type defined above can also be used to represent values. If used in such
a way the value of the name will be a reference to a variable with the corresponding
name. When such a value is evaluated it will thus try to find a variable with the given
name and evaluate the value stored in that variable. If a variable with that name can
not be found an error will be presented.

---b) Collections-----------------------------------------------------------------------
A collection is a value that can store variables. A collection is represented by a
comma seperated list enclosed in curly brackets (the symbols { and } ). Each entry in
this list consists of a valid name, the symbol = and then a value. An example of such a
collection would be
	{ a = 2 , b = "text"}

A collection is in fact a primitive type and will thus result in itself when evaluated.
The operators defined in section 1.b) will present the user with errors if they are
evaluated on collections. Special behavior for these operators and evaluation can
however be defined. Section 7 will describe this in detail.

---c) Nested variables------------------------------------------------------------------
In order to access variables within a collection one can use the symbol . . If Col is
any expression that represents a collection, evaluates to a collection or is the name
of a variable with as value a collection, then adding the symbol . followed by a name
will represent the value of a variable with that name in the before mentioned
collection. Hence
	Col.name
will refer to the value of the variable name inside the collection reffered to by Col.

Examples of this usage would include
	{ a = 3 }.a
	x.y
where x is the name of a variable with as value a collection, or
	({b = "hello world"} + ()).b
as the part before the dot will evaluate to the collection
	{b = "hello world"}
	
One could also combine multiple usages of the symbol . . For example
	a.b.c
will acces the value of the variable named c inside the collection stored in the
variable named b inside the collection stored under the name a. One could think of this
as the variable named c being two levels below the variable named a. One could therefore
interpret the symbol . as going down one level.

Besides going down a level, one could also go up one or multiple levels by adding
additional symbols . . For example
	{a = {}, b = 3}.a..b
will evaluate to
	3
In this case
	a..b
means the program will look for the variable named b in the collection that contains
the variable named a, hence one level above the variable named a. Multiple uses of the
symbol . would go up more levels. For example
	a....c
will look for a variable named c in a collection three levels above the variable named
a. In general n+1 uses of the symbol . in a row will result in looking for a variable
at level n above whatever is in front of the first symbol . . This generalizes to the
case where n is 0, in which case it will look for a variable inside the collection
referred to before the first symbol . , hence at level 0.

There are some more remarks to make about levels. First of all there is always a top
level collection and going up levels from there will always end again in this top level
collection. Furthermore, when requesting a non-hidden variable from a collection that
does not contain it, it will request the same variable of the level above. This allows
non-hidden variables inside the top level to be accessed by all levels below it, unless
a variable with the same name is defined at such a level.

Besides user created collections, the program also stores a special collection. This
collection will be referred to as the global collection and is represented by the
symbol : . Variables in this collection could be accessed using the method described
above, but when accesing variables in the global collection one can leave out the
symbol . . For example
	:.b
accesses the variable named b in the global collection, but
	:b
does as well. Note that variables will only be stored when they are at some level below
the global collection, but that these stored variables will only be stored as long as
the program is running. To save and load variables between sessions look at section 5d).

Besides the global collection the program also keeps track of a current collection,
which is called the environment. By default this environment is set to be the global
collection, but it can be changed (see section 5ci). When names of variables are typed
without the symbol . in front, the program looks for these variables inside this
collection. One can also write the symbol . without any value in front, in which case
it will link to variables relative to the environment. For example
	..b
will look for a variable named b in the collection above the environment.

A last remark about the symbol . is that it can also be followed by a value surrounded
by brackets, i.e. the symbols ( and ) . In this case this value will be calculated
inside the level the symbols . refer to. For example
	..( a + b )
will evaluate
	a + b
inside the collection above the environment, meaning that when the addition evaluates
the variables named a and b it will look for these variables in that collection. The
same will happen when evaluating any value inside a variable. For example
	..c
where the variable named c in the collection above the environment has value
	a + b
will evaluate in the same way as the first example.

---d) Paths-----------------------------------------------------------------------------
Throughout the following sections we will talk about certain values, which we shall
call paths. Paths are values consisting of only names of variables seperated by
(multiple) symbols . . A path may also start with the symbol : if so required. For all
practical purposes a path will always refer to some variable on some level below the
global collection.

--4) Arguments--------------------------------------------------------------------------
When evaluating values it is possible to pass it other values as arguments. The main
application of this is given by system commands (see section 5). A value to be evaluated
with arguments is represented by the value followed by a comma seperated sequence of
values surrounded by brackets, i.e. the symbols ( and ) . For example, when
	coolStuff( a , 3 , "Just do it")
is evaluated, it will evaluate the value coolStuff with arguments
	a
	3
and
	"Just do it"
	
Note that arguments apply only to the value directly attached to the opening bracket ( ,
but are passed along during evaluation. For example
	a + b(3)
will only evaluate the variable named b with the argument 3, whilst
	(a + b)(3)
will evaluate the addition with the argument 3 and thus also evaluate a and b with the
same argument. Note that adding seperate arguments to a value will ignore arguments
passed along during evaluation, so
	(a + b(4))(3)
will evaluate the variable named b with the argument 4 and not with the argument 3.

---a) Argument values-------------------------------------------------------------------
A special type of value exists that is closely linked to arguments, called the argument
value. An argument value is represented by the symbol # followed by a (strictly)
positive integer value, which we will call n. When evaluated an argument value will
evaluate to the evaluation of the n'th argument given in evaluation. When this argument
does not exist it will evaluate to a void value.

--5) System Commands--------------------------------------------------------------------
Inside the global collection there are some special variables known as system commands.
These variables can not be modified in any way and store specific values that perform
certain special actions when evaluated. This section explains what these system commands
do. In each subsection we will name the command after the name of the corresponding
variable inside the global collection. Note that each command will evaluate to a void
value unless otherwise mentioned.

---a) Variable manipulation commands----------------------------------------------------

----i) Create---------------------------------------------------------------------------
The create command creates a variable or path of variables. When evaluating the create
command needs at least one argument, which should be a path (see section 3d). When
evaluated the create command will attempt to create all named variables in this path at
their appropiate level (see section 3c). When a second argument is given, the value of
the last named variable in the path will be set to the value given as a second argument.

----ii) Set-----------------------------------------------------------------------------
The set command can be used to set the value of a variable. The command requires two
arguments. The first should be a path (see section 3d), which refers to a variable
(which may not yet exist). The second should be a value which should become the value
of the afore mentioned variable. When evaluated the set command will create the given
path (like the create command) and set the value of the last variable in the path to
the second argument. When the optional third argument is set to something that evaluates
to the boolean value false or a void value, the set command will not create variables if
they do not exist.

----iii) Remove-------------------------------------------------------------------------
The remove command removes a variable. The remove command requires a single argument
that should be a path. The variable linked to the last name in this path will be removed
when the remove command is evaluated.

----iv) Copy----------------------------------------------------------------------------
The copy command copies the value of a variable to another variable. The copy command
requires two arguments which both should be paths. When the copy command is evaluated,
the value of the variable at the end of the second path will be set to the value of the
variable at the end of the first path. Variables in the second path will be created if
they do not already exist (like the create command). The second argument can also be
left out, in which case the value of the variable at the end of the first path is copied
to a variable with the same name in the environment. When the optional third argument is
set to something that evaluates to the boolean value false or a void value, the copy
command will not create variables if they do not exist.

----v) Move-----------------------------------------------------------------------------
The move command moves a variable to another location. The move command accepts two
arguments which should be paths, but requires at least the first. Upon evaluation, if
only the first path is specified, the variable at the end of the path will be moved to
the current environment, i.e. it will be copied and then removed in the original
location. If a second path is given only the value of the variable at the end of the
first path will be moved such that it becomes the value of the variable at the end of
the second path. Note that the original variable (at the end of the first path) will
also be removed in this case. When the optional third argument is set to something that
evaluates to the boolean value false or a void value, the set command will not create
variables if they do not exist.

---b) Screen commands-------------------------------------------------------------------

----i) Print----------------------------------------------------------------------------
The print command prints values to the screen. The print command accepts any amount
of arguments. When evaluated the print command will print each argument on the output
screen. If an argument is a text value it will print the text stored in the value,
hence without the symbol " and with all occurences of the symbol \ replaced according
to the rules mentioned in section 1aiii).

----ii) Clear---------------------------------------------------------------------------
The clear command removes all output from the output screen when evaluated.

---c) Location commands-----------------------------------------------------------------

----i) Environment----------------------------------------------------------------------
The environment command changes or displays the environment (see section 3c). If no
argument is given the environment command will display the current environment when
evaluated. If an argument is given it should be a path. When evaluated the environment
command will then set the environment to the value of the variable at the end of the
given path.

----ii) List----------------------------------------------------------------------------
The list command prints a list of all variables in a collection. When the list command
is evaluated it will print a list of all non-hidden variables in the environment and
their values. In this case values which are collections will be denoted simply by
	{...}
or their value if specified (see section 7a). If an argument is given that is a path,
then the list command will instead print the value of the variable at the end of the
given path. If this value is a collection it will do the same as with the environment,
but with that collection instead.

If a second argument is given and does not evaluate to the boolean value false or a
void value, then the list command will also display the contents of all collections it
encounters whilst printing values. Note that these values will be indented according to
the level they are below the collection list originally tried to list.

If a third argument is provided to the list command and it evaluates to anything but
a void value or the boolean value false, the list command will also display all the
hidden values stored inside collections it lists.

---d) File usage------------------------------------------------------------------------
When using files the program has default extensions it uses. These will be added to any
filename when no extension is mentioned. The default extensions are ".dat" for files
that store values and ".scr" for script files.

----i) Save-----------------------------------------------------------------------------
The save command allows you to store values in files. The save command requires at
least one argument to work, which should evaluate to a text value. This text value
should contain the (relative) location of a file on disk to which you want to save
(which does not have to exist yet). When evaluated the save command will attempt to
save the environment to this file. If a second argument is given, which should be path,
the save command will save the value of the variable at the end of the path instead.

----ii) Load----------------------------------------------------------------------------
The load command allows you to load values from files. The load command requires two
arguments to work. The first argument should evaluate to a text value which contains
the (relative) location of a file on disk to load from. The second argument should be
a path. When evaluated the load command will read the value from the file defined by
the first argument and assign it to the variable at the end of the path that is the
second argument. If some variables in this path do not exist, the load command will
create them like the create command.

The load command can also be used to load text files. Text files will be stored as
collections of the lines in the text file.

----iii) Run----------------------------------------------------------------------------
The run command allows you to run scripts. The text command requires one argument which
should evaluate to a text value which contains the (relative) location of a file on
disk. When evaluating the run command, it will open this file and execute every line in
the file as if it was entered on the input line. Files that can be used in such a way
are called script files. The default extension for such files is .scr . At startup the
program will always attempt to run a file named startup.scr . This could be used to
load commands that you want to load at the start.

---e) Miscellaneous commands------------------------------------------------------------

----i) Quit-----------------------------------------------------------------------------
The quit command exits the program when evaluated.

----ii) Text-----------------------------------------------------------------------------
The text command will turn values into text that describes them. The text command
requires one argument that can be any value. When evaluated the text command will
return a text value that contains a textual representation of the given value.

----iii) Void---------------------------------------------------------------------------
The void command will simply evaluate its arguments and 'void' the result. When
evaluated the void command will evaluated all its arguments, but disregard the values
they evaluate to.

---f) Programming commands--------------------------------------------------------------

----i) If-------------------------------------------------------------------------------
The if command evaluates a value only if a given value evaluates to true. The if
command needs at least two arguments, but one can give three. When the if command
is evaluated it will first evaluate the first argument. If this argument evaluates to
anything but a void value or the boolean value false, the second argument will be
evaluated and the third ignored if given. If the first argument does evaluate to a void
value or a boolean value false, the second argument will be ignored and the third
argument will be evaluated if given. Note that in all cases the if command always
evaluates to a void value.

----ii) Sort----------------------------------------------------------------------------
The sort command sorts a collection by a given path. The first argument to the sort
command must be or evaluate to a collection. The optional second argument must be a
path (see section 3d). When evaluated with at least one argument, the sort command
attempts to sort the non-hidden variables (see section 3a) in the collection by
comparing their values. The end result will be hidden variables named _n where n is an
integer ranging from 1 up to the number of non-hidden variables in the collection, where
each one contains as a value the name of a non-hidden variable. The names are ordered
in such a way such that the variable referred to by _1 has the smallest value and the
variable referred to by _n for the highest n has the highest value.

If the optional second argument was specified, the sorting will happen by looking at
the values at the end of the path to each variable extended by the path given as the
second argument. For example, if we sort the collection C with variables a, b and c in
it using the command
	sort C by.val
then the sort command will compare the evaluations of C.a.by.val, C.b.by.val and
C.c.by.val and sort a, b and c according to these values as mentioned before. Note that
if one or more of the variables in these longer paths do not exist, they will be
assumed to be void values, which can lead to unpredictable sorting behavior.

----iii) While--------------------------------------------------------------------------
The while command evaluates a value as long as another value evaluates to true. The
while command requires two arguments. The first argument will be called the condition,
whilst the second argument will be called the code. When the while command is evaluated
it will first evaluate the condition. If the condition evaluates to anything but a 
void value or the boolean value false, the while command will then evaluate the code.
This proces will repeat until the condition evaluates to a void value or the boolean
value false.

As an example the following input will raise the value of a by one at a time until it
is equal to 3
	set a 0
	while (i < 3) set(i,[[i+1]])

Note that to prevend endless loops, there is a cap on the maximum amount of evaluations
that the while command can do.

----iv) For-----------------------------------------------------------------------------
The for command evaluates a value for every variable in a collection. The for command
requires at least two arguments. The first argument should evaluate to a collection.
When the for command is evaluated, the for command will evaluate the second argument
once for each variable in the collection given by the first argument. In each evaluation
of the second argument a path to the respective variable is passed along as the first
argument.

Note that the for command does not consider hidden variable, unless the optional third
argument is given and evaluates to anything but a void value or the boolean value false.

--6) Special operations-----------------------------------------------------------------
Besides the standard operations described in section 1b) there are some operations that
can be used for advanced usage. These operations are described in this section.

---a) Auto evaluate---------------------------------------------------------------------
For certain applications you might want to first evaluate a value before passing it as
an argument to another evaluation. This is possible by surrounding the value with
square brackets, i.e. the symbols [ and ] . Any value between the symbol [ and a
matching next symbol ] will first be evaluated and then be regarded as the value that
results from this evaluation.

As an example you might want to print the outcome of a dice roll. If you write
	print(1D6)
the output on the screen would be
	1D6
as the program just prints that value. However when you write
	print([1D6])
the argument passed to the print command will be an evaluation of
	1D6
and the output on screen might for example be
	3

---b) Values defined by primitive values------------------------------------------------
There could be possible situations in which you don't want to write down a fixed value,
but rather a value defined by the evaluation of some other value. The program has a way
to convert certain primitive values into values. This can be done by surrounding a value
by the symbols < and > .

To explain how this works, let A be the representation of a value. Now if the program
evaluates
	< A >
it will first evaluate A to some primitive value B . Next it will attempt to interpret
B itself as a value. If B is a non-negative integer value the program will regard it as
the variable with as name the symbol _ followed by a representation of the integer. If
B is a text value, the program will rather interpret the text in B as the representation
of a value and read it like that. In all other cases an error will be presented.

To give an example, the value
	< "3" >
will evaluate to
	3
whilst the value
	< 3 >
will evaluate to the evaluation of the value stored in the variable named
	_3

--7) Advanced usage of collections------------------------------------------------------
Collections as described in section 3b) can be used to create your own types of values
with special behavior. This is all done by assigning values to some special hidden
variables inside the collection. This section will describe what these variables are
and what effect they have when set.

---a) Value-----------------------------------------------------------------------------
A collection can be given special behavior that should happen when it is evaluated. To
do this one has to create and set the value of the hidden variable named
	_value
If this variable inside a collection is defined, then whenever that collection is
evaluated that variable will be evaluated instead.

For example, when we evaluate
	{ a = 3 , b = 5 , _value = a + b }
we will get
	8
	
---b) Text------------------------------------------------------------------------------
It is possible to give a special text that should be printed whenever we want to print
something for a collection. For this one must create and set the value of the variable
named
	_toString
Whenever the program needs a textual representation of the collection, it will evaluate
the variable named
	_toString
when it is defined inside the collection and use the text value it evaluates to. Note
that it is necessary that the variable evaluates to a text value or else an error will
be presented.

An example of the usage of this hidden variable would be
	print({ _toString = "This is my personal collection."})
which would print
	This is my personal collection.
to the screen when evaluated.

---c) Type------------------------------------------------------------------------------
When constructing your own type of values it can be usefull to specify their type. This
can be done by creating and setting the value of the hidden variable named
	_type
which should evaluate to a text value. If this variable is correctly set the program
will use its value as the type for error messages and other purposes. Note that
currently there is not much use for this option besides for debugging purposes.

---d) Addition--------------------------------------------------------------------------
A collection can be given special behavior such that it works with addition. To do this
one should create and set the value of the hidden variable named
	_add
If this value is set and this collection is the first term in an addition, the addition
will be evaluated by evaluating the variable named
	_add
with as arguments the two terms of the addition.

Note that behavior for this option has not yet been finalized.

---e) Subtraction-----------------------------------------------------------------------
One can make it such that a collection can be used in a subtraction. To do this one
should create and set the value of the hidden variable named
	_subtract
If this value is set and this collection is the value a of a subtraction (see section
1biii) the variable named
	_substract
will be evaluated with as arguments a and b whenever the subtraction is evaluated.

Note that behavior for this option has not yet been finalized.

---f) Multiplication--------------------------------------------------------------------
A collection can be given special behavior when used in a multiplication. In order to do
this one should create and set the value of the hidden variable named
	_product
Whenever a product is evaluated and the first factor is a collection with this variable
defined, the variable named
	_product
will be evaluated instead with as arguments both factors.

Note that behavior for this option has not yet been finalized.

---g) Division--------------------------------------------------------------------------
Is is possible to give a collection special behavior inside a division. To make this
possible the variable named
	_divide
should be created and set. If this is the case for some collection, then whenever that
collection is the value a in a division (see section 1bv) evaluation of the division
will result in the evaluation of the variable _divide with the values a and b as
arguments.

Note that behavior for this option has not yet been finalized.

---h) Templates-------------------------------------------------------------------------
A collection already has access to all variables on levels above it, but it is also
possible to give it access to the variables in a specific collection called a template.
In contrast to variable on higher levels a collection can access hidden variables inside
a template. This allows the user to define the behavior of a certain collection only
once and easily create more collections with the same behavior.

To create and set a template for a collection, one has to create and set the hidden 
variable named
	_template
to any value that evaluates to the required template.

An example of the usage of a template would for example be
	{ a = 3 , _template = { _value = a } } 
which would evaluate to
	3