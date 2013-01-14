# Brain - Library for biomedical knowledge manipulation

**Brain** helps you to quickly and easily buid OWL ontologies and knowledge bases. It aims at bridging the gap
 between graphical user interfaces (GUI) such as [Protege](http://protege.stanford.edu/) and the 
 [OWL-API](http://owlapi.sourceforge.net/).

The library is
useful to develop Semantic Web applications and particularly suited for the biomedical domain.
Brain wraps the interaction with the OWL-API an relies on [Elk](http://elk-reasoner.googlecode.com/) 
for reasoning tasks.

# Features

## Manchester syntax support
The interaction with Brain is done with the user friendly Manchester syntax.
Brain exposes a series of method to help you validate and transform strings of
characters into OWL expressions.

## Web Application development
Brain has been created for Semantic Web application developers. The library is thread-safe
which allows you to handle ontologies in a web server setting.

## Scalability

OWL is a very demanding in terms of computing, but thanks to the 
[OWL 2 EL](https://github.com/loopasam/Brain/wiki/OWL-2-EL) profile, it is possible to
thread reasoning some tasks in parallel (Elk reasoner). Brain therefore supports the OWL 2 EL to help you
build scalable solutions.

## Query
OWL knowledge bases can be easily queried with Brain. Powerful
questions can be quickly answered with the help of the Elk reasoner.
Brain accepts the formulation of queries from labels as well as from class
expressions.

# Documentation

Read the wiki for more details: https://github.com/loopasam/Brain/wiki
