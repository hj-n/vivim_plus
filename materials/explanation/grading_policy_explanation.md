Grading Policy
==============

Team projects will be evaluated based on (1) how well the final product is developed, and (2) how well the team has followed XP. Project scores will by default be the same for all team members, but some team members can get a higher or lower score than the team score based on individual performance. 

Note that project wiki pages, issue tracking in GitLab, and git commit records will be used as "data" for grading. If this data is insufficient (e.g., because you do not commit frequently with meaningful commit messages), your grade will be severely affected.


Product (35%)
-------------

  - Requirements
    - Your project is well motivated, and your contribution is nontrivial (compared to the existing code base).
    - User stories are written properly; short, understandable, valuable, and testable.
    - User stories describe well the functionality of your plugin.

  - Design
    - Your software meets basic design principles, such as low coupling, high cohesion, etc.
    - Your software is well refactored and thus has (almost) no code smell at the design level.
    - Design patterns are used appropriately, if necessary.

  - Implementation
    - All features (i.e., user stories) are well implemented and account for corner cases.
    - Have comments for all public methods and public classes following the proper commenting conventions
    - The code is properly and consistently formatted, either manually or by using the code formatter.

  - Testing
    - JUnit tests for at least 80% statement coverage for each non-GUI class.
    - (Automated) black-box test cases for each user story.
    - Manual tests are only acceptable for testing GUI components (otherwise, you must discuss beforehand with your team's TA).


Process (35%)
-------------

  - Planning game
    - Division of user stories into iterations
    - Break down of user stories into tasks
    - Proper (re-)estimates associated with each user story and team's _velocity_

  - Test-driven development and refactoring
    - Write (failing) tests – write the code – refactor code
    - Commit changes for each step
    - Do _not_ complete your program and then try to write tests, or you will end up having to rewrite much of the code to make it testable.

  - Configuration management
    - Proper use of codelines and branching
    - Continuous integration
    - Frequent commits with meaningful commit messages

  - Project management
    - Weekly/iteration meetings 
    - Role assignments
    - Pair-programming with pair rotations
  

Participation (30%)
-------------------

  - Peer evaluation of team members
  - Contribution to artifacts (e.g., version control commits, Wiki updates, issue tracking, etc.)
  - TA opinion from iteration meetings
