JAVAC = javac
JFLAGS = -g -d bin/
SOURCES = $(wildcard src/*.java)
CLASSES = $(SOURCES:.java=.class)

all: $(CLASSES)

clean :
	rm -f bin/*.class

%.class : %.java
	$(JAVAC) $(JFLAGS) $<