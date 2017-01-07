# Ant Colorizor

An easy way to add color to Apache Ant build output. Unlike many other ant color utilities, this one does not automatically color your output based on rules, but allows explicit control of colors and formatting from within the ant build file itself. Requires ant version 1.8 or above and color support from your terminal. For Windows you might have to use something like [ANSICON](https://github.com/adoxa/ansicon).

Note that this library is not doing any real magic and is just a convenient wrapper around ANSI color codes.

For full documentation see http://www.joesdiner.org/programs/ant-colorizor/

## Basic usage

Add the following to your ant buildfile:

~~~~ .xml
<componentdef classname="org.dogsplayingpoker.ant.PropertyHelpers.ColorPropertyEvaluator"
              name="colorpropertyevaluator"
              classpath="path/to/ant-colorizor.jar" />
<propertyhelper>
  <colorpropertyevaluator />
</propertyhelper>
~~~~

Then add `${color:expr}` markup to any task that supports property expansion. Tested with `echo` and `fail`, but should work with others as well.

~~~~ .xml
  <echo>${color:blue}Hello World! in blue${color:reset}</echo>
  <echo>${color:bold}Hello World! in bold${color:reset}</echo>
  <echo>${color:bg-red}Hello World! with a red background${color:reset}</echo>
  <echo>${color:cyan,bold}Hello World! in cyan and bold${color:reset}</echo>
  <echo>${color:green,underline}Hello World! green and underlined.${color:reset-underline} No more underline but still green${color:reset}</echo>
  <echo>${color:purple}Colors are in effect until explicitly changed...</echo>
  <echo>...not at the end of a message${color:reset}</echo>
  <echo message="${color:green}Hello World! in green from within a message${color:reset}" />
~~~~

Valid foreground color values are:

&nbsp;&nbsp;&nbsp;&nbsp;black, red, green, brown, yellow, blue, purple, cyan, gray

Valid foreground color values are:

&nbsp;&nbsp;bg-black, bg-red, bg-green, bg-brown, bg-yellow, bg-blue, bg-purple, bg-cyan, bg-gray

Valid formatting values are:  

&nbsp;&nbsp;normal, bold, dim, underline, blink, reverse, hidden
  
Valid formatting reset values are:

&nbsp;&nbsp;reset-normal, reset-bold, reset-dim, reset-underline, reset-blink, reset-reverse, reset-hidden

## Disabling
  
To globally disable colorization without having to remove the markup from all of your tasks (for example, on a terminal which does not support colors), set the property `org.dogsplayingpoker.ant.disable-color`

~~~~ .xml
  <property name="org.dogsplayingpoker.ant.disable-color" value="true" />

  <echo>${color:red}This text will not be red, despite the markup${color:reset}</echo>
~~~~

