[,,c,i]=process.argv
a=[/^></.test(c=c[r='replace'](/[^+-@#><]/g,'').split`+-`)*i|0]
f=x=>c[x]&&eval(c[x][r](/[+-]/g,'a[p]=~~a[p]$&1;')[r](/>|</g,'p+=1$&0?1:-1;')[r](/#[#@]*/g,b=>`a[p]&&f(0b${b[r](/./g,t=>t<'@'|0)});`)[r](/@+/g,'k=a[p];p+=n="$&".length;f(a[p]?k:-1);p-=k'))
f(p=0)
console.log(a[p])