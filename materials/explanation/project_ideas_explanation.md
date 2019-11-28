Project Ideas
=============

The goal is to develop an IntelliJ IDEA plugin that can help with software development. Check out existing plugins at https://plugins.jetbrains.com/idea. There are several references to plugin development. 
For example, see [Plugin Development Guidelines](https://www.jetbrains.com/help/idea/plugin-development-guidelines.html), [IntelliJ Platform SDK](http://www.jetbrains.org/intellij/sdk/docs/welcome.html), [IntelliJ plugin development tutorial](https://www.plugin-dev.com/intellij/).

You can choose a project from the following list or other ideas that you may have. Your team may implement a new plugin from scratch, or extend an existing open source project. If your team decides to extend an existing open source project, you should clearly show what __new__ features/functionalities will be added.
It will take a __considerable amount of time__ to study and understand the existing codebase. (see [Object-Oriented Reengineering Patterns](http://scg.unibe.ch/download/oorp/OORP.pdf) for more information on reverse engineering).



#### Project 1: Software Metrics

Calculate a set of software metric(s) for a project of your choice, and visualize these metrics in an effective way. For example, you may show calculated metrics in a table (for each class or method), possibly together with red/yellow/green ratings like [Maintainability Index](https://blogs.msdn.microsoft.com/zainnab/2011/05/26/code-metrics-maintainability-index). See the following links for a list of software metrics. You may want to implement a plugin that calculates and visualizes a subset of those metrics.

- https://en.wikipedia.org/wiki/Software_metric
- [Martin's design quality metrics](https://docs.google.com/viewer?a=v&pid=sites&srcid=ZGVmYXVsdGRvbWFpbnxkZWxsZGVza3RvcGZpbGVzfGd4OjJjOGM1MDk4ZGU5MzAwNjM)
- [Object-oriented metrics suite](http://www.aivosto.com/project/help/pm-oo-ck.html)

There is also an existing plugin to compute software metrics as follows. You may get some idea from the plugin, or even extend it with new functionalities or visualizations!

- [MetricsReloaded](https://github.com/BasLeijdekkers/MetricsReloaded)



#### Project 2: Finding Code Smells or Bad Designs

Identify code smells or bad designs by analyzing source code, based on _software metrics_ or _pattern matching_ on the abstract syntax tree of code. Those potential problems identified by your plugin can be shown in the IDE as warnings or errors. See the following links for a list of potential code smells and bad designs. Some of them are already discussed in the class. You may want to implement a plugin that identifies a subset of those problems.

- https://en.wikipedia.org/wiki/Code_smell
- https://sourcemaking.com/refactoring/smells
- https://refactoring.guru/refactoring/smells

There are also existing plugins to identify code smells or bad designs. You may get some idea from the plugin, or even extend it with new features!

- [Checkstyle](https://github.com/checkstyle/checkstyle)
- [SpotBugs](https://github.com/spotbugs/spotbugs)



#### Project 3: Automated Refactorings

Perform automated code refactoring(s), besides those already supported by IntelliJ IDEA. This project can be combined with [Project 2: Code Smell](#project-2-finding-code-smells-or-bad-designs).
Your plugin will show the resulting code by your refactoring(s), and transform the code if approved by the user. See the following links for standard refactorings. Some of them are already discussed in the class.
You may want to implement a plugin to perform some instances of those refactorings.

- https://en.wikipedia.org/wiki/Code_refactoring
- https://sourcemaking.com/refactoring/refactorings
- https://refactoring.guru/refactoring/techniques


There are also existing plugins to support extra automated refactorings. You may get some idea from the plugin, or even extend it with new features!

- [Refactoring Plugins for IDEA](https://plugins.jetbrains.com/search?pr=idea_ce&headline=51-refactoring&pr_productId=idea_ce&canRedirectToPlugin=false&showPluginCount=false&tags=Refactoring)



#### Project 4: Code Completion and Recommendation

Improve existing code completion systems in various ways. All widely used IDEs, such as IntelliJ IDEA, Eclipse, and Visual Studio, have some code completion features. Many of these systems employ data-mining and machine-learning approaches to improve suggestion accuracy by using existing code bases as training sets. See the following links for more details:

- https://en.wikipedia.org/wiki/Intelligent_code_completion

There are several directions for this project. You may extend existing plugins by adding more features (e.g., new templates, targeted APIs, source code models, etc.), or by improving the user interface (e.g., new input methods, better GUI, more customization, etc.). The following links show existing plugins in IntelliJ IDEA:

- [Code Completion in IntelliJ](https://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/code_completion.html)

  