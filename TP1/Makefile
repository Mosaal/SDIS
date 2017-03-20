JAVA = java
JAVAC = javac
JFLAGS = -g -d bin/
CPFLAG = -cp
CLASSPATH = bin/
SOURCES = $(wildcard src/Main/*.java) \
			# $(wildcard src/*.java) Add others
CLASSES = $(SOURCES:.java=.class)

all: $(CLASSES)

clean:
	rm -f bin/*.class

run:
	$(JAVA) $(CPFLAG) $(CLASSPATH) ${ARGS}

%.class: %.java
	$(JAVAC) $(JFLAGS) $<