package PromoCompiler

internal class runProgram(program:programCommand,input:BigInt) {
    private var program=program
    private var i=0
    private var list:MutableMap<Int,BigInt> = mutableMapOf()
    private val input=input
    private var level=1
    private var nOnLevel : MutableMap<Int,BigInt> = mutableMapOf()
    val result=run()

    private fun run():BigInt
    {
        if(input.isEmpty)
            return input
        list[0]=if(program.xMode!!) input else BigInt(0)
        runFunction(0)
        return CurrentPosition()
    }

    private fun runFunction(index:Int)
    {
        val x=program.programBody.indexOfFirst{it.functionNumber==index}
        if(x==-1)
            return
        level++
        for(y in program.programBody[x].body)
         runCommand(y)
        level--
    }

    private fun runCommand(y: commandInterface)
    {
        when(y)
        {
            is addCommand -> add(y)
            is addProductsCommand -> addProducts(y)
            is callIfCommand -> callIf(y)
            is foreverCommand -> forever(y)
            is ifCommand -> runIf(y)
            is moveCommand -> move(y)
            is repeatCommand -> runRepeat(y)
            else -> throw Exception("program formatted incorrectly")
        }
    }

    private fun runRepeat(y: repeatCommand) {

        if(y.loop==loop.While)
        {
            nOnLevel[level]=BigInt(0)
            level++
            while(CurrentPosition()!=BigInt(0))
            {
                for(j in y.body)
                    runCommand(j)
                nOnLevel[level-1]= nOnLevel[level-1]!! + 1
            }
            level--
        }
        else if(y.loop==loop.For)
        {
            val k=nOnLevel.getOrPut(level,{BigInt(0)})
            level++
            var i=BigInt(0)
            while(i<k)
            {
                for (j in y.body) {
                    runCommand(j)
                }
                i+=1
            }
            level--
            nOnLevel[level]=BigInt(0)
        }

    }

    private fun move(y: moveCommand) {
        i+=y.value
    }

    private fun runIf(y: ifCommand) {
        level++
        if(CurrentPosition()!=BigInt(0) && y.pos==If.NotZero)
        {
            for(j in y.body)
                runCommand(j)
        }
        else if(CurrentPosition()>0 && y.pos==If.Positive)
        {
            for(j in y.body)
                runCommand(j)
        }
        else if(CurrentPosition()<0 && y.pos==If.Negative)
        {
            for(j in y.body)
                runCommand(j)
        }
        level--
    }

    private fun forever(y: foreverCommand) {
        while(true)
            pass
    }

    private fun callIf(y: callIfCommand) {
        if(CurrentPosition()!=BigInt(0))
        {
            if(y.dynamic)
            {
                runFunction(list.getOrPut(i+y.value,{BigInt(0)}).toIntOrNull()?:-1)
            }
            else
            {
                runFunction(y.value)
            }
        }
    }

    private fun addProducts(y: addProductsCommand) {
        //level++
        if(y.div!=null)
        {
            if(CurrentPosition().abs().divides(y.div!!))
            {
                val div=CurrentPosition().abs()/(y.div!!)
                val origi=i
                i-=y.leftBy
                nOnLevel[level]=div
                for(x in y.list)
                {
                    list[i]=CurrentPosition()+div*x
                    i++
                }
                i=origi
            }
            else
            {
                while(true)
                    pass
            }
        }
        else
        {
            val div=nOnLevel.getOrPut(level,{BigInt(0)})
            val origi=i
            i-=y.leftBy
            for(x in y.list)
            {
                list[i]=CurrentPosition()+div*x
                i++
            }
            i=origi
        }
        //level--
    }

    private fun add(y: addCommand) {
        val origi=i
        i-=y.leftBy
        for(x in y.list)
        {
            list[i]=CurrentPosition()+x
            i++
        }
        i=origi
    }
    
    private fun CurrentPosition():BigInt
    {
        return list.getOrPut(i,{BigInt(0)})
    }
}