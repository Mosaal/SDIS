JAVA = java
JAVAC = javac
JFLAGS = -cp ./bin
JCFLAGS = -d ./bin

SOURCES = $(shell find ./src -type f -iname '*.java')
CLASSES = $(shell find ./bin -type f -iname '*.class')

all:
	@for src in $(SOURCES); do \
		echo $(JAVAC) $(JCFLAGS) $$src; \
		$(JAVAC) $(JCFLAGS) $$src; \
	done

run:
	$(JAVA) $(JFLAGS) ${args}

clean:
	@for cls in $(CLASSES); do \
		echo $(RM) $$cls; \
		$(RM) $$cls; \
	done