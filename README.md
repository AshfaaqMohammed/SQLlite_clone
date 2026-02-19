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

Bug1 - inserting is happening but persistance is not successfull, we need to look into it, bug is in table cunstroctor.
07-02-2026 - Reolved the Bug 1
    solution - Problem & Solution Summary:
        Initially, row count was inferred from the database file size by assuming that all allocated pages were fully filled with rows. 
        This approach is incorrect because databases allocate storage in fixed-size pages (e.g., 4KB), 
        and a page may contain unused space even if only one row is inserted. File size therefore reflects allocated space, 
        not actual data. As a result, row count could not be reliably reconstructed after a restart. 
        The correct solution is to persist logical metadata explicitly by reserving page 0 as a metadata/header page and storing the row count there. 
        On startup, the database reads this metadata to restore state accurately.   


last i stopped - to implement part 7 there are total 7 steps, and i need to read that and i need to ask gpt to give complete code directory structure and all the code part of part7 
tutorial point.

16-02-26 - 
completed till part 10, but app does not work as expected, cause we need to implement part 11 to run the application as expected.
    what do i mean app is not working as expected - 
        insert is happening, but after one page is full, we split the leaf node and create left and right child
        when we create such, our select operation is still according to liner scan, so when we do select our data
        look like corrupted, but not, it just that our select logic is not right yet.

18-02-26 - 
    last i impleted part 11, but there are few bugs,
    1. bug is routing the elements when select command is not working, 
    2. bug is after splitting, duplication check is not happening acrross the table but only to each node.
we need to fix these, 
we can check the below conversation in gpt to start from now left over.
`ok we will go step by step, first will add debug and check, where should i put InternalNode.getKey(root,0) and what and when should i observe?'
