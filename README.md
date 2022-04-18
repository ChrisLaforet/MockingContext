# Mocking Context
Creates lightweight application context for unit tests

The problem with most testing in large legacy applications is that they depend on layer after layer of dependencies.  Either one has to run testing in a Spring context (which slows down a developer's test suite for TDD) or do crazy Spy antics with a mocking library and create a brittle test suite with wiring to internals.  The purpose of this project is to create a very light application context to handle autowiring mocks and fakes at any level without loading a full framework.

This is mostly a different scenario than that of the "fragile test problem" in which the tests are hopelessly coupled to the code.  The testing scenario defined above is a (hopefully) proper test suite with a deep depedency graph. 


### Credits
The DI codebase is inspired in no small part by Martin Häusler's articles on DEV entitled "Understanding Dependency Injection."  These can be found at https://dev.to/martinhaeusler/understanding-dependency-injection-by-writing-a-di-container-from-scratch-part-1-1hdf

