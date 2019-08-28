# COMPSYS725 Assignment 1

## Notes about my commands
**The commands themselves are not case sensitive, so instead of USER you can type user, instead of LIST V, you can type list v, however please note that directories or filenames passed as arguments ARE case-sensitive.**    

###### USER
This doesn't authenticate the user unless you run ```USER guest```.

##### ACCT
Works as protocol dictates. You can use ```ACCT aa``` to log in.

##### PASS
Works as protocol dictates. You can use ```PASS aa``` to log in.

##### You cannot call any of the below commands successfully without authentication

##### LIST
Works as protocol dictates. You can use ```LIST F``` or ```LIST V``` and then pass a **relative path** after it to list the directory.

##### TYPE
Works as protocol dictates.

#### CDIR
Works as protocol dictates. You can CDIR before authenticating, but you **MUST** call ACCT/PASS to CDIR into the directory that you specified before authenticating (you cannot substitute this step with ```USER guest```, you must use ACCT/PASS).    
Other thing to note is that this command takes **relative pathing**, so you can run ```CDIR ..``` to go up a level. This command does not work if you pass it an absolute path.

##### NAME/TOBE
Works as protocol dictates. You cannot call TOBE without first calling NAME successfully.

##### KILL
Works as protocol dictates.

##### RETR/SEND/STOP
Works as protocol dictates. You cannot call SEND/STOP without first calling RETR succesfully.

##### STOR/SIZE
Works as protocol dictates. The client will automatically call SIZE after a successful STOR call. But the user may not call SIZE without first calling  STOR successfully.

##### DONE
Works as protocol dictates.

## Using the program    
Instructions:
1. Make sure you are in the same directory as the Makefile
2. Open a terminal
2. Run ```make clean```
3. Run ```make run_server```
4. Open another separate terminal and in it, type ```make run_client```
5. Type your commands in the client terminal.

## Running tests
The Makefile runs 2 different test suites. The server output will be printed to the terminal as if you were actually running the program.
Instructions:
1. Make sure you are in the same directory as the Makefile
2. Open a terminal
3. Run ```make clean```
4. Run ```make run_server```
5. Open another separate terminal and in it, type ```make run_tests```

## About the test suite

###### Test commands part 1
The first set of test commands focus primarily on calling commands that cannot be called before the user is authenticated by logging on. The server should respond by rejecting these commands.    
It then tests the user-authenticated-only commands after the user has logged in, i.e - it tests RETR, SEND, NAME, KILL and STOR.

###### Test commands part 2
The second set of test commands focus on calling commands without enough arguments.

###### Test commands part 3
The third set of test commands focus on authenticating the user with invalid credentials, and then tests them with correct credentials.

###### Test commands part 4
The fourth set of test commands test the KILL and NAME commands with non-existent and existing file names. Also tests TOBE without calling NAME in advance.

###### Test commands part 5
The fifth set of test commands test the LIST and CDIR commands more extensively. This includes - invalid list types, listing non-existent directories, and trying to CDIR into non-existing directories.

###### Test commands part 6
The fifth set of test commands test the STOR and RETR commands more extensively. This includes retrieving non-existent files, retrieving existing files, and all the different types of STOR. Also tests calling SEND/STOP before RETR, and SIZE before STOR.
