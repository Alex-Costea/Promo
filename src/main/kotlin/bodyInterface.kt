package PromoCompiler

internal interface bodyInterface : commandInterface {
    var body:MutableList<commandInterface>
    var level:Int
    override fun deep_copy(): bodyInterface
    fun deep_set_level(x:Int)
    {
        level=x
        body.forEach{if(it is bodyInterface) it.deep_set_level(x+1)}
    }
    fun get_calls(max:Int):MutableList<Int>?
    {
        var count=0
        var dynamic=false
        var calls=MutableList(max+1){0}
        for(x in body)
        {
            if(x is callIfCommand)
            {
                if(x.dynamic)
                {
                    return null
                }
                calls[x.value]+=1
            }
            if(x is bodyInterface)
            {
                val new_calls=x.get_calls(max)
                if(new_calls==null)
                    return null
                calls=calls.zip(x.get_calls(max)!!).map{it.first+it.second}.toMutableList()
            }
        }
        return calls
    }
    fun read_body(data:String)
    {
        if(data[0]!='(')
            throw Exception("Syntax error")
        if(data.last()!=')')
            throw Exception("Syntax error")
        if(data.length<2)
            throw Exception("Syntax error")
        var my_data=data.drop(1).dropLast(1)
        my_data=my_data.dropWhile{it!='('}
        var level=0
        var last_str=""
        body= mutableListOf()
        for(x in my_data)
        {
            if(x=='(')
                level++
            if(x==')')
                level--
            if(level<0)
                throw Exception("Syntax error")
            last_str += x
            if(x==')' && level==0)
            {
                var my_name=last_str.split(" ")[0].drop(1)
                if(my_name.last()==')')
                    my_name=my_name.dropLast(1)
                var commands=listOf(
                        addCommand(0),
                        addProductsCommand(mutableListOf(),0,0),
                        callIfCommand(0,false),
                        foreverCommand(),
                        funCommand(0),
                        ifCommand(0),
                        moveCommand(0),
                        repeatCommand(0,loop.While)
                )
                var new_command : commandInterface? = null
                for(x in commands)
                {
                    if(my_name==x.name)
                    {
                        new_command=x
                        x.read_data(last_str)
                        body.add(x)
                    }
                }
                if(new_command==null)
                    throw Exception("Syntax error")
                last_str=""
            }
        }
        if(last_str.isNotEmpty())
            throw Exception("Syntax error")
    }
}