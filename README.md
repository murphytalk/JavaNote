# README

[![Build Status](https://travis-ci.org/murphytalk/JavaNote.svg?branch=master)](https://travis-ci.org/murphytalk/JavaNote)

This is my little playground of Java and the other JVM languages (e.g. Kotlin). 

Run this to build a fat jar:

```
./gradlew fatjar
```

Some codes are used in my other projects, including: 

- [ShuntingYard.kt](src/main/kotlin/ShuntingYard.kt)
  - An implementation of [shunting yard algorithm](https://en.wikipedia.org/wiki/Shunting-yard_algorithm) to parse mathematical expressions specified in infix notation and produce [Reverse Polish notation](https://en.wikipedia.org/wiki/Reverse_Polish_notation).
  - A function to evaluate [Reverse Polish notation](https://en.wikipedia.org/wiki/Reverse_Polish_notation).
- [ArithmeticGenerator.kt](src/main/kotlin/ArithmeticGenerator.kt): generate elementary arithmetic problems randomly, but follow pre-defined rules, for example the number of operators, how many digits each number should have and what are the possiblities, if negative result is allowed during the calculation, etc.
- [Arithmetic.kt](src/main/kotlin/Arithmetic.kt) is a console app to randomly generate elementary arithmetic problems. I make my son solve the problems it generates and it only finishes while all problems are solved correctly. Working on an Android version now. Some screenshots of the console app.
 
  ![Arithmetic 1](img/arithmetic-1.png)
  ![Arithmetic 2](img/arithmetic-2.png)
- Math Homework - this is a web app that I use to torture my 9-year old.

  ![Web Arithmetic 1](img/arithmetic-web-1.PNG)
  ![Web Arithmetic 2](img/arithmetic-web-2.PNG)
  ![Web Arithmetic 3](img/arithmetic-web-3.PNG)

 
