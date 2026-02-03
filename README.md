# SQLlite_clone

- taken reference from https://cstack.github.io/db_tutorial/parts/
- In part 1
  * added
    * app/Main
    * cli/Console
    * cli/InputHandler
    * command/MetaCommandProcessor
  * updated
  
- In part 2
  * added
    * command/insertCommand
    * command/selectCommand
    * command/sqlCommand
    * command/sqlCompiler
    * command/sqlExecutor
  * updated
    * cli/InputHandler

- In part 3
  * Added
    * model/Row
    * model/Table
  * Updated
    * command/sqlCompiler
    * command/insertCommand
    * command/selectCommand
    * command/sqlExecutor

- In part 4
  * added
  * updated
    * command/sqlCompiler

inserting is happening but persistance is not successfull, we need to look into it, bug is in table cunstroctor.