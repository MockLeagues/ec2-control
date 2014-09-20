# EC2 Control

Command line tool written in Java to start/stop EC2 instances. Example usage:

```
$ java -jar ec2-control-VER-jar-with-dependencies.jar -a access-key
  -s secret-key -r us-east-1 -c start i-instid1 i-instid2 ...
```

Get help on usage:

```
$ java -jar ec2-control-VER-jar-with-dependencies.jar -h
```

This tool is available as part of _"The Joy of Unix in Windows Tool Bundle"_:

[![](http://static.wiztools.org/wiztools-cli-tools.png)](http://cli-bundle.wiztools.org/)
