a=int(input("enter the number of names: "))
names=[]
for i in range(a):
    names.append(input("enter the name: "))
print("\nYou entered:")
for i in range(a):
    print(names[i])