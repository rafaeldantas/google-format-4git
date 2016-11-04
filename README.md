# google-format-4git

Simple wrapper for google-java-format that allows you to format files based on git staged/modified files.

Usage:
```
$ java -jar formatter.jar
```

Usage:
```
    -g, --g, -git, --git
       Use git staging area to detrmine the list of files to be formatted
       Default: false
    -ml, --ml, -max-line-length, --max-line-length
       Format only modified lines
       Default: 140
    -m, --m, -modified-lines, --modified-lines
       Format only modified lines
       Default: false
```       
