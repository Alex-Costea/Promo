import math
def sequence(x):
    l=[0]
    for i in range(1,x+1):
        l.append(math.inf)
        for k in range(2,i+2):
            inbase=[]
            q=i
            while q>0:
                inbase.append(q%k)
                q=q//k
            for j in range(len(inbase)-2,-1,-1):
                if inbase[j]>(k//2+1) and inbase[j+1]!=(k-1):
                    inbase[j]=k-inbase[j]
                    inbase[j+1]+=1
            mylen=len(inbase)-1
            mysum=sum(inbase)
            l[i]=min(l[i],mysum+2*mylen+k+6)
            inbase=[x if x!=0 or i==0 else 2 for i,x in enumerate(inbase)]
            mysum=sum(inbase)
            l[i]=min(l[i],mysum+mylen+k+3)
    return l

def multiplications(l):
    for x in range(1,n+1):
        for y in range(2,n//x+1):
            t=1
            cur=x*y
            while cur<=n:
                l[cur]=min(l[cur],x+y+2*(t-1)+8)
                l[cur]=min(l[cur],x+y+3*(t-1)+5)
                cur*=y
                t+=1
                
            t=1
            cur=x*y
            while cur<=n:
                l[cur]=min(l[cur],x+y+2*(t-1)+5)
                cur+=1
                cur*=y
                t+=1
    return l

def smoothen(l):
    for i in range(len(l)):
        k=0
        for j in range(i-1,-1,-1):
            k+=1
            if(l[j]<l[i]+k):
                break
            l[j]=l[i]+k
        k=0
        for j in range(i+1,len(l)):
            k+=1
            if(l[j]<l[i]+k):
                break
            l[j]=l[i]+k
    return l

n=10000
l=sequence(n)
l[0]=0
l=multiplications(l)
l[8560]=min(l[8560],29) # 8560 = ++#>+#>--#>--#>--+-->++++++++<#
l=smoothen(l)
with open("Kolmogorov complexity upper bounds.txt", "w") as f:
    for i in range(0,len(l)):
        f.write(str(i)+" "+str(l[i])+"\n")
