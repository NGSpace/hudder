# Naming
With Hudder being as old as it is, over time, with no proper guidelines, the names of variables, functions and methods in Hudder have grown inconsistent.
With some variables being in lowercase and some in snake_case, some functions being lowercase and some camelCase, etc.
This page's purpose is to solve this by setting proper guidelines for naming.
> [!NOTE]
> This page does not touch the Hudder language's syntax, as of now there are no guidelines for it.

> [!NOTE]
> If you think of a valid exception to the guidelines defined here that is not already addressed, feel free to start a discussion on it.

# Variables
This section discusses the naming guidelines for variables
## Casing
Variables use the snake_case casing style, meaning every word is lower case and seperated by an underscore.
## Booleans
Boolean variables should have the `is_` prefix only *if* the variable name is only one word
## Numbers
If you have a value that has a decimal point then the variable should ideally have 1 version that returns the exact value with the decimal point.

If you think Hudder could benefit from having 2 versions for the variable, one with and one without the decimal point, then the naming scheme should be as follows:

The version without the decimal point should take the name of the variable.

The version with the decimal point should be the same as the version without but with an added "_d" suffix.
## Exceptions
*Additions to existing variables:*

If there already exists a variable that doesn't follow those guidelines, you may use the original name of the variable but any additions should follow the guidelines.
## Examples
+ a variable that returns the slot that the player's mouse is hovering at in the inventory should be named something similar to `hovered_inventory_slot`
+ a variable that returns whether the player is flying should be named `is_flying`
+ a variable that returns whether the player is on the ground should be named `on_ground`
+ a variable that returns whether the player is jumping for the first time should be named `first_jump`

# Functions
## Casing
Functions should use camelCase
## Booleans
Functions that return a boolean should have the `is` prefix
## Getters
Getter functions should have the `get` prefix

# Methods
## Casing
Method names should use snake_case
## Verbosity
Method names should use as few words as possible while still ensuring the method name is descriptive
## UI methods
UI methods, should never, under any circumstances have a `draw`, `render` or similar, prefix. This should be understood by the "Verbosity" section but I just want to make it clear.
