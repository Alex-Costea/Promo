# Promo

**Promo** (**Pro**cedural **Mo**del) is a minimalist esoteric programming language based on Brainfuck, with a few key differences. It is built to be an effective model of computation for theoretical purposes.

# Features

## Turing completeness

Promo is Turing complete, and as such every algorithm can be simulated on it, given enough time and memory.

## Minimalism

Promo only uses 6 characters (+,-,<,>,#,@) every other character being ignored.

## Every program compiles

A big inspiration for making this language is the fact that Brainfuck, minimalist as it is, has programs that can't compile properly (those with parentheses not matching). By replacing the loop functionality with procedures, in Promo there are no invalid programs, which can be useful for theoretical purposes. There are exactly 6^n valid programs with n characters.

## Procedural programming

Promo, as the name suggests, is built around procedural programming and recursion. In fact, it's the only way to loop code.

## Dynamic calls

There is support for calling the nth function, where n is the value of the current cell.

## If and For loop-like functionality

Using functions, implementing "if x is not 0, then:" is easy. And using the mace pattern, you can implement functionality similar to for loops.

## Unboundedness

Another difference between Promo and Brainfuck is Promo is unbounded. The list it operates on can be extended to infinity in both directions, and every element on it can get arbitrarily big or small. This makes it possible to write code that calculates ridiculously high numbers easier, even if you couldn't actually run it because of lack of memory and time. Again, this is useful for theoretical purposes.

# Language details

The program operates on a doubly linked list, infinite in both directions. All cells have an integer written in them. At first, all cells contain 0, except the beginning cell which cointains the input number, when run in xPromo mode.

There are 8 commands, corresponding to 6 characters:

- **+** adds one to the current element in the list

- **-** removes one from the current element in the list

- **<** goes to the left by 1 in the list

- **>** goes to the right by 1 in the list

- **#** and **@** are the characters used for function calls. Calls to nonexistent fuctions are simply ignored.

- - A string of #'s and @'s, starting with an #, is interpreted as "convert # to 1 and @ to 0, and call the function corresponding to that number in binary". For example, # calls function 1, #@ calls function 2, ## calls function 3 etc. The calls are only made if the current cell is not 0.

- - A string of n @'s without a # preceeding it is taken as a dynamic call. You usually do not need this. It is equivalent to the following instructions:

    1. let k = the current cell
    2. move to the right by n
    3. if the current cell is not 0, call function k
    4. move to the left by n


- **+- (cross)** separates procedures between them. everything before the first **+-** is considered procedure 0, then it separates procedures 1, 2, 3 etc.

- **>< (x)** is a special symbol which defines the way the program will run. If (and only if) the first 2 valid characters are ">" and "<", the program will run in xPromo mode. Otherwise, it will run in iPromo mode

- **<> (diamond)** has no special meaning (and as such is not an individual command), it simply means "go to the left and then to the right". It can be used, however, as filler between function calls. For example "#<>#*" means first call function #1 and then function #2. If nothing (or characters with no significance such as spaces) is put between the 2 calls, it would be interpreted as "call function #6"

- **+--#** (mace), with the # being part of a recursive call to the current function, is, again, not technically a separate command. However, functionally it behaves like a "Repeat [current cell] times" command, with the repeat being unconditional on the value of the current cell. This is very useful because it shortens and simplifies functions that, for example, multiply a number by 3.

When iPromo mode is used, no input is taken. Running in iPromo mode is equivalent to running in xPromo mode with input 0. If run in xPromo mode, an integer x will be taken as input.

The program will return the content of the last cell it is in as output. As such, formally speaking every xPromo program defines a computable partial function from Z to Z. An iPromo program can be seen as being equivalent to a "Maybe Integer" (since it will either return an integer, or run forever and return nothing).

## Example

This code will return 2 to the power of the input number (assuming it's >=0)

    ><
    >+<#>  +-
    -#>#@<  +-
    -#@++

The formatting is unnecessary, it's only added for clarity. This code is equivalent to

    ><>+<#>+--#>#@<+--#@++

Another few easy programs:

    >< // f(x)=x
    >< > // f(x)=0
    >< + // f(x)=x plus 1
    >< # +- -#++ // f(x)=2*x, x positive or 0
    >< # +- # // f : {0} → {0}, f(0)=0
    +<@ // shortest program to loop forever

# The Promo game

What's the biggest number that can be outputted with an iPromo program with n characters (that doesn't run forever)?

This is equivalent to the Busy Beaver function, but it's a more clear and simple definition. As such, iBB(n) is an uncomputable function, and it grows faster than any computable function.

iBB(21) is at least 2^256-2 (115792089237316195423570985008687907853269984665640564039457584007913129639934), corresponding to program:

    ++#+#+#+--#+#@+--#@++
    
iBB(22) has more than 1 million digits.

# Kolmogorov complexity

How many characters does the shortert iPromo program that outputs n have?

For every n<=10000, the answer is at most 26. 26 is not necessarily the minimum number with this property however, it's only an upper bound. Note that if n can be outputted with k characters, it can also be outputted with k+1 characters.

# Try it out!

https://alcostar.net/Promo/

**NOTE:** There is no guarantee that this works in any instance and there can be bugs
