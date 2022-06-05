import PromoCompiler.*

fun main()
{
	val code=readln()
	val BBCL=Promo.toBBCL(code)
	println(BBCL)
	val mode=BBCL.replace("\n"," ").split(" ")[1]
	val x=mode=="x"
	val i=mode=="i"
	val program=Promo(BBCL)
	var input=0;
	if(!i && !x)
		throw Exception("Syntax Error")
	if(x)
		input=readln().toInt()
	val output = program.run(BigInt(input))
	print(output)
}