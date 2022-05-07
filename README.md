# 540-p1

## Team member:
Hao Zhang hzhang62 
<br>
Ganyu Hou ghou3 
<br>
Junyan Li jli56 
<br>

### Note: This project is developed based on oracle 11gR2, please ensure that the JDBC environment has been configured correctly.

## 1.Data initializing
You need change jdbcURL, user,passwd in line 15,16,17 of parkinglot.java
<br>
Upload create_tables.sql,seed_final.sql (in Folder oracle_iniSQL) to the server which could access to oracle DB, 
log on SQLPlus with proper DB user, execute these command in SQLPlus:


@create_tables.sql 
<br>
@seed_final.sql
<br> <br>
Note: create_tables.sql is for creating tables, and seed_final.sql is for importing sample data;

## 2. Compile code command:
javac parkingLot.java 

## 3.Execute code command:
java parkingLot

## 4.User interface
### To choose one operation, a number should be input. For example, when we see this screen:
Select an action to continue:
1--> Enter Lot
2--> Exit Lot
0--> Exit program

### input 1 press key 'Enter' then the EnterLot function will be called.

## 5.User log in
After entering the user interface, we need to choose a role to continue.
### For Admin Role, the univid is:
admin

### For Employee Role, the univid is:
1007999 
<br>
1006003 
<br>
1006020 
<br>

### For Student Role, the univid is:
1006135  
<br>
1006022  
<br>

### All users' password is:
password
