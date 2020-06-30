Feature: Example games from history/trivia

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
