# FAQ

## Does Linticator support PC-lint Plus?

Yes, Linticator supports PC-lint Plus, with one caveat: the msg.xml file that Linticator asks for during configuration needs to be manully generated:

<pre>
./pclp64_linux "-dump_messages(file=msg.xml, format=xml)"
</pre>

You can use the generated file in the configuration wizard.

## Can I add more than one Custom Lint File file to a project's configuration?

Yes, additional ```.lnt``` files can be added to the Custom arguments section.

![](/docs/images/additional_custom_lint_files.png)


## What information about the compiler does Linticator use to generate the project configuration?

Linticator uses Eclipse CDT's information about compiler includes and symbols:

![](/docs/images/linticator_cdt_includes.png)

![](/docs/images/linticator_cdt_paths_symbols.png)

All these includes and defines are passed to PC/Flexe-Lint.


## Can I use PC-lint on Linux?

Yes, you can. [PC-lint runs under Linux with Wine](http://www.gimpel.com/Discussion.cfm?ThreadID=609), all you need is a wrapper script that calls wine with pc-lint, like:

<pre>
#!/bin/sh
/usr/bin/wine /opt/pc-lint/lint-nt.exe $* | tr '\\' /
</pre>

And then use this script as the "Lint executable" in the configuration entry.


## Where can I change the Linticator console font?

The Linticator Console uses the same font settings as the debug console, so you can change its appearance under _Window_ → _Preferences_ → _General_ → _Appearance_ → _Colors and Fonts_ → _Debug_ → _Console Font_.


## Can I exclude specific source files?

If you have source files in your project that you want Lint to ignore, you can exclude them via the _Resource Configurations_ menu in the file's context menu.

![](/docs/images/exclude_resource_from_build.png)

You can see and configure (e.g. with a wildcard match) all the excluded resources in the _Paths and Symbols_ settings of your project:

![](/docs/images/excluded_resources.png)

Alternatively, Lint has two options to *mark header and implementation files as library code*. The flags are _+libh(filename.h)_ for headers and _+libm(C:\filename.c)_. Note that for libm you need to specify the full path to the file, otherwise Lint will ignore it. Linticator allows you to use variables for these settings. For example, to mark B.cpp and C.h as library, you can use the following flag:

![](/docs/images/linticator-mark-as-library.png)

To ignore directories, wildcards in combination with _libm_ and _libh_ can be used:
<pre>
+libm("${ProjDirPath}/src/generated/*.cpp")
+libh("${ProjDirPath}/src/generated/*.h")
</pre>


## Configuration Troubleshooting Guide

If you were able to complete the configuration wizard but Linticator doesn't seem to work, there are a few things you can check.

First, you can revisit the configuration tester from the initial setup wizard under the _Help_ → _Linticator Configuration_ menu.

Linticator creates a hidden directory _.lint_ your project root where it stores its auto-generated source files. This looks as follows:

![](/docs/images/linticator_config_folder2.png)
![](/docs/images/linticator_config_folder1.png)

If some of these are missing, there might be a problem with the configuration builder. First, check if the builder is active in your project's settings:

![](/docs/images/linticator_builders.png)

Second, check the Linticator Console for entries related to the configuration builder:

![](/docs/images/linticator_config_builder_log.png)