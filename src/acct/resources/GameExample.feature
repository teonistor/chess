Feature: Example games from history/trivia

  Scenario: Fool
    Given a game in its initial state
    When the following moves take place
    # The player is an implicit assertion. The move choice is an input. The capture is an explicit assertion
      | a | b |

    Then White wins
