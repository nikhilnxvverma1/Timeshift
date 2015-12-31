# Timeshift
A keyframe based animation software with support for both temporal and spatial animation(for translation)
The system comprises of a tool based graphical workspace and a tabular key frame table for animating several properties of a shape.
This also includes motion path for spatial animation.The animations themselves are made in seperate compositions
Shapes can be selected individually or in a group with an outline around them. On double clicking a shape, a detailed "gizmo" outline
appears for visually tweaking that particular shape's properties. Every action in the software is undoable(and redoable) to 
multiple levels.

Timeshift is a multi module gradle project written in Java(JavaFX used for the frontend). The project is primarily divided into five 
sub projects: The first three projects are coded to be completely void of any java specific dependency

1.Util
This is a relatively small module that sits at the very top.This project deals with all the utility based classes. Some important ones 
include UtilPoint, BezierPoint and MathUtil

2.Timeline
This module is the beating heart of all the animation stuff. Essentially it comprises of a Timeline class and a set of ChangeNode classes
that facilitate a callback based mechanism for triggering a change.

3.Motion
This module keeps all the shape models and additionally it also has composition controllers that basically "contain" all the elements of
a composition

4.Level editor
This is the JavaFX project that has the main class. The term "level editor" is actually used mostly because it started with the intention to be used as a 
tool in a game. But now its much bigger than that.From this project onwards, there is natural dependency on every Java based feature.

5.Game
This project is mostly unused for now but will be used later on while creating a "runtime" for running these animations independently 
without the editor in a different frontend.In other words , it will essentially house all the "views".Again ignore the word "game"

This project is still under development. Some of the tools will not work.So only the tools that are "pressable" will work. Specifically
1.Selection
2.Circle
3.Triangle
4.Parallelogram
5.Polygon

To run the project , you must have:
1.Gradle
2. JDK 1.8

Simply run the Main.java in the "level-editor" project.
You can import the project in your favorite IDE if you want( I use IntelliJ) 

