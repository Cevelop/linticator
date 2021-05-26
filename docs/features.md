# Linticator's Features

With Linticator, you get immediate feedback on your code so you can promptly fix any problems. Don't let bugs infest your code!

## Problems Overview

Linticator enriches the Problems View that shows warnings and errors from the compiler with Lint messages, making them easy to spot.

![](/docs/images/linticator_problems_view.png)

From this view, you can quickly jump to the corresponding source code location and display explanations on this specific message.

## Message Explanation View

Linticator contains a view that automatically shows you an explanation message for the currently selected entry in the Problems View:

![](/docs/images/linticator_documentation_view.png)

## Quickfixes

Quickfixes are actions that can be performed on a Linticator source code annotation to resolve the problem. Possible solutions range from simply ignoring a problem &ndash; perhaps it was a false positive &ndash; to automatically fix your code. In the following screenshot, we see the lint message 1093 and two possible resolutions for this problem:

![](/docs/images/linticator_quickfix_demo_virtual.png)

For example, applying the "Declare function virtual" Quickfix changes the code as follows and removes the now unneeded Lint error message:

![](/docs/images/linticator_quickfix_demo_virtual_resolved.png)

Linticator comes with several such Quickfixes.

