Feature: Example games

  Scenario: Shortest possible game
    Given a game in its initial state
    When the following moves take place
      | White | F2 | F4 | false |
      | Black | E7 | E6 | false |
      | White | G2 | G4 | false |
      | Black | D8 | H4 | false |
    Then Black wins

  Scenario: Second shortest possible game
    Given a game in its initial state
    When the following moves take place
      | White | A2 | A3 | false |
      | Black | G7 | G5 | false |
      | White | E2 | E4 | false |
      | Black | F7 | F5 | false |
      | White | D1 | H5 | false |
    Then White wins

  Scenario: Fool
    Given a game in its initial state
    When the following moves take place
      | White | D2 | D4 | false |
      | Black | F7 | F5 | false |
      | White | C1 | G5 | false |
      | Black | H7 | H6 | false |
      | White | G5 | H4 | false |
      | Black | G7 | G5 | false |
      | White | H4 | G3 | false |
      | Black | F5 | F4 | false |
      | White | E2 | E3 | false |
      | Black | F4 | G3 | false |
      | White | D1 | H5 | false |
    Then White wins
