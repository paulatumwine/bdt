users = load 'input/users.txt' using PigStorage('|') as (userId:int, age:int, gender:chararray, occupation:chararray, zipCode:int);
filtered = filter users by gender == 'M' and occupation == 'lawyer';
grouped = group filtered all;
count = foreach grouped generate COUNT(filtered);
store count into 'male-lawyers-output';
