package PromoCompiler

val pass=Unit

class Promo(BBCL:String){
    private var prog : programCommand
    var xMode : Boolean
    private var running : Boolean

    companion object
    {
        fun toBBCL(it:String) = bbcompiler.Promo2BBCL(it).show_data()
    }

    init{
        val x=programCommand()
        x.read_data(BBCL)
        this.prog=x
        xMode=prog.xMode!!
        running=false
    }


    fun run(input:BigInt):BigInt
    {
        return runProgram(prog, input).result
    }
}
